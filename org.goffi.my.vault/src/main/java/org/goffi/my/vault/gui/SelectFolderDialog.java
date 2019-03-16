/*
 * org.goffi.my.vault
 *
 * File Name: SelectFolderDialog.java
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

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.goffi.my.vault.model.Document;
import org.goffi.my.vault.model.VaultNode;

import java.util.Optional;
import java.util.ResourceBundle;

public class SelectFolderDialog {

    private Stage primaryStage;
    private ResourceBundle resourceBundle;

    public SelectFolderDialog(Stage primaryStage,
            ResourceBundle resourceBundle) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
    }

    public Optional<VaultNode> show(Document document,
            VaultNode selectedFolder) {
        TreeView<VaultNode> documentTreeView = new TreeView<>();

        // Build a tree of Folder's only and select selectedFolder
        TreeItem<VaultNode> root = new TreeItem<>(document.getRootFolder());
        root.setExpanded(true);
        buildTree(root);
        documentTreeView.setRoot(root);

        documentTreeView.getSelectionModel().select(findSelection(root,
                selectedFolder));

        Platform.runLater(documentTreeView::requestFocus);

        GridPane.setVgrow(documentTreeView, Priority.ALWAYS);
        GridPane.setHgrow(documentTreeView, Priority.ALWAYS);

        GridPane contentPane = new GridPane();
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.add(documentTreeView, 0, 0);

        Dialog<VaultNode> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(contentPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,
                ButtonType.CANCEL);
        dialog.setResizable(true);
        dialog.setTitle(resourceBundle.getString(
                "dialog.select.folder.title"));
        dialog.setResultConverter((dialogButton) -> {
            if (dialogButton == ButtonType.OK) {
                TreeItem<VaultNode> selectedItem =
                        documentTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() != null) {
                    return selectedItem.getValue();
                }
            }
            return null;
        });
        dialog.initOwner(primaryStage);
        return dialog.showAndWait();
    }

    private TreeItem<VaultNode> findSelection(TreeItem<VaultNode> parent,
            VaultNode selectedFolder) {
        return TreeViews.findByValue(parent, selectedFolder);
    }

    private void buildTree(TreeItem<VaultNode> parent) {
        for (VaultNode child : parent.getValue().getChildren()) {
            if (child.isContainer()) {
                TreeItem<VaultNode> treeItemChild = new TreeItem<>(child);
                treeItemChild.setExpanded(true);
                parent.getChildren().add(treeItemChild);
                buildTree(treeItemChild);
            }
        }
    }
}
