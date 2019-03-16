/*
 * org.goffi.moffi
 *
 * File Name: TextEditorMainFrame.java
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
package org.goffi.moffi.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.text.SimpleTextMatcher;
import org.goffi.moffi.domain.model.services.TextFileManagerService;
import org.goffi.moffi.domain.model.services.impl.BcTextFileManagerService;
import org.goffi.moffi.gui.actions.CompositeTimedAction;
import org.goffi.moffi.gui.events.GuiEventListener;
import org.goffi.moffi.gui.events.MoffiEventProcessor;
import org.goffi.moffi.gui.events.mode.ShadowModeEnabledEvent;
import org.goffi.moffi.gui.events.mode.TextModeEnabledEvent;
import org.goffi.moffi.gui.events.text.FindNextEvent;
import org.goffi.moffi.gui.events.text.ReplaceAllEvent;
import org.goffi.moffi.gui.events.text.ReplaceEvent;
import org.goffi.moffi.gui.state.MoffiDisplayStateContext;
import org.goffi.moffi.gui.text.JTextAreaUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TextEditorMainFrame extends JFrame implements WindowListener {

    private static final Dimension WINDOW_PREFERRED_SIZE = new Dimension(620,
            420);
    private static final Dimension FILE_CHOOSER_PREFERRED_SIZE = new Dimension(
            720, 610);
    private static final int SHADOW_MODE_DEFAULT_LOCK_TIMEOUT_MINUTES = 5; // in
    // minutes
    private static final Logger LOG = LogManager
            .getLogger(TextEditorMainFrame.class);

    private final ResourceBundle resourceBundle;
    private JTextArea textArea;
    private final JFileChooser fileChooser = new JFileChooser(
            System.getProperty("user.dir"));
    private DocumentMetadata documentMetadata = DocumentMetadata.emptyDoc();
    private final UndoManager undoManager = new UndoManager();
    private final TextFileManagerService textFileManagerService = new BcTextFileManagerService();
    private Timer globalTaskTimer;
    private ShadowModePanel shadowModePanel;
    private JScrollPane textAreaScrollPane;
    private boolean shadowMode = false;
    private final CompositeTimedAction compositeTimedAction = new CompositeTimedAction();
    private final MoffiEventProcessor eventProcessor = new MoffiEventProcessor();
    private final MoffiDisplayStateContext displayStateContext = new MoffiDisplayStateContext(
            eventProcessor);
    private FindReplaceDialog findReplaceDialog;

    public TextEditorMainFrame() {
        resourceBundle = ResourceBundle
                .getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings");
        initializeLayoutAndComponents();
    }

    private void dispatchCloseEvent() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private Font getTextAreaFont() {
        return GuiConstants.DEFAULT_FONT;
    }

    @SuppressWarnings("deprecation")
    private void initializeLayoutAndComponents() {
        TextEditorMainFrame theTextEditorMainFrame = this;

        JMenuBar menuBar = new JMenuBar();

        // File
        JMenu fileMenu = new JMenu(
                resourceBundle.getString("mainframe.menu.file"));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.file.new"),
                KeyEvent.VK_N);
        newMenuItem.addActionListener((l) -> onNew());
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(newMenuItem);

        JMenuItem openMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.file.open"),
                KeyEvent.VK_O);
        openMenuItem.addActionListener((l) -> onOpen());
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(openMenuItem);

        fileMenu.addSeparator();

        JMenuItem saveMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.file.save"),
                KeyEvent.VK_S);
        saveMenuItem.addActionListener((l) -> onSave());
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.file.saveas"),
                KeyEvent.VK_A);
        saveAsMenuItem.addActionListener((l) -> onSaveAs());
        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.file.exit"),
                KeyEvent.VK_X);
        exitMenuItem.addActionListener(
                (l) -> theTextEditorMainFrame.dispatchCloseEvent());
        fileMenu.add(exitMenuItem);

        // Edit
        JMenu editMenu = new JMenu(
                resourceBundle.getString("mainframe.menu.edit"));
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem undoMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.undo"));
        undoMenuItem.addActionListener((l) -> onUndo());
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(undoMenuItem);

        JMenuItem redoMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.redo"));
        redoMenuItem.addActionListener((l) -> onRedo());
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(redoMenuItem);

        editMenu.addSeparator();

        JMenuItem cutMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.cut"));
        cutMenuItem.addActionListener((l) -> onCut());
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(cutMenuItem);

        JMenuItem copyMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.copy"));
        copyMenuItem.addActionListener((l) -> onCopy());
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(copyMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.paste"));
        pasteMenuItem.addActionListener((l) -> onPaste());
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(pasteMenuItem);

        editMenu.addSeparator();

        JMenuItem selectAllMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.selectall"));
        selectAllMenuItem.setMnemonic(KeyEvent.VK_A);
        selectAllMenuItem.addActionListener((l) -> onSelectAll());
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(selectAllMenuItem);

        editMenu.addSeparator();

        JMenuItem findReplaceMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.edit.findreplace"));
        findReplaceMenuItem.setMnemonic(KeyEvent.VK_F);
        findReplaceMenuItem.addActionListener((l) -> onFindReplace());
        findReplaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(findReplaceMenuItem);

        // View
        JMenu viewMenu = new JMenu(
                resourceBundle.getString("mainframe.menu.view"));
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JCheckBoxMenuItem checkBoxMenuItemWrodWrap = new JCheckBoxMenuItem(
                resourceBundle.getString("mainframe.menu.view.wordwrap"));
        checkBoxMenuItemWrodWrap.setMnemonic(KeyEvent.VK_W);
        checkBoxMenuItemWrodWrap.addActionListener((l) -> onWordWrap());
        // See -> Post JTextArea create
        viewMenu.add(checkBoxMenuItemWrodWrap);

        // Theme
        JMenu themeMenu = new JMenu(
                resourceBundle.getString("mainframe.menu.theme"));
        themeMenu.setMnemonic(KeyEvent.VK_T);

        ButtonGroup buttonGroupLookAndFeel = new ButtonGroup();
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager
                .getInstalledLookAndFeels()) {
            JRadioButtonMenuItem themeMenuItem = new JRadioButtonMenuItem(
                    lookAndFeelInfo.getName());
            themeMenuItem.addActionListener((l) -> {
                try {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    // Replace with event eventually
                    SwingUtilities.updateComponentTreeUI(fileChooser);
                    SwingUtilities
                            .updateComponentTreeUI(theTextEditorMainFrame);
                    if (isShadowModeEnabled()) {
                        SwingUtilities
                                .updateComponentTreeUI(textAreaScrollPane);
                    } else {
                        SwingUtilities.updateComponentTreeUI(shadowModePanel);
                    }
                    SwingUtilities.updateComponentTreeUI(findReplaceDialog);
                } catch (Exception e) {
                    LOG.error("Error on selecting theme: " + e.getMessage(), e);
                    ExceptionDialog.of(e);
                }
            });
            boolean currentSelection = UIManager.getLookAndFeel().getClass()
                    .getCanonicalName().equals(lookAndFeelInfo.getClassName());
            themeMenuItem.setSelected(currentSelection);
            buttonGroupLookAndFeel.add(themeMenuItem);

            themeMenu.add(themeMenuItem);
        }

        // Shadow Mode
        JMenu shadowModeMenu = new JMenu(
                resourceBundle.getString("mainframe.menu.shadowmode"));
        shadowModeMenu.setMnemonic(KeyEvent.VK_S);

        JMenuItem lockUnlockMenuItem = new JMenuItem(resourceBundle
                .getString("mainframe.menu.shadowmode.lockunlock"));
        lockUnlockMenuItem.addActionListener((l) -> onShadowModeLockUnlock());
        lockUnlockMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_DOWN_MASK));
        shadowModeMenu.add(lockUnlockMenuItem);

        // Help
        JMenu helpMenu = new JMenu(
                resourceBundle.getString("mainframe.menu.help"));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutMenuItem = new JMenuItem(
                resourceBundle.getString("mainframe.menu.help.about"),
                KeyEvent.VK_A);
        aboutMenuItem.addActionListener((l) -> onAbout());
        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(themeMenu);
        menuBar.add(shadowModeMenu);
        menuBar.add(helpMenu);

        // Shadow mode panel
        shadowModePanel = new ShadowModePanel();

        // Text area
        textArea = new JTextArea();
        textArea.setFont(getTextAreaFont());
        textAreaScrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Setup undo manager
        textArea.getDocument()
                .addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        textArea.getActionMap().put("Undo", new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUndo();
            }
        });
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        textArea.getActionMap().put("Redo", new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRedo();
            }
        });
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control R"), "Redo");
        // Mark document metadata dirty on modification
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                documentMetadata.update();
                updateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentMetadata.update();
                updateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentMetadata.update();
                updateTitle();
            }
        });

        // Post JTextArea create
        checkBoxMenuItemWrodWrap.setSelected(textArea.getLineWrap());

        // File Chooser
        FileNameExtensionFilter filterMoffiFiles = new FileNameExtensionFilter(
                resourceBundle.getString("file.dialog.extension.filter"),
                "moffi");
        fileChooser.addChoosableFileFilter(filterMoffiFiles);
        fileChooser.setFileFilter(filterMoffiFiles);
        fileChooser.setPreferredSize(FILE_CHOOSER_PREFERRED_SIZE);

        // Setup timed actions
        compositeTimedAction.add(() -> {
            if (documentMetadata.isEligibleForAutoSave()) {
                onSave();
                // Update title to indicate to user that document is auto-saved
                this.setTitle(this.getTitle() + " - ["
                        + resourceBundle.getString("mainframe.autosaved.title")
                        + "]");
            }
        });
        compositeTimedAction.add(() -> {
            if (documentMetadata.isIdleForAtLeast(
                    getShadowModeLockTimeoutInMinutes(), TimeUnit.MINUTES)) {
                if (!isShadowModeEnabled()) {
                    onShadowModeLockUnlock();
                }
            }
        });

        // Setup the global timer
        globalTaskTimer = new Timer((int) TimeUnit.MINUTES.toMillis(1),
                e -> onTimer());
        globalTaskTimer.start();

        // Display state
        displayStateContext.setDocumentMetadata(this.documentMetadata);

        // Make this consumer
        Consumer<Boolean> setMenuItemsEnabled = (enabled) -> {
            checkBoxMenuItemWrodWrap.setEnabled(enabled);
            newMenuItem.setEnabled(enabled);
            openMenuItem.setEnabled(enabled);
            saveMenuItem.setEnabled(enabled);
            saveAsMenuItem.setEnabled(enabled);
            undoMenuItem.setEnabled(enabled);
            redoMenuItem.setEnabled(enabled);
            cutMenuItem.setEnabled(enabled);
            copyMenuItem.setEnabled(enabled);
            pasteMenuItem.setEnabled(enabled);
            selectAllMenuItem.setEnabled(enabled);
            findReplaceMenuItem.setEnabled(enabled);
        };

        // Event listeners
        eventProcessor.add((TextModeEnabledEvent e) -> {
            this.remove(shadowModePanel);
            this.add(textAreaScrollPane, BorderLayout.CENTER);
            this.textArea.requestFocus();
            this.documentMetadata.updateTimestamp();

            // Enable menu items
            setMenuItemsEnabled.accept(true);

            shadowMode = false;

            this.revalidate();
            this.repaint();
        });

        eventProcessor.add((ShadowModeEnabledEvent e) -> {
            this.remove(textAreaScrollPane);
            this.add(shadowModePanel, BorderLayout.CENTER);

            // Disable menu items
            setMenuItemsEnabled.accept(false);

            shadowMode = true;

            this.revalidate();
            this.repaint();
        });

        eventProcessor.add((GuiEventListener<FindNextEvent>) this::onFindNext);
        eventProcessor
                .add((GuiEventListener<ReplaceAllEvent>) this::onReplaceAll);
        eventProcessor.add((GuiEventListener<ReplaceEvent>) this::onReplace);

        // Find replace dialog
        findReplaceDialog = new FindReplaceDialog(this, eventProcessor);
        findReplaceDialog.setLocationRelativeTo(this);

        // Frame specific
        try {
            InputStream inputStream = this.getClass().getModule()
                    .getResourceAsStream("org/goffi/moffi/gui/images/moffi_icon.png");

            if (inputStream != null) {
                BufferedImage appIcon = ImageIO.read(inputStream);
                this.setIconImage(appIcon);
            }
        } catch (Exception e) {
            LOG.error("Unable to load window icon: " + e.getMessage(), e);
            ExceptionDialog.of(e);
        }

        this.add(textAreaScrollPane, BorderLayout.CENTER);
        this.setJMenuBar(menuBar);
        updateTitle();
        this.addWindowListener(this);

        this.setPreferredSize(WINDOW_PREFERRED_SIZE);
        this.setMinimumSize(GuiConstants.WINDOW_MIN_DIMENSIONS);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void updateTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append(resourceBundle.getString("app.name"));
        sb.append(" - [");
        sb.append(documentMetadata.getDocumentTitle());
        sb.append("]");
        if (documentMetadata.isDirty()) {
            sb.append(" - [");
            sb.append(resourceBundle.getString("mainframe.unsaved.title"));
            sb.append("]");
        }
        if (!this.getTitle().equals(sb.toString())) {
            this.setTitle(sb.toString());
        }
    }

    private int getShadowModeLockTimeoutInMinutes() {
        return SHADOW_MODE_DEFAULT_LOCK_TIMEOUT_MINUTES;
    }

    public void showTheEditor() {
        // Show
        this.pack();
        this.setLocationRelativeTo(null); // To the center of the screen
        this.setVisible(true);
    }

    private void onFindReplace() {
        findReplaceDialog.setVisible(true);
    }

    private void onAbout() {
        AboutDialog.showIt(this);
    }

    private void onSave() {
        try {
            if (documentMetadata.isBrainNewDocument()) {
                onSaveAs();
            } else {
                if (documentMetadata.isDirty()) {
                    // Save to file only if it's changed

                    textFileManagerService.save(textArea.getText(),
                            documentMetadata.getFile(),
                            documentMetadata.getUserEncryptionKey());

                    // Update the UI
                    this.documentMetadata.setDirty(false);
                    updateTitle();
                }
            }
        } catch (Exception domainException) {
            LOG.error(domainException.getMessage(), domainException);
            ExceptionDialog.of(this, domainException);
        }
    }

    private void onSaveAs() {
        try {
            // Save As requires new password, new pin and new memorable word
            CryptoInfoDialog dialog = new CryptoInfoDialog(true);
            dialog.setTitle(resourceBundle
                    .getString("crypto.information.dialog.title"));
            dialog.showDialog(this);
            if (!dialog.isOkPressed()) {
                // User cancel this operation
                return;
            }

            if (fileChooser
                    .showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                // Read dialog data
                textFileManagerService.save(textArea.getText(),
                        fileChooser.getSelectedFile().toPath(),
                        dialog.getUserEncryptionKey());

                // Update the UI
                this.documentMetadata = new DocumentMetadata(
                        fileChooser.getSelectedFile().toPath(),
                        dialog.getUserEncryptionKey());
                // Replace with event
                displayStateContext.setDocumentMetadata(this.documentMetadata);
                updateTitle();
            }
        } catch (Exception domainException) {
            LOG.error(domainException.getMessage(), domainException);
            ExceptionDialog.of(this, domainException);
        }
    }

    /**
     * Used when user click on new or open. This method ask the user to confirm
     * if the existing document needs to be saved or not.
     *
     * @return {@code true} if document was processed successful and the
     * {@code false} in case user cancel the operation. {@code false}
     * will cancel the pending operations such as open or new as well.
     */
    private boolean tryToProcessPreviousDocument() {
        boolean result = true;

        if (documentMetadata.isDirty()) {
            // Previous document is not saved
            int option = JOptionPane.showConfirmDialog(this,
                    MessageFormat.format(
                            resourceBundle
                                    .getString("message.filenotsaved.question"),
                            documentMetadata.getDocumentTitle()),
                    resourceBundle.getString("message.question"),
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                onSave();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                // In case of cancel we do nothing
                result = false;
            }
        }

        return result;
    }

    private void onOpen() {
        try {
            if (tryToProcessPreviousDocument()) {
                if (fileChooser
                        .showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    // Ask for encryption information
                    CryptoInfoDialog dialog = new CryptoInfoDialog(false);
                    dialog.setTitle(resourceBundle
                            .getString("crypto.information.dialog.title"));
                    dialog.showDialog(this);
                    if (!dialog.isOkPressed()) {
                        // User cancel this operation
                        return;
                    }

                    // Read file path
                    String fileContent = textFileManagerService.open(
                            fileChooser.getSelectedFile().toPath(),
                            dialog.getUserEncryptionKey());

                    // Update UI
                    textArea.setText(fileContent);
                    textArea.setCaretPosition(0);
                    documentMetadata = new DocumentMetadata(
                            fileChooser.getSelectedFile().toPath(),
                            dialog.getUserEncryptionKey());
                    // Replace with event
                    displayStateContext
                            .setDocumentMetadata(this.documentMetadata);
                    undoManager.discardAllEdits();
                    updateTitle();
                }
            }
        } catch (Exception domainException) {
            LOG.error(domainException.getMessage(), domainException);
            ExceptionDialog.of(this, domainException);
        }
    }

    private void onNew() {
        if (tryToProcessPreviousDocument()) {
            textArea.setText(""); // clear the text
            undoManager.discardAllEdits();
            documentMetadata = DocumentMetadata.emptyDoc();
            // Replace with event
            displayStateContext.setDocumentMetadata(this.documentMetadata);
            updateTitle();
        }
    }

    private void onUndo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void onRedo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void onWordWrap() {
        textArea.setWrapStyleWord(!textArea.getWrapStyleWord());
        textArea.setLineWrap(!textArea.getLineWrap());
    }

    private boolean isShadowModeEnabled() {
        return shadowMode;
    }

    private void onCut() {
        textArea.cut();
    }

    private void onCopy() {
        textArea.copy();
    }

    private void onPaste() {
        textArea.paste();
    }

    private void onSelectAll() {
        textArea.selectAll();
    }

    private void onTimer() {
        compositeTimedAction.action();
    }

    private void onShadowModeLockUnlock() {
        displayStateContext.lockUnlockScreen();
    }

    private void onFindNext(FindNextEvent findNextEvent) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleTextMatcher matcher = new SimpleTextMatcher(
                        findNextEvent.isMatchCase(),
                        findNextEvent.getSearchFor());

                JTextAreaUtils.findAndSelectNext(textArea, matcher);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    private void onReplaceAll(ReplaceAllEvent replaceAllEvent) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleTextMatcher matcher = new SimpleTextMatcher(
                        replaceAllEvent.isMatchCase(),
                        replaceAllEvent.getSearchFor());

                JTextAreaUtils.replaceAll(textArea, matcher,
                        replaceAllEvent.getReplaceWith());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    private void onReplace(ReplaceEvent replaceEvent) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleTextMatcher matcher = new SimpleTextMatcher(
                        replaceEvent.isMatchCase(),
                        replaceEvent.getSearchFor());

                JTextAreaUtils.replace(textArea, matcher,
                        replaceEvent.getReplaceWith());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (tryToProcessPreviousDocument()) {
            globalTaskTimer.stop(); // Stop the globalTaskTimer

            if (this.isDisplayable()) {
                this.dispose();
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // Not implemented
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Not implemented
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // Not implemented
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // Not implemented
    }

    @Override
    public void windowActivated(WindowEvent e) {
        // Not implemented
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // Not implemented
    }
}
