/*
 * org.goffi.my.vault
 *
 * File Name: EncryptionDataReader.java
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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.goffi.core.domainmodel.MemorableWord;
import org.goffi.core.domainmodel.Pin;
import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.exceptions.RepeatedPasswordDontMatchException;
import org.goffi.core.domainmodel.exceptions.WeakPasswordException;
import org.goffi.core.domainmodel.exceptions.ZeroPinException;
import org.goffi.fx.core.AlertUtils;
import org.goffi.my.vault.model.EncryptionData;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class EncryptionDataReader {

    private ResourceBundle resourceBundle;
    private Stage primaryStage;
    private boolean showEncryptionMode = true;
    private boolean repeatPassword = true;

    public EncryptionDataReader(Stage primaryStage,
            ResourceBundle resourceBundle) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
    }

    public EncryptionDataReader setShowEncryptionMode(
            boolean showEncryptionMode) {
        this.showEncryptionMode = showEncryptionMode;
        return this;
    }

    public boolean isShowEncryptionMode() {
        return showEncryptionMode;
    }

    public EncryptionDataReader setRepeatPassword(boolean repeatPassword) {
        this.repeatPassword = repeatPassword;
        return this;
    }

    public boolean isRepeatPassword() {
        return repeatPassword;
    }

    public Optional<EncryptionData> read(boolean repeatPassword) {
        return setRepeatPassword(repeatPassword).read();
    }

    public Optional<EncryptionData> read() {
        Dialog<EncryptionData> encryptionDetailsDialog = new Dialog<>();
        encryptionDetailsDialog.initOwner(primaryStage);
        encryptionDetailsDialog
                .setTitle(resourceBundle.getString("crypto.information.title"));
        encryptionDetailsDialog.setHeaderText(
                resourceBundle.getString("crypto.information.top.label"));
        encryptionDetailsDialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) encryptionDetailsDialog.getDialogPane()
                .lookupButton(ButtonType.OK);

        MigPane contentPane = new MigPane();
        PasswordField passwordField = new PasswordField();
        PasswordField repeatPasswordField = new PasswordField();
        PasswordField memorableField = new PasswordField();
        PasswordField pinField = new PasswordField();
        ChoiceBox<EncryptionData.Mode> modeChoiceBox = new ChoiceBox<>();
        modeChoiceBox.getItems().addAll(EncryptionData.Mode.values());
        modeChoiceBox.getSelectionModel().selectFirst();

        contentPane.add(new Label(
                resourceBundle.getString("crypto.information.password.label")));
        contentPane.add(passwordField, "wrap,growx,pushx");

        if (repeatPassword) {
            contentPane.add(new Label(resourceBundle
                    .getString("crypto.information.repeat.password.label")));
            contentPane.add(repeatPasswordField, "wrap,growx,pushx");
        }
        contentPane.add(new Label(resourceBundle
                .getString("crypto.information.memorable.word.label")));
        contentPane.add(memorableField, "wrap,growx,pushx");
        contentPane.add(new Label(
                resourceBundle.getString("crypto.information.pin.label")));
        contentPane.add(pinField, "wrap,growx,pushx");
        contentPane.add(new Label(
                resourceBundle.getString("crypto.information.mode.label")));
        if (showEncryptionMode) {
            contentPane.add(modeChoiceBox, "growx,pushx");
        }

        encryptionDetailsDialog.getDialogPane().setContent(contentPane);

        Supplier<EncryptionData> createEncryptionData = () -> {
            org.goffi.core.domainmodel.Password password;
            if (repeatPassword) {
                password = org.goffi.core.domainmodel.Password.of(
                        passwordField.getText().toCharArray(),
                        repeatPasswordField.getText().toCharArray());
            } else {
                password = org.goffi.core.domainmodel.Password.of(
                        passwordField.getText().toCharArray());
            }

            Pin pin = Pin.of(Integer.parseInt(pinField.getText()));

            MemorableWord memorableWord =
                    MemorableWord.of(memorableField.getText().toCharArray());

            UserEncryptionKey userEncryptionKey =
                    new UserEncryptionKey(password, pin, memorableWord);

            EncryptionData encryptionData = new EncryptionData();

            encryptionData.setUserEncryptionKey(userEncryptionKey);
            encryptionData.setMode(
                    modeChoiceBox.getSelectionModel().getSelectedItem());

            return encryptionData;
        };

        okButton.addEventFilter(ActionEvent.ACTION, (ae) -> {
            try {
                createEncryptionData.get();
            } catch (WeakPasswordException ex) {
                AlertUtils.error(primaryStage, resourceBundle.getString(
                        "error.weak.password"));
                ae.consume(); // not valid
            } catch (RepeatedPasswordDontMatchException ex) {
                AlertUtils.error(primaryStage, resourceBundle.getString(
                        "error.password.mismatch"));
                ae.consume(); // not valid
            } catch (NumberFormatException e) {
                AlertUtils.error(primaryStage, resourceBundle.getString(
                        "error.number.format"));
                ae.consume(); // not valid
            } catch (ZeroPinException e) {
                AlertUtils.error(primaryStage, resourceBundle.getString(
                        "error.zero.pin"));
                ae.consume(); // not valid
            }
        });

        Platform.runLater(passwordField::requestFocus);

        encryptionDetailsDialog.setResultConverter((dialogButton) -> {
            if (dialogButton == ButtonType.OK) {
                return createEncryptionData.get();
            }
            return null;
        });

        return encryptionDetailsDialog.showAndWait();
    }
}
