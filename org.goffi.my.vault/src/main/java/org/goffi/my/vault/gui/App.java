/*
 * org.goffi.my.vault
 *
 * File Name: App.java
 *
 * Copyright 2017 Dzhem Riza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.goffi.my.vault.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.goffi.core.concurrent.ExecutorUtils;
import org.goffi.core.domainmodel.Trees;
import org.goffi.fx.core.AboutDialog;
import org.goffi.fx.core.AlertUtils;
import org.goffi.fx.core.FontPreferencesDialog;
import org.goffi.fx.core.SystemPropertiesDialog;
import org.goffi.my.vault.exceptions.MyVaultException;
import org.goffi.my.vault.gui.editors.Editor;
import org.goffi.my.vault.gui.editors.FolderEditor;
import org.goffi.my.vault.gui.editors.NoteEditor;
import org.goffi.my.vault.gui.editors.PasswordEditor;
import org.goffi.my.vault.gui.state.DisplayStateContext;
import org.goffi.my.vault.model.Document;
import org.goffi.my.vault.model.EncryptionData;
import org.goffi.my.vault.model.Folder;
import org.goffi.my.vault.model.Note;
import org.goffi.my.vault.model.Password;
import org.goffi.my.vault.model.VaultNode;
import org.goffi.my.vault.services.ClipboardService;
import org.goffi.my.vault.services.ExportService;
import org.goffi.my.vault.services.FileService;
import org.goffi.my.vault.services.PreferencesService;
import org.goffi.my.vault.services.UserPreferences;
import org.goffi.my.vault.services.exceptions.InvalidBase64TextInClipboardException;
import org.goffi.my.vault.services.exceptions.NoTextFoundInClipboardException;
import org.goffi.my.vault.services.impl.ClipboardServiceImpl;
import org.goffi.my.vault.services.impl.ExportServiceImpl;
import org.goffi.my.vault.services.impl.FileServiceImpl;
import org.goffi.my.vault.services.impl.PreferencesServiceImpl;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class App extends Application {

    private ResourceBundle resourceBundle;
    private Stage primaryStage;
    private final MigPane editorPane = new MigPane();
    private TreeView<VaultNode> documentTreeView;
    private ArrayList<Editor> editors = new ArrayList<>();
    private Editor activeEditor;
    private ClipboardService clipboardService = new ClipboardServiceImpl();
    private Document currentDocument;
    private ObjectMapper objectMapper = new ObjectMapper();
    private FileService fileService = new FileServiceImpl(objectMapper);
    private PreferencesService preferencesService =
            new PreferencesServiceImpl(objectMapper);
    private UserPreferences userPreferences;
    private SearchAllWindow searchAllWindow;
    private ExecutorService executorService =
            Executors.newSingleThreadExecutor();
    private ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private CustomFileChooser customFileChooser;
    private ExportService exportService = new ExportServiceImpl(objectMapper,
            clipboardService);
    private WindowActivityTracker windowActivityTracker =
            new WindowActivityTracker(scheduledExecutorService);
    private UUID scheduledAutoSaveIdleEventId;
    private UUID scheduledAutoLockIdleEventId;
    private MigPane mainPane;
    private SplitPane splitPane;
    private LockScreenView lockScreenView;
    /**
     * Controls menu enablement/disablement
     */
    private SimpleBooleanProperty screenLockedProperty =
            new SimpleBooleanProperty(false);
    private DisplayStateContext displayStateContext;

    public static void main(String[] arg) throws Exception {
        launch(arg);
    }

    private void createEditors() {
        editors.add(new FolderEditor(resourceBundle,
                this::doSelectVaultNodeInTheTree));
        editors.add(new NoteEditor(primaryStage, resourceBundle));
        editors.add(new PasswordEditor(primaryStage, resourceBundle,
                clipboardService));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(App::unexpectedError);

        this.primaryStage = primaryStage;

        resourceBundle = ResourceBundle
                .getBundle("org/goffi/my/vault/gui/i18n/ApplicationStrings");

        readProgramPreferences();
        createEditors();

        BorderPane root = new BorderPane();
        root.setTop(createMainMenu());
        root.setCenter(createMainPane());

        Scene scene = new Scene(root, Constants.MAIN_WINDOW_WIDTH,
                Constants.MAIN_WINDOW_HEIGHT);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            // Workaround for the annoying issue with Alt+Tab
            if (event.isAltDown()) {
                event.consume();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setOnHiding(event -> onHiding());

        InputStream resourceIcon = this.getClass().getModule()
                .getResourceAsStream(
                        "org/goffi/my/vault/gui/images/my_vault_icon.png");

        searchAllWindow = new SearchAllWindow(primaryStage, resourceBundle,
                () -> currentDocument,
                this::doSelectVaultNodeInTheTree,
                executorService, windowActivityTracker);
        updateSearchAllWindowPreferences();

        displayStateContext = new DisplayStateContext(primaryStage, mainPane,
                splitPane, lockScreenView, searchAllWindow,
                screenLockedProperty, resourceBundle);

        // update custom file chooser preferences
        getCustomFileChooser().setLastDirectory(
                userPreferences.getLastKnownOpenSaveDirectory());

        primaryStage.getIcons().add(new Image(resourceIcon));
        updateTitle();

        primaryStage.focusedProperty().addListener(
                (observable, oldValue, newValue) -> windowActivityTracker
                        .recordActivity(primaryStage, newValue));

        primaryStage.show();

        scheduleAutoSaveEventOnIdle();
        scheduleAutoLockOnIdle();
    }

    private void scheduleAutoLockOnIdle() {
        Runnable runnable = () -> {
            // We need to execute this inside the UI thread
            Platform.runLater(this::onAutoLock);

            // Re-schedule the event (it's safe to execute this in the
            // scheduledExecutor's thread pool)
            scheduleAutoLockOnIdle();
        };
        scheduledAutoLockIdleEventId =
                windowActivityTracker.scheduleEventOnIdle(
                        runnable,
                        userPreferences.getAutoLockInterval(),
                        TimeUnit.MINUTES);
    }

    private void onAutoLock() {
        if (userPreferences.getAutoLockInterval() <= 0) {
            // Run this code only if we have auto lock enabled
            return;
        }

        if (!screenLockedProperty.get() && canLockScreen()) {
            onLockUnlockScreen();
        }
    }

    private void onAutoSave() {
        if (userPreferences.getAutoSaveInterval() <= 0) {
            // Run this code only if we have auto save enabled
            return;
        }

        saveCurrentEditor();

        if (currentDocument.getEncryptionData() != null &&
                currentDocument.getFile() != null &&
                currentDocument.isDirty()) {

            fileService.save(currentDocument,
                    currentDocument.getEncryptionData().createEncoder(),
                    currentDocument.getFile());

            // After save we need to do some manual work to set the dirty
            // flag off and update the title of the window
            currentDocument.setDirty(false);
            updateTitle();
        }
    }

    private void scheduleAutoSaveEventOnIdle() {
        Runnable runnable = () -> {
            // We need to execute this inside the UI thread
            Platform.runLater(this::onAutoSave);

            // Re-schedule the event (it's safe to execute this in the
            // scheduledExecutor's thread pool)
            scheduleAutoSaveEventOnIdle();
        };
        scheduledAutoSaveIdleEventId =
                windowActivityTracker.scheduleEventOnIdle(
                        runnable,
                        userPreferences.getAutoSaveInterval(),
                        TimeUnit.MINUTES);
    }

    private void updateSearchAllWindowPreferences() {
        searchAllWindow.getTextArea().setFont(userPreferences.getFont());
        searchAllWindow.getTextArea().setWrapText(userPreferences.isWordWrap());
    }

    @Override
    public void stop() throws Exception {
        try {
            // Update the preferences with the latest data
            userPreferences.setLastKnownOpenSaveDirectory(
                    getCustomFileChooser().getLastDirectory());

            preferencesService.write(userPreferences);
            ExecutorUtils.shutdownAndAwaitTermination(executorService);
            ExecutorUtils.shutdownAndAwaitTermination(scheduledExecutorService);
        } catch (Exception e) {
            // Print the error to standard err
            e.printStackTrace(System.err);
        }

        super.stop(); // call the base class
    }

    private void readProgramPreferences() {
        userPreferences = preferencesService.read();
    }

    private void onHiding() {
        saveCurrentEditor();
        askAndSaveIfDirty();
    }

    private static void unexpectedError(Thread thread, Throwable throwable) {
        System.err.println(throwable.getMessage());
        throwable.printStackTrace(System.err);

        AlertUtils.error(null, "Unexpected error in thread with name=" +
                thread.getName() + ", id=" + thread.getId(), throwable);
    }

    private MenuBar createMainMenu() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu(resourceBundle.getString("menu.file"));
        MenuItem newFileMenu =
                new MenuItem(resourceBundle.getString("menu.file.new"));
        newFileMenu.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newFileMenu.setOnAction(a -> onNew());
        newFileMenu.disableProperty().bind(screenLockedProperty);
        MenuItem openFileMenu =
                new MenuItem(resourceBundle.getString("menu.file.open"));
        openFileMenu.setOnAction(a -> onOpen());
        openFileMenu.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openFileMenu.disableProperty().bind(screenLockedProperty);
        MenuItem openFromHistoryFileMenu = new MenuItem(
                resourceBundle.getString("menu.file.open.from.history"));
        openFromHistoryFileMenu.setOnAction(a -> onOpenFromHistory());
        openFromHistoryFileMenu.disableProperty().bind(screenLockedProperty);
        MenuItem saveFileMenu =
                new MenuItem(resourceBundle.getString("menu.file.save"));
        saveFileMenu.setOnAction(a -> onSave());
        saveFileMenu.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveFileMenu.disableProperty().bind(screenLockedProperty);
        MenuItem saveAsFileMenu =
                new MenuItem(resourceBundle.getString("menu.file.save.as"));
        saveAsFileMenu.setOnAction((a) -> onSaveAs());
        saveAsFileMenu.disableProperty().bind(screenLockedProperty);
        MenuItem exportToClipboardMenu = new MenuItem(
                resourceBundle.getString("menu.file.export.to.clipboard"));
        exportToClipboardMenu.setOnAction((a) -> onExportToClipboard());
        exportToClipboardMenu.disableProperty().bind(screenLockedProperty);
        MenuItem importFromClipboardMenu = new MenuItem(
                resourceBundle.getString("menu.file.import.from.clipboard"));
        importFromClipboardMenu.setOnAction((a) -> onImportFromClipboard());
        importFromClipboardMenu.disableProperty().bind(screenLockedProperty);
        MenuItem lockScreen = new MenuItem(
                resourceBundle.getString("menu.file.lock.screen"));
        lockScreen.setOnAction((a) -> onLockUnlockScreen());
        lockScreen.setAccelerator(
                KeyCombination.keyCombination("Ctrl+Shift+L"));
        MenuItem exit = new MenuItem(resourceBundle.getString(
                "menu.file.exit"));
        exit.setOnAction((e) -> primaryStage.close());
        file.getItems().addAll(
                newFileMenu, new SeparatorMenuItem(), openFileMenu,
                openFromHistoryFileMenu, new SeparatorMenuItem(),
                saveFileMenu, saveAsFileMenu, new SeparatorMenuItem(),
                exportToClipboardMenu, importFromClipboardMenu,
                new SeparatorMenuItem(), lockScreen, new SeparatorMenuItem(),
                exit);

        Menu document = new Menu(resourceBundle.getString("menu.document"));
        document.getItems().addAll(createDocumentMenuItems());

        Menu preferences = new Menu(
                resourceBundle.getString("menu.preferences"));
        CheckMenuItem wordWrap = new CheckMenuItem(
                resourceBundle.getString("menu.preferences.word.wrap"));
        wordWrap.setSelected(userPreferences.isWordWrap());
        wordWrap.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    userPreferences.setWordWrap(newValue);
                    onUpdatePreferences();
                });
        wordWrap.disableProperty().bind(screenLockedProperty);

        MenuItem font = new MenuItem(
                resourceBundle.getString("menu.preferences.font"));
        font.setOnAction(event -> onPreferencesFont());
        font.disableProperty().bind(screenLockedProperty);

        preferences.getItems().addAll(wordWrap, font,
                createAutoSavePreferencesMenu(),
                createAutoLockScreenPreferencesMenu());

        Menu help = new Menu(resourceBundle.getString("menu.help"));
        MenuItem systemProperties = new MenuItem(
                resourceBundle.getString("menu.help.system.properties"));
        systemProperties.setOnAction(a -> SystemPropertiesDialog
                .showAndWait(primaryStage, resourceBundle));
        MenuItem about = new MenuItem(
                resourceBundle.getString("menu.help.about"));
        about.setOnAction(
                event -> AboutDialog.showAndWait(primaryStage, resourceBundle));
        about.setAccelerator(KeyCombination.keyCombination("F1"));
        help.getItems().addAll(systemProperties,
                new SeparatorMenuItem(), about);

        menuBar.getMenus().addAll(file, document, preferences, help);
        return menuBar;
    }

    private Menu createAutoSavePreferencesMenu() {
        return createNumericMenu(resourceBundle.getString(
                "menu.preferences.auto.save"),
                userPreferences.getAutoSaveInterval(),
                this::onAutoSaveIntervalChange);
    }
    
    private Menu createAutoLockScreenPreferencesMenu() {
        return createNumericMenu(resourceBundle.getString(
                "menu.preferences.auto.lock.screen"),
                userPreferences.getAutoLockInterval(),
                this::onAutoLockIntervalChange);
    }

    private Menu createNumericMenu(String menuName, int currentNumericValue,
            IntConsumer onValueChange) {
        Menu numericMenu = new Menu(menuName);
        ToggleGroup numericMenuGroup = new ToggleGroup();

        final String[] itemsStrs = new String[]{
                resourceBundle.getString("menu.preferences.numeric.never"),
                resourceBundle.getString("menu.preferences.numeric.1"),
                resourceBundle.getString("menu.preferences.numeric.5"),
                resourceBundle.getString("menu.preferences.numeric.10"),
                resourceBundle.getString("menu.preferences.numeric.15"),
                resourceBundle.getString("menu.preferences.numeric.30"),
                resourceBundle.getString("menu.preferences.numeric.45"),
                resourceBundle.getString("menu.preferences.numeric.60"),
                resourceBundle.getString("menu.preferences.numeric.75"),
                resourceBundle.getString("menu.preferences.numeric.90"),
        };

        final int[] itemValues = new int[]{0, 1, 5, 10, 15, 30, 45, 60, 75, 90};

        int checkedItem = currentNumericValue;

        if (!IntStream.of(itemValues).anyMatch(
                x -> x == currentNumericValue)) {
            // The checked item is not recognized so we are going to use 0
            // (never auto save)
            checkedItem = 0;
        }

        for (int i = 0; i < itemValues.length; ++i) {
            RadioMenuItem radioMenuItem = new RadioMenuItem(itemsStrs[i]);
            radioMenuItem.setUserData(itemValues[i]);
            radioMenuItem.setToggleGroup(numericMenuGroup);
            radioMenuItem.disableProperty().bind(screenLockedProperty);
            numericMenu.getItems().add(radioMenuItem);

            if (checkedItem == itemValues[i]) {
                radioMenuItem.setSelected(true);
            }

            if (i == 0) {
                // Add separator after the first element
                numericMenu.getItems().add(new SeparatorMenuItem());
            }
        }

        numericMenuGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        onValueChange.accept((int) newValue.getUserData());
                    }
                });
        return numericMenu;
    }

    private boolean canLockScreen() {
        saveCurrentEditor();
        return !(currentDocument.isDirty() ||
                currentDocument.getFile() == null ||
                currentDocument.getEncryptionData() == null);
    }

    private void onLockUnlockScreen() {
        if (!canLockScreen()) {
            AlertUtils.info(primaryStage, resourceBundle.getString(
                    "error.lock.unlock.screen.dirty.document"));
            return;
        }

        displayStateContext.lockUnlockScreen(
                currentDocument.getEncryptionData());
    }

    private void onAutoSaveIntervalChange(int newAutoSaveInterval) {
        // Update the preferences
        userPreferences.setAutoSaveInterval(newAutoSaveInterval);
        // Cancel and re-schedule
        windowActivityTracker.cancelEvent(scheduledAutoSaveIdleEventId);
        scheduleAutoSaveEventOnIdle();
    }

    private void onAutoLockIntervalChange(int newAutoLockInterval) {
        // Update the preferences
        userPreferences.setAutoLockInterval(newAutoLockInterval);
        // Cancel and re-schedule
        windowActivityTracker.cancelEvent(scheduledAutoLockIdleEventId);
        scheduleAutoLockOnIdle();
    }

    private void onExportToClipboard() {
        readEncryptionData(true).ifPresent(encryptionData -> {
            AlertUtils.info(this.primaryStage, resourceBundle.getString(
                    "export.to.clipboard.complete.message.content"));

            exportService.exportToClipboard(this.currentDocument,
                    encryptionData.createEncoder());
        });
    }

    private void onImportFromClipboard() {
        try {
            saveCurrentEditor();
            askAndSaveIfDirty();

            readEncryptionData(false).ifPresent(encryptionData -> {
                this.currentDocument = exportService.importFromClipboard(
                        encryptionData.createDecoder());
                initializeTreeViewFromCurrentDocument();

                // Update document's meta data
                this.currentDocument.setEncryptionData(encryptionData);
                this.currentDocument.setDirty(true);
                updateTitle();

                // Do not update the file and history as we imported the data
                // from clipboard

                AlertUtils.info(this.primaryStage, resourceBundle.getString(
                        "import.from.clipboard.complete.message.content"));
            });
        } catch (NoTextFoundInClipboardException e) {
            AlertUtils.error(this.primaryStage, resourceBundle.getString(
                    "error.no.text.found.in.clipboard"));

        } catch (InvalidBase64TextInClipboardException e) {
            AlertUtils.error(this.primaryStage, resourceBundle.getString(
                    "error.invalid.base64.text.in.clipboard"));
        }
    }

    /**
     * Updates the current selected editor's preferences
     */
    private void onUpdatePreferences() {
        if (activeEditor != null) {
            activeEditor.updatePreferences(userPreferences);
        }
        // Update the font on the SearchAllWindow as well
        updateSearchAllWindowPreferences();
    }

    private void onPreferencesFont() {
        FontPreferencesDialog.showAndWait(primaryStage, resourceBundle,
                userPreferences.getFont().getSize(),
                userPreferences.getFont().getFamily()).ifPresent(font -> {
            userPreferences.setFont(font);
            onUpdatePreferences();
        });
    }

    private void updateTitle() {
        StringBuilder title = new StringBuilder();
        title.append(resourceBundle.getString("app.name"));
        title.append(" - [");
        if (this.currentDocument.getFile() == null) {
            title.append(resourceBundle.getString(
                    "document.title.file.untitled"));
        } else {
            title.append(this.currentDocument.getFile().getAbsolutePath());
        }
        title.append("]");
        if (this.currentDocument.isDirty()) {
            title.append(" - [");
            title.append(resourceBundle.getString(
                    "document.title.file.unsaved"));
            title.append("]");
        }

        primaryStage.setTitle(title.toString());
    }

    private void onOpen() {
        saveCurrentEditor();
        askAndSaveIfDirty();

        getCustomFileChooser().showOpenDialog().ifPresent(file ->
                readEncryptionData(false)
                        .ifPresent(encryptionData ->
                                openDocumentAsync(file, encryptionData)));
    }

    private void onOpenFromHistory() {
        HistoryDialog historyDialog = new HistoryDialog(primaryStage,
                resourceBundle);
        historyDialog.showAndWait(userPreferences
                .getFileHistory().getAllRecords()).ifPresent(file -> {
            saveCurrentEditor();
            askAndSaveIfDirty();

            readEncryptionData(false)
                    .ifPresent(encryptionData ->
                            openDocumentAsync(file, encryptionData));
        });
    }

    private void openDocumentAsync(File file, EncryptionData encryptionData) {
        LongRunningOperationWindow longRunningOperationWindow =
                new LongRunningOperationWindow(this.primaryStage,
                        this.resourceBundle);

        FileOpenService fileOpenService = new FileOpenService(file,
                encryptionData);
        fileOpenService.setExecutor(executorService);
        fileOpenService.setOnSucceeded(event -> {
            longRunningOperationWindow.hide();

            Platform.runLater(() -> {
                // Do the update in the JavaFX thread
                Document document = (Document) event.getSource().getValue();
                this.currentDocument = document;
                initializeTreeViewFromCurrentDocument();

                // Update document's meta data
                this.currentDocument.setEncryptionData(encryptionData);
                this.currentDocument.setFile(file);
                updateTitle();

                // Update the preferences
                userPreferences.getFileHistory().addFile(document.getFile());
            });
        });
        fileOpenService.setOnFailed(event -> {
            longRunningOperationWindow.hide();

            Platform.runLater(() -> {
                // Do the update in the JavaFX thread
                AlertUtils.error(primaryStage, event.getSource().getException()
                                .getMessage(),
                        event.getSource().getException());
            });
        });

        fileOpenService.start();
        longRunningOperationWindow.showAndWait();
    }

    private void askAndSaveIfDirty() {
        if (currentDocument.isDirty()) {
            // We are going to open a new document we should ask the client
            // if he wants to save the previous modified data
            if (AlertUtils.question(primaryStage, resourceBundle
                    .getString("confirmation.message.safe.previous.data"))) {
                onSave();
            }
        }
    }

    private void onNew() {
        saveCurrentEditor();
        askAndSaveIfDirty();

        currentDocument = createEmptyFolder();
        initializeTreeViewFromCurrentDocument();
        updateTitle();
    }

    private void saveCurrentEditor() {
        if (activeEditor != null) {
            // Save the current editor
            if (activeEditor.save()) {
                makeDocumentDirty();
            }
        }
    }

    private void onSave() {
        saveCurrentEditor();

        Optional<EncryptionData> encryptionDataOptional;

        if (currentDocument.getEncryptionData() == null) {
            encryptionDataOptional = readEncryptionData(true);
        } else {
            encryptionDataOptional =
                    Optional.of(currentDocument.getEncryptionData());
        }

        encryptionDataOptional.ifPresent(encryptionData -> {
            if (currentDocument.getFile() == null) {
                getCustomFileChooser().showSaveDialog().ifPresent(file ->
                        saveDocumentAsync(file, encryptionData));
            } else {
                saveDocumentAsync(currentDocument.getFile(), encryptionData);
            }
        });
    }

    private void onSaveAs() {
        saveCurrentEditor();

        readEncryptionData(true)
                .ifPresent((encryptionData -> getCustomFileChooser()
                        .showSaveDialog().ifPresent(file ->
                                saveDocumentAsync(file, encryptionData))));
    }

    private void saveDocumentAsync(File file, EncryptionData encryptionData) {
        LongRunningOperationWindow longRunningOperationWindow =
                new LongRunningOperationWindow(this.primaryStage,
                        this.resourceBundle);

        FileSaveService fileSaveService = new FileSaveService(currentDocument,
                file, encryptionData);
        fileSaveService.setExecutor(executorService);

        fileSaveService.setOnSucceeded(event -> {
            longRunningOperationWindow.hide();

            Platform.runLater(() -> {
                // On actual save we are updating the document meta fields
                currentDocument.setEncryptionData(encryptionData);
                currentDocument.setFile(file);
                currentDocument.setDirty(false);
                updateTitle();
                primaryStage.requestFocus();

                // Update the preferences
                userPreferences.getFileHistory().addFile(file);
            });
        });
        fileSaveService.setOnFailed(event -> {
            longRunningOperationWindow.hide();

            Platform.runLater(() -> {
                // Do the update in the JavaFX thread
                AlertUtils.error(primaryStage, event.getSource().getException()
                                .getMessage(),
                        event.getSource().getException());
                primaryStage.requestFocus();
            });
        });

        fileSaveService.start();
        longRunningOperationWindow.showAndWait();
    }

    private Optional<EncryptionData> readEncryptionData(
            boolean repeatPassword) {
        EncryptionDataReader reader =
                new EncryptionDataReader(primaryStage, resourceBundle);
        return reader.read(repeatPassword);
    }

    private CustomFileChooser getCustomFileChooser() {
        if (customFileChooser == null) {
            customFileChooser = new CustomFileChooser(primaryStage);
            customFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("My Vault Files",
                            "*.my.vault"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
        }
        return customFileChooser;
    }

    private Document createEmptyFolder() {
        Document document = new Document();
        // Create an empty document if we don't have such yet
        Folder folder = new Folder();
        folder.setName(
                resourceBundle.getString("document.root.default.name"));

        document.setRootFolder(folder);
        document.setDirty(false); // To stop having the annoying message on exit
        return document;
    }

    private Document getCurrentDocument() {
        if (currentDocument == null) {
            // Create an empty document if we don't have such yet
            currentDocument = createEmptyFolder();
        }

        return currentDocument;
    }

    private void initializeTreeViewFromCurrentDocument() {
        documentTreeView.setRoot(
                createTree(getCurrentDocument().getRootFolder()));
        documentTreeView.getSelectionModel().selectFirst(); // Select the root
    }

    private void traverseTree(TreeItem<VaultNode> parent) {
        for (VaultNode child : parent.getValue().getChildren()) {
            TreeItem<VaultNode> treeItemChild = new TreeItem<>(child);
            parent.getChildren().add(treeItemChild);
            traverseTree(treeItemChild);
        }
    }

    private TreeItem<VaultNode> createTree(Folder root) {
        TreeItem<VaultNode> rootUi = new TreeItem<>(root);
        traverseTree(rootUi);
        return rootUi;
    }

    private void onItemSelect(TreeItem<VaultNode> oldValue,
            TreeItem<VaultNode> newValue) {
        // Dispose the previous editor
        if (activeEditor != null) {
            if (activeEditor.stopEdit()) {
                makeDocumentDirty();
            }
        }

        if (newValue != null) {
            // Open the new editor
            activeEditor = getEditor(newValue.getValue());
            activeEditor.updatePreferences(userPreferences);
            activeEditor.startEdit(newValue.getValue(), editorPane);
        }
    }

    private Editor getEditor(VaultNode node) {
        for (Editor editor : editors) {
            if (editor.isSupports(node)) {
                return editor;
            }
        }
        throw new MyVaultException("Missing editor for: " + node);
    }

    private Pane createMainPane() {
        mainPane = new MigPane();

        documentTreeView = new TreeView<>();

        documentTreeView.setEditable(true);
        documentTreeView.setCellFactory(p -> new VaultTreeCellImpl());
        documentTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onItemSelect(
                        oldValue, newValue));
        documentTreeView.setContextMenu(createDocumentTreeContextMenu());

        initializeTreeViewFromCurrentDocument();

        splitPane = new SplitPane();
        splitPane.setDividerPosition(0, 0.20); // Left side is 20%
        splitPane.getItems().addAll(documentTreeView, this.editorPane);

        lockScreenView = new LockScreenView();

        mainPane.add(splitPane, "dock center");

        return mainPane;
    }

    private MenuItem[] createDocumentMenuItems() {
        Menu newMenu = new Menu(resourceBundle.getString("menu.document.new"));

        MenuItem newFolderMenuItem = new MenuItem(
                resourceBundle.getString("menu.document.new.folder"));
        newFolderMenuItem.setOnAction((action) -> onNewFolder());
        newFolderMenuItem
                .setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        newFolderMenuItem.disableProperty().bind(screenLockedProperty);
        MenuItem newNoteMenuItem = new MenuItem(
                resourceBundle.getString("menu.document.new.note"));
        newNoteMenuItem.setOnAction((action) -> onNewNote());
        newNoteMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
        newNoteMenuItem.disableProperty().bind(screenLockedProperty);
        MenuItem newPasswordMenuItem = new MenuItem(
                resourceBundle.getString("menu.document.new.password"));
        newPasswordMenuItem.setOnAction((action) -> onNewPassword());
        newPasswordMenuItem.setAccelerator(KeyCombination.keyCombination(
                "Ctrl+E"));
        newPasswordMenuItem.disableProperty().bind(screenLockedProperty);
        newMenu.getItems().addAll(newFolderMenuItem, newNoteMenuItem,
                newPasswordMenuItem);

        MenuItem deleteMenuItem =
                new MenuItem(resourceBundle.getString("menu.document.delete"));
        deleteMenuItem.setOnAction((a) -> onDeleteNode());
        deleteMenuItem.disableProperty().bind(screenLockedProperty);

        MenuItem cloneMenuItem =
                new MenuItem(resourceBundle.getString("menu.document.clone"));
        cloneMenuItem.setOnAction((a) -> onCloneNode());
        cloneMenuItem.disableProperty().bind(screenLockedProperty);

        MenuItem expandAllMenuItem = new MenuItem(
                resourceBundle.getString("menu.document.expand.all"));
        expandAllMenuItem.setOnAction(action -> onExpandAll());
        expandAllMenuItem.disableProperty().bind(screenLockedProperty);
        MenuItem collapseAllMenuItem = new MenuItem(
                resourceBundle.getString("menu.document.collapse.all"));
        collapseAllMenuItem.setOnAction(action -> onCollapseAll());
        collapseAllMenuItem.disableProperty().bind(screenLockedProperty);

        MenuItem searchAllMenuItem = new MenuItem(resourceBundle.getString(
                "menu.document.search.all"));
        searchAllMenuItem.setAccelerator(KeyCombination.keyCombination(
                "Ctrl+Shift+S"));
        searchAllMenuItem.setOnAction(a -> onSearchAll());
        searchAllMenuItem.disableProperty().bind(screenLockedProperty);

        MenuItem documentOverviewMenuItem = new MenuItem(
                resourceBundle.getString("menu.document.overview"));
        documentOverviewMenuItem.setOnAction(a -> onDocumentOverview());
        documentOverviewMenuItem.disableProperty().bind(screenLockedProperty);

        MenuItem documentTableView = new MenuItem(
                resourceBundle.getString("menu.document.table.view"));
        documentTableView.setOnAction(a -> onDocumentTableView());
        documentTableView.setAccelerator(KeyCombination.keyCombination(
                "Ctrl+Shift+R"));
        documentTableView.disableProperty().bind(screenLockedProperty);

        MenuItem moveToFolder = new MenuItem(
                resourceBundle.getString("menu.document.move.to.folder"));
        moveToFolder.setOnAction(a -> onMoveToFolder());
        moveToFolder.disableProperty().bind(screenLockedProperty);

        return new MenuItem[]{newMenu, deleteMenuItem, cloneMenuItem,
                new SeparatorMenuItem(), expandAllMenuItem, collapseAllMenuItem,
                new SeparatorMenuItem(), searchAllMenuItem,
                new SeparatorMenuItem(), documentOverviewMenuItem,
                documentTableView, new SeparatorMenuItem(), moveToFolder};
    }

    private void onMoveToFolder() {
        TreeItem<VaultNode> selectedItem =
                documentTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem == documentTreeView.getRoot()) {
                AlertUtils.error(primaryStage,
                        resourceBundle.getString("error.move.root"));
            } else {
                SelectFolderDialog selectFolderDialog = new SelectFolderDialog(
                        primaryStage, resourceBundle);

                VaultNode folderInTheScope;
                if (selectedItem.getValue().isContainer()) {
                    folderInTheScope = selectedItem.getValue();
                } else {
                    folderInTheScope = selectedItem.getParent().getValue();
                }

                selectFolderDialog.show(currentDocument, folderInTheScope)
                        .ifPresent((targetFolder) -> {
                            if (targetFolder != folderInTheScope) {
                                // source cell to move = selectedItem
                                // target cell to move = Cell(targetFolder)
                                moveDroppedNode(selectedItem,
                                        nodeToCell(targetFolder));
                            }
                        });
            }
        }
    }

    private TreeItem<VaultNode> nodeToCell(VaultNode node) {
        return TreeViews.findByValue(documentTreeView.getRoot(), node);
    }

    private void onDocumentTableView() {
        DocumentTableViewDialog dialog = new DocumentTableViewDialog(
                primaryStage, resourceBundle,
                this::doSelectVaultNodeInTheTree);
        dialog.show(currentDocument);
    }

    private void onDocumentOverview() {
        DocumentOverviewDialog documentOverviewDialog =
                new DocumentOverviewDialog(primaryStage, resourceBundle);
        documentOverviewDialog.show(this.currentDocument);
        updateTitle();
    }

    private void onSearchAll() {
        if (searchAllWindow.isShowing()) {
            searchAllWindow.hide();
        } else {
            searchAllWindow.show();
        }
    }

    private void expand(TreeItem<VaultNode> node, boolean expand) {
        if (node != null) {
            node.setExpanded(expand);
            for (TreeItem<VaultNode> children : node.getChildren()) {
                expand(children, expand);
            }
        }
    }

    private void onCloneNode() {
        TreeItem<VaultNode> selectedItem =
                documentTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem == documentTreeView.getRoot()) {
                AlertUtils.error(primaryStage,
                        resourceBundle.getString("error.clone.root"));
            } else {
                try {
                    // Clone the node and attach it to the parent
                    linkAndSelect(selectedItem.getValue().clone(),
                            this::getParentFolder);

                } catch (CloneNotSupportedException e) {
                    AlertUtils.error(primaryStage,
                            resourceBundle.getString("error.unable.to.clone"));
                }
            }
        }
    }

    private void onDeleteNode() {
        TreeItem<VaultNode> selectedItem =
                documentTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem == documentTreeView.getRoot()) {
                AlertUtils.error(primaryStage,
                        resourceBundle.getString("error.delete.root"));
            } else if (!selectedItem.isLeaf()) {
                // Unable to delete no leave item
                AlertUtils.error(primaryStage, resourceBundle
                        .getString("error.delete.nonemptyfolder"));
            } else {
                if (AlertUtils.confirm(primaryStage, resourceBundle
                        .getString("confirmation.message.delete"))) {
                    // Remove the item from the tree view
                    TreeItem<VaultNode> parent = selectedItem.getParent();
                    parent.getChildren().remove(selectedItem);

                    // Remove the node from the folder
                    VaultNode parentNode = parent.getValue();
                    parentNode.getChildren().remove(selectedItem.getValue());

                    makeDocumentDirty();
                }
            }
        }
    }

    private void makeDocumentDirty() {
        this.currentDocument.setDirty(true);
        updateTitle();
    }

    private void onNewNote() {
        Note newNote = new Note();
        newNote.setName(
                resourceBundle.getString("document.template.names.new.note"));

        linkAndSelect(newNote);
    }

    private void onNewPassword() {
        Password newPassword = new Password();
        newPassword.setName(resourceBundle
                .getString("document.template.names.new.Password"));

        linkAndSelect(newPassword);
    }

    private void onNewFolder() {
        Folder newFolder = new Folder();
        newFolder.setName(
                resourceBundle.getString("document.template.names.new.folder"));

        linkAndSelect(newFolder);
    }

    private void linkAndSelect(VaultNode newNode,
            Supplier<TreeItem<VaultNode>> folder) {
        TreeItem<VaultNode> firstFolder = folder.get();

        TreeItem<VaultNode> newTreeItem = new TreeItem<>(newNode);
        traverseTree(newTreeItem);

        // Link to the model and the tree
        firstFolder.getValue().getChildren().add(newNode);
        firstFolder.getChildren().add(newTreeItem);

        // Select the new added item
        documentTreeView.getSelectionModel().select(newTreeItem);

        // Now the document is dirty
        makeDocumentDirty();
    }

    private void linkAndSelect(VaultNode newNode) {
        linkAndSelect(newNode, this::getFirstFolder);
    }

    private TreeItem<VaultNode> getParentFolder() {
        TreeItem<VaultNode> firstFolder =
                documentTreeView.getSelectionModel().getSelectedItem();
        if (firstFolder != null) {
            firstFolder = firstFolder.getParent();
        } else {
            firstFolder = documentTreeView.getRoot();
        }
        return firstFolder;
    }

    private TreeItem<VaultNode> getFirstFolder() {
        TreeItem<VaultNode> firstFolder =
                documentTreeView.getSelectionModel().getSelectedItem();
        if (firstFolder != null) {
            if (!firstFolder.getValue().isContainer()) {
                firstFolder = firstFolder.getParent();
            }
        } else {
            firstFolder = documentTreeView.getRoot();
        }
        return firstFolder;
    }

    private void onExpandAll() {
        expand(this.documentTreeView.getRoot(), true);
    }

    private void onCollapseAll() {
        expand(this.documentTreeView.getRoot(), false);
        this.documentTreeView.getSelectionModel()
                .select(this.documentTreeView.getRoot());
    }

    private ContextMenu createDocumentTreeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(createDocumentMenuItems());
        return contextMenu;
    }

    private void onNodeNameChanged() {
        if (activeEditor != null) {
            // Save the existing data from the editor into the VaultNode
            activeEditor.save();
            // Refresh the editor based on the updated VaultNode name change
            activeEditor.refresh();
            // All this is safe because our Editor's are not able to modify
            // VaultNode.name properties. Name properties are modified only
            // from the TreeView control.
        }
        makeDocumentDirty();
    }

    private void doSelectVaultNodeInTheTree(VaultNode vaultNode) {
        TreeViews.traverseTreeView(
                documentTreeView.getRoot(), (treeItem) -> {
                    if (treeItem.getValue().equals(vaultNode)) {
                        // select the item
                        documentTreeView.getSelectionModel().select(treeItem);
                        return false; // stop the search further
                    }
                    return true; // search continues
                });
    }

    private void moveDroppedNode(TreeItem<VaultNode> source,
            TreeItem<VaultNode> target) {
        if (source == target) {
            // If we are dropping the node on top of the same node so this
            // should be a no op.
            return;
        }
        if (source == null || target == null) {
            // Ignore invalid drops
            return;
        }

        // if the source is a container we need to check does target is a sub
        // item of the source
        if (source.getValue().isContainer()) {
            boolean[] subItem = new boolean[1];
            subItem[0] = false;

            Trees.dfs(source, item -> {
                if (item.getValue().equals(target.getValue())) {
                    subItem[0] = true;
                    return false; // stop traversal
                }
                return true; // continue traversal
            }, TreeItem::getChildren);

            if (subItem[0]) {
                // Target node is a sub item of the source we cant continue
                return;
            }
        }

        // Unlink the source from the tree
        TreeItem<VaultNode> sourceParent = source.getParent();
        int sourceIndex = sourceParent.getChildren().indexOf(source);

        // Modify the view
        sourceParent.getChildren().remove(source);
        // Modify the model
        sourceParent.getValue().getChildren().remove(source.getValue());

        if (target.getValue().isContainer()) {
            // Modify the view
            target.getChildren().add(source);

            // Modify the model
            target.getValue().getChildren().add(source.getValue());
        } else {
            int targetIndex = target.getParent().getChildren()
                    .indexOf(target);

            if (sourceParent == target.getParent() &&
                    sourceIndex <= targetIndex) {
                // If they have the same parent and sourceIndex <= targetIndex
                // the targetIndex should be +1
                targetIndex++;
            }

            // Modify the view
            target.getParent().getChildren().add(targetIndex,
                    source);

            // Modify the model
            target.getParent().getValue().getChildren().add(
                    targetIndex, source.getValue());
        }

        // The document is dirty now we need to update the title
        makeDocumentDirty();
    }

    private final class VaultTreeCellImpl extends TreeCell<VaultNode> {

        private TextField textField;

        public VaultTreeCellImpl() {
            setOnDragDetected(event -> {
                VaultTreeCellImpl currentItem =
                        (VaultTreeCellImpl) event.getSource();

                if (currentItem.getItem() == currentDocument.getRootFolder()) {
                    // Root stays where it is!
                    event.consume();
                    return;
                }

                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(currentItem.getItem()
                        .getId().toString());
                dragboard.setContent(clipboardContent);
                event.consume();
            });
            setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            });
            setOnDragDropped(event -> {
                VaultTreeCellImpl gestureSource =
                        (VaultTreeCellImpl) event.getGestureSource();
                VaultTreeCellImpl gestureTarget =
                        (VaultTreeCellImpl) event.getGestureTarget();

                moveDroppedNode(gestureSource.getTreeItem(),
                        gestureTarget.getTreeItem());
                event.consume();
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            } else {
                textField.setText(getDisplayString());
            }

            setText(null);
            setGraphic(textField);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getDisplayString());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(VaultNode item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getDisplayString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getDisplayString());
                    setGraphic(getTreeItem().getGraphic());
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getDisplayString());

            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    String newName = textField.getText();
                    // Remove unwanted spaces from the new name
                    newName = newName.trim();

                    if (newName.equals("")) {
                        // We can't commit empty text as a name
                        cancelEdit();
                    } else {
                        VaultNode node = getItem();

                        node.setName(newName);
                        commitEdit(node);

                        // The new name is now updated so we need to update
                        // the current editor if possible
                        onNodeNameChanged();
                    }
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        private String getDisplayString() {
            VaultNode node = getItem();
            return node == null ? "" : node.getName();
        }
    }

    private class FileSaveService extends Service<Void> {

        private final Document document;
        private final File file;
        private final EncryptionData encryptionData;

        public FileSaveService(Document document, File file,
                EncryptionData encryptionData) {
            this.document = document;
            this.file = file;
            this.encryptionData = encryptionData;
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    fileService.save(document, encryptionData.createEncoder(),
                            file);
                    return null;
                }
            };
        }
    }

    private class FileOpenService extends Service<Document> {

        private final File file;
        private final EncryptionData encryptionData;

        public FileOpenService(File file, EncryptionData encryptionData) {
            this.file = file;
            this.encryptionData = encryptionData;
        }

        @Override
        protected Task<Document> createTask() {
            return new Task<>() {
                @Override
                protected Document call() throws Exception {
                    return fileService.open(file,
                            encryptionData.createDecoder());
                }
            };
        }
    }
}
