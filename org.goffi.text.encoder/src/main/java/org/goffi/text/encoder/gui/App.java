/*
 * org.goffi.text.encoder
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
package org.goffi.text.encoder.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.goffi.core.concurrent.ExecutorUtils;
import org.goffi.core.domainmodel.exceptions.RepeatedPasswordDontMatchException;
import org.goffi.core.domainmodel.exceptions.WeakPasswordException;
import org.goffi.core.domainmodel.exceptions.ZeroPinException;
import org.goffi.fx.core.AboutDialog;
import org.goffi.fx.core.FontPreferencesDialog;
import org.goffi.fx.core.SystemPropertiesDialog;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    public static final long DEFAULT_TIMER_IN_MILLISECONDS = 2500;
    private static final int MIN_WIDTH = 520;
    private static final int MIN_HEIGHT = 520;

    private final ExecutorService backgroundTasks = Executors
            .newSingleThreadExecutor();
    private ResourceBundle resourceBundle;
    private Stage primaryStage;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private PasswordField passwordField;
    private PasswordField repeatedPasswordField;
    private PasswordField memorableWordField;
    private PasswordField pinField;
    private ChoiceBox<ChoiceBoxMode> choiceBoxModes;
    private CheckBox autoEncodeDecode;
    private CheckBox useCompression;
    private boolean isInputDirty = false;

    public static void main(String[] arg) throws Exception {
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        resourceBundle = ResourceBundle.getBundle(
                "org/goffi/text/encoder/gui/i18n/ApplicationStrings");

        Pane centerPane = createMainPane();

        BorderPane root = new BorderPane();
        root.setTop(createMainMenu());
        root.setCenter(centerPane);

        Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle(resourceBundle.getString("app.name"));

        InputStream resourceIcon = this.getClass().getModule()
                .getResourceAsStream(
                        "org/goffi/text/encoder/gui/images/text_encoder_icon" +
                                ".png");

        primaryStage.getIcons().add(new Image(resourceIcon));
        primaryStage.show();

        setupTickTimer();
    }

    private void setupTickTimer() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(DEFAULT_TIMER_IN_MILLISECONDS),
                        ae -> onTimer()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @Override
    public void stop() throws Exception {
        ExecutorUtils.shutdownAndAwaitTermination(backgroundTasks);
        super.stop();
    }

    private MenuBar createMainMenu() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu(resourceBundle.getString("menu.file"));

        MenuItem exit = new MenuItem(
                resourceBundle.getString("menu.file.exit"));
        exit.setOnAction(this::onExit);
        file.getItems().add(exit);

        Menu preferences = new Menu(
                resourceBundle.getString("menu.preferences"));
        CheckMenuItem wordWrap = new CheckMenuItem(
                resourceBundle.getString("menu.preferences.word.wrap"));
        wordWrap.setSelected(inputTextArea.isWrapText());
        wordWrap.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    inputTextArea.setWrapText(newValue);
                    outputTextArea.setWrapText(newValue);
                });

        MenuItem font = new MenuItem(
                resourceBundle.getString("menu.preferences.font"));
        font.setOnAction(this::onSelectFont);

        preferences.getItems().addAll(wordWrap, font);

        Menu help = new Menu(resourceBundle.getString("menu.help"));
        MenuItem systemProperties = new MenuItem(
                resourceBundle.getString("menu.help.system.properties"));
        systemProperties.setOnAction(a -> SystemPropertiesDialog
                .showAndWait(primaryStage, resourceBundle));
        MenuItem about = new MenuItem(
                resourceBundle.getString("menu.help.about"));
        about.setOnAction(
                event -> AboutDialog.showAndWait(primaryStage, resourceBundle));
        help.getItems().addAll(systemProperties, about);

        menuBar.getMenus().addAll(file, preferences, help);

        return menuBar;
    }

    private Pane createMainPane() {
        MigPane pane = new MigPane();
        pane.add(createTopPane(), "dock north");
        pane.add(createCenterPane(), "dock center");
        return pane;
    }

    private Pane createTopPane() {
        MigPane top = new MigPane();

        top.add(new Label(
                resourceBundle.getString("crypto.information.top" +
                        ".label")), "wrap,span");

        top.add(new Label(
                resourceBundle.getString("crypto.information.password.label")));
        passwordField = new PasswordField();
        top.add(passwordField, "wrap,growx,pushx");

        top.add(new Label(resourceBundle
                .getString("crypto.information.repeat.password.label")));
        repeatedPasswordField = new PasswordField();
        top.add(repeatedPasswordField, "wrap,growx,pushx");

        top.add(new Label(resourceBundle
                .getString("crypto.information.memorable.word.label")));
        memorableWordField = new PasswordField();
        top.add(memorableWordField, "wrap,growx,pushx");

        top.add(new Label(
                resourceBundle.getString("crypto.information.pin.label")));
        pinField = new PasswordField();
        top.add(pinField, "growx,pushx");

        return top;
    }

    private Pane createCenterPane() {
        MigPane center = new MigPane();
        inputTextArea = new TextArea();
        inputTextArea.textProperty().addListener(this::onInputTextChanged);
        center.add(inputTextArea, "span,pushy,growy,pushx,growx,wrap");
        center.add(createCenterBar(), "growx,pushx,wrap");
        center.add(createResultPane(), "span,pushy,growy,pushx,growx");
        return center;
    }

    private Pane createResultPane() {
        MigPane result = new MigPane();

        result.add(new Label(resourceBundle.getString("result.label")),
                "dock north");

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        result.add(outputTextArea, "dock center");

        return result;
    }

    private class ChoiceBoxMode {
        private final String displayName;
        private final TextTransformer textTransformer;

        public ChoiceBoxMode(String displayName,
                TextTransformer textTransformer) {
            this.displayName = displayName;
            this.textTransformer = textTransformer;
        }

        public TextTransformer getTextTransformer() {
            return textTransformer;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private Pane createCenterBar() {
        MigPane bar = new MigPane();

        bar.add(new Label(resourceBundle.getString("bar.mode.label")));

        choiceBoxModes = new ChoiceBox<>();
        choiceBoxModes.getItems().addAll(
                new ChoiceBoxMode(resourceBundle
                        .getString("bar.mode.encode.gcm.aes.choice"),
                        new GcmAesEncoderTextTransformer()),
                new ChoiceBoxMode(resourceBundle
                        .getString("bar.mode.decode.gcm.aes.choice"),
                        new GcmAesDecoderTextTransformer()),
                new ChoiceBoxMode(resourceBundle
                        .getString("bar.mode.encode.eax.aes.choice"),
                        new EaxAesEncoderTextTransformer()),
                new ChoiceBoxMode(resourceBundle
                        .getString("bar.mode.decode.eax.aes.choice"),
                        new EaxAesDecoderTextTransformer()));
        choiceBoxModes.getSelectionModel().selectFirst();

        Button encodeDecodeButton = new Button(
                resourceBundle.getString("bar.encode_decode.button"));
        encodeDecodeButton.setOnAction(event -> onEncodeDecode());

        bar.add(choiceBoxModes);
        bar.add(encodeDecodeButton);
        autoEncodeDecode = new CheckBox(resourceBundle
                .getString("bar.encode_decode.checkbox"));
        bar.add(autoEncodeDecode);
        useCompression = new CheckBox(resourceBundle.getString(
                "bar.use.compression.checkbox"));
        bar.add(useCompression);
        return bar;
    }

    private void onExit(ActionEvent event) {
        primaryStage.close();
    }

    private void onInputTextChanged(
            ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
        isInputDirty = true;
    }

    private void onEncodeDecode() {
        isInputDirty = false;
        TextTransformerService service = new TextTransformerService();
        service.setExecutor(this.backgroundTasks);
        ChoiceBoxMode mode = choiceBoxModes.getSelectionModel()
                .selectedItemProperty().getValue();
        service.setTextTransformer(mode.getTextTransformer());
        service.setTextToBeTransformed(inputTextArea.getText());
        service.setPassword(passwordField.getText());
        service.setRepeatedPassword(repeatedPasswordField.getText());
        service.setMemorableWord(memorableWordField.getText());
        service.setPin(pinField.getText());
        service.setUseCompression(useCompression.isSelected());
        service.setOnSucceeded(this::onTextTransformationSucceeded);
        service.setOnFailed(this::onTextTransformationFailed);
        service.start();
    }

    private void onTextTransformationFailed(WorkerStateEvent event) {
        String msg;

        try {
            throw event.getSource().getException();
        } catch (NumberFormatException e) {
            msg = resourceBundle.getString("error.number.format");
        } catch (ZeroPinException e) {
            msg = resourceBundle.getString("error.zero.pin");
        } catch (WeakPasswordException e) {
            msg = resourceBundle.getString("error.weak.password");
        } catch (RepeatedPasswordDontMatchException e) {
            msg = resourceBundle.getString("error.password.mismatch");
        } catch (Throwable t) {
            msg = resourceBundle.getString("error.unknown") + "\n"
                    + t.getMessage() + "\n" + ExceptionUtils.getStackTrace(t);
        }

        outputTextArea.setText(msg);
    }

    private void onTextTransformationSucceeded(WorkerStateEvent event) {
        outputTextArea.setText(event.getSource().getValue().toString());
    }

    private void onSelectFont(ActionEvent event) {
        FontPreferencesDialog.showAndWait(primaryStage, resourceBundle,
                inputTextArea.getFont().getSize(),
                inputTextArea.getFont().getFamily()).ifPresent(font -> {
            inputTextArea.setFont(font);
            inputTextArea.requestLayout();
            outputTextArea.setFont(font);
            outputTextArea.requestLayout();
        });
    }

    private void onTimer() {
        // If auto encode decode check box is selected and if the
        // input is dirty do the encryption
        if (autoEncodeDecode.isSelected()) {
            if (isInputDirty) {
                onEncodeDecode();
            }
        }
    }
}
