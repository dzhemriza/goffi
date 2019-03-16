/*
 * org.goffi.my.vault
 *
 * File Name: PasswordEditor.java
 *
 * Copyright 2018 Dzhem Riza
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
package org.goffi.my.vault.gui.editors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.goffi.core.domainmodel.exceptions.RepeatedPasswordDontMatchException;
import org.goffi.core.domainmodel.exceptions.WeakPasswordException;
import org.goffi.fx.core.AlertUtils;
import org.goffi.my.vault.model.Password;
import org.goffi.my.vault.model.VaultNode;
import org.goffi.my.vault.services.ClipboardService;
import org.goffi.my.vault.services.UserPreferences;
import org.tbee.javafx.scene.layout.MigPane;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordEditor implements Editor {

    private MigPane container;
    private MigPane passwordContainer;
    private Label passwordLabel;
    private TextField descriptionTextField;
    private TextField urlField;
    private Password password;
    private boolean passwordChanged = false;
    private ResourceBundle resourceBundle;
    private ClipboardService clipboardService;
    private Stage parent;

    public PasswordEditor(Stage parent, ResourceBundle resourceBundle,
            ClipboardService clipboardService) {
        this.parent = parent;
        this.resourceBundle = resourceBundle;
        this.clipboardService = clipboardService;
    }

    @Override
    public boolean isSupports(VaultNode node) {
        return node.getClass().equals(Password.class);
    }

    @Override
    public void startEdit(VaultNode node, MigPane container) {
        password = (Password) node;
        passwordChanged = false;
        this.container = container;

        refresh();

        this.container.add(passwordContainer, "dock center");
    }

    @Override
    public void updatePreferences(UserPreferences userPreferences) {
        // Not implemented
    }

    @Override
    public boolean stopEdit() {
        boolean result = save();
        password = null;
        this.container.remove(passwordContainer);
        return result;
    }

    @Override
    public boolean save() {
        boolean result = passwordChanged;
        passwordChanged = false;

        if (password != null) {
            try {
                String descriptionText = descriptionTextField.getText();
                String urlText = urlField.getText().trim();

                // Password description change check
                result = result ||
                        !descriptionText.equals(password.getDescription());
                // Password URL change check

                if (password.getUrl() != null) {
                    // if we have non null url
                    result = result ||
                            !password.getUrl().toString().equals(urlText);
                } else {
                    // If it's null and the text is not empty we have a change
                    result = result || !urlText.isEmpty();
                }

                password.setDescription(descriptionText);

                if (!urlText.isEmpty()) {
                    password.setUrl(new URL(urlField.getText()));
                } else {
                    password.setUrl(null);
                }
            } catch (MalformedURLException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle(
                        resourceBundle
                                .getString("editor.password.error.title"));
                error.setContentText(resourceBundle
                        .getString(
                                "editor.password.error.malformedurl.content"));
                error.showAndWait();
            }
        }

        return result;
    }

    @Override
    public void refresh() {
        createControls();

        passwordLabel.setText(password.getName());
        descriptionTextField.setText(password.getDescription());
        if (password.getUrl() != null) {
            urlField.setText(password.getUrl().toString());
        } else {
            urlField.setText("");
        }
    }

    private void onCopyToClipboard() {
        if (password.getPassword() != null) {
            String pass = new String(this.password.getPassword());
            clipboardService.copyToClipboard(pass);
        }

        AlertUtils.info(parent, resourceBundle
                .getString("editor.password.info.passwordcopied.content"));
    }

    private void onChangePassword() {
        Dialog<String> changePasswordDialog = new Dialog<>();

        changePasswordDialog.initOwner(parent);
        changePasswordDialog.setTitle(resourceBundle
                .getString("editor.password.changepassword.dialog.title"));
        changePasswordDialog.setHeaderText(resourceBundle
                .getString("editor.password.changepassword.dialog.header"));
        changePasswordDialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) changePasswordDialog.getDialogPane()
                .lookupButton(ButtonType.OK);

        MigPane contentPane = new MigPane();

        PasswordField passwordField = new PasswordField();
        PasswordField repeatPasswordField = new PasswordField();

        contentPane.add(new Label(resourceBundle.getString(
                "editor.password.changepassword.dialog.password.label")));
        contentPane.add(passwordField, "wrap");
        contentPane.add(new Label(resourceBundle.getString(
                "editor.password.changepassword.dialog.repeatpassword.label")));
        contentPane.add(repeatPasswordField);

        changePasswordDialog.getDialogPane().setContent(contentPane);

        okButton.addEventFilter(ActionEvent.ACTION, (ae) -> {
            try {
                org.goffi.core.domainmodel.Password password =
                        org.goffi.core.domainmodel.Password
                                .of(passwordField.getText().toCharArray(),
                                        repeatPasswordField.getText()
                                                .toCharArray());
            } catch (WeakPasswordException ex) {
                AlertUtils.error(parent,
                        resourceBundle.getString("error.weak.password"));
                ae.consume(); // not valid
            } catch (RepeatedPasswordDontMatchException ex) {
                AlertUtils.error(parent,
                        resourceBundle.getString("error.password.mismatch"));
                ae.consume(); // not valid
            }
        });

        Platform.runLater(passwordField::requestFocus);

        changePasswordDialog.setResultConverter((dialogButton) -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        changePasswordDialog.showAndWait().ifPresent((newPass) -> {
            this.password.setPassword(newPass.toCharArray());
            this.passwordChanged = true;
        });
    }

    private void createControls() {
        if (passwordContainer == null) {
            passwordContainer = new MigPane();

            passwordLabel = new Label(password.getName());

            descriptionTextField = new TextField("");
            urlField = new TextField("");

            MigPane staticFieldsPane = new MigPane();
            staticFieldsPane.add(new Label(resourceBundle
                    .getString("editor.password.description.label")));
            staticFieldsPane.add(descriptionTextField, "growx,pushx,wrap");
            staticFieldsPane.add(new Label(
                    resourceBundle.getString("editor.password.url.label")));
            staticFieldsPane.add(urlField, "growx,pushx");

            Button copyToClipboardButton = new Button(resourceBundle
                    .getString("editor.password.copytoclipboard.button"));
            copyToClipboardButton.setOnAction((a) -> onCopyToClipboard());
            Button changePasswordButton = new Button(resourceBundle
                    .getString("editor.password.changepassword.button"));
            changePasswordButton.setOnAction((a) -> onChangePassword());

            MigPane buttonsPane = new MigPane();
            buttonsPane.add(copyToClipboardButton);
            buttonsPane.add(changePasswordButton);

            passwordContainer.add(passwordLabel, "wrap,growx,pushx");
            passwordContainer.add(staticFieldsPane, "wrap,growx,pushx");
            passwordContainer.add(buttonsPane);
        }
    }
}
