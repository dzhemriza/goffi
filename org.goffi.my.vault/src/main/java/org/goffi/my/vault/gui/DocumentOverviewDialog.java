/*
 * org.goffi.my.vault
 *
 * File Name: DocumentOverviewDialog.java
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
package org.goffi.my.vault.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.goffi.my.vault.model.Document;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.ResourceBundle;

public class DocumentOverviewDialog {

    private Stage primaryStage;
    private ResourceBundle resourceBundle;

    public DocumentOverviewDialog(Stage primaryStage,
            ResourceBundle resourceBundle) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
    }

    public void show(Document document) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.setTitle(resourceBundle.getString(
                "dialog.document.overview.title"));

        MigPane centerPane = new MigPane();

        buildLayout(centerPane, document);

        GridPane.setVgrow(centerPane, Priority.ALWAYS);
        GridPane.setHgrow(centerPane, Priority.ALWAYS);

        GridPane contentPane = new GridPane();
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.add(centerPane, 0, 0);

        dialog.getDialogPane().setContent(contentPane);

        dialog.showAndWait();
    }

    private void buildLayout(MigPane centerPane, Document document) {
        StringBuilder sbFileName = new StringBuilder();
        sbFileName.append(resourceBundle.getString(
                "dialog.document.overview.file.name.label"));
        sbFileName.append(" ");
        if (document.getFile() != null) {
            sbFileName.append(document.getFile().toString());
        } else {
            sbFileName.append(resourceBundle.getString(
                    "dialog.document.overview.file.name.text.field.null"));
        }

        Label fileNameLabel = new Label(sbFileName.toString());

        String isDirtyLabel;
        if (document.isDirty()) {
            isDirtyLabel = resourceBundle.getString(
                    "dialog.document.overview.document.is.dirty.label");
        } else {
            isDirtyLabel = resourceBundle.getString(
                    "dialog.document.overview.document.is.not.dirty.label");
        }

        Label documentDirtyLabel = new Label(isDirtyLabel);

        Button changeEncryptionDataButton = new Button(resourceBundle.getString(
                "dialog.document.overview.change.encryption.data.button"));
        changeEncryptionDataButton.setOnAction(a -> {
            EncryptionDataReader reader =
                    new EncryptionDataReader(primaryStage, resourceBundle);
            final boolean repeatPassword = true;
            reader.read(repeatPassword).ifPresent(encryptionData -> {
                document.setEncryptionData(encryptionData);
                // Set the document dirty to prompt the user to save the file
                document.setDirty(true);
            });
        });

        centerPane.add(fileNameLabel, "wrap");
        centerPane.add(documentDirtyLabel, "wrap");
        centerPane.add(changeEncryptionDataButton, "wrap");
    }
}

