/*
 * org.goffi.my.vault
 *
 * File Name: DocumentTableViewDialog.java
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
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.goffi.my.vault.model.Document;
import org.goffi.my.vault.model.VaultNode;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class DocumentTableViewDialog {

    private static int COLUMN_PREFERED_WIDTH = 120;

    private Stage primaryStage;
    private ResourceBundle resourceBundle;
    private Consumer<VaultNode> selectItemConsumer;
    private Dialog<Void> dialog = new Dialog<>();

    public DocumentTableViewDialog(Stage primaryStage,
            ResourceBundle resourceBundle,
            Consumer<VaultNode> selectItemConsumer) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
        this.selectItemConsumer = selectItemConsumer;
    }

    public void show(Document document) {
        dialog.initOwner(primaryStage);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.setTitle(resourceBundle.getString(
                "dialog.document.table.view.title"));
        dialog.setResizable(true);

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

    @SuppressWarnings("unchecked")
    private void buildLayout(MigPane centerPane, Document document) {
        TableView<VaultNodeTableView> tableView =
                VaultNodeTableViewUtils.createVaultNodeTableView(
                        resourceBundle);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().forEach(
                col -> col.setPrefWidth(COLUMN_PREFERED_WIDTH));


        // Setup row double click
        tableView.setRowFactory(tableRow -> {
            TableRow<VaultNodeTableView> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onRowDoubleClicked(row);
                }
            });
            return row;
        });

        // Convert the tree to a flat list
        ArrayList<VaultNodeTableView> docTableView = new ArrayList<>();
        traverse(document.getRootFolder(), docTableView);
        // Create a filter and populate the table with the results
        FilteredList<VaultNodeTableView> filteredList = new FilteredList<>(
                FXCollections.observableArrayList(docTableView),
                p -> true);
        SortedList sortedList = new SortedList(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        tableView.getSelectionModel().clearSelection();

        tableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                VaultNodeTableView node =
                        tableView.getSelectionModel().getSelectedItem();

                if (node != null && node.getVaultNode() != null) {
                    onRowDoubleClicked(node);
                }
            }
        });

        Label filterLabel = new Label(resourceBundle.getString(
                "dialog.document.table.view.search.label"));
        TextField filterTextField = new TextField();
        filterTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // Set the filterList predicate based on the new value
                    filteredList.setPredicate(vaultNodeTableView ->
                            filterNode(vaultNodeTableView.getVaultNode(),
                                    newValue));
                    // Clear the selection on the list
                    tableView.getSelectionModel().clearSelection();
                });
        filterTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                tableView.getSelectionModel().selectNext();
                tableView.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                tableView.getSelectionModel().selectPrevious();
                tableView.requestFocus();
            } else if (event.getCode() == KeyCode.ENTER) {
                VaultNodeTableView node =
                        tableView.getSelectionModel().getSelectedItem();

                if (node != null && node.getVaultNode() != null) {
                    onRowDoubleClicked(node);
                }
            }
        });

        // Make sure the text field is in focus
        Platform.runLater(filterTextField::requestFocus);

        centerPane.add(filterLabel, "dock north");
        centerPane.add(filterTextField, "dock north");
        centerPane.add(tableView, "dock center");
    }

    private boolean filterNode(VaultNode vaultNode, String newValue) {
        if (newValue == null || newValue.isEmpty()) {
            return true;
        }

        String searchText = newValue.toLowerCase();

        String vaultNodeName = vaultNode.getName();

        if (vaultNodeName == null || vaultNodeName.isEmpty()) {
            return false;
        }

        return vaultNodeName.toLowerCase().contains(searchText);
    }

    private void onRowDoubleClicked(TableRow<VaultNodeTableView> row) {
        VaultNodeTableView rowData = row.getItem();
        onRowDoubleClicked(rowData);
    }

    private void onRowDoubleClicked(VaultNodeTableView rowData) {
        Platform.runLater(() -> {
            selectItemConsumer.accept(rowData.getVaultNode());
            dialog.close();
        });
    }

    private void traverse(VaultNode node, List<VaultNodeTableView> flatView) {
        for (VaultNode child : node.getChildren()) {
            flatView.add(new VaultNodeTableView(child));
            if (child.isContainer()) {
                traverse(child, flatView);
            }
        }
    }
}
