/*
 * org.goffi.my.vault
 *
 * File Name: HistoryDialog.java
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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.goffi.fx.core.AlertUtils;
import org.goffi.my.vault.services.FileHistory;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HistoryDialog {

    private static double PREF_HEIGHT = 460;
    private static double PREF_WIDTH = 460;

    private Stage primaryStage;
    private ResourceBundle resourceBundle;

    public HistoryDialog(Stage primaryStage, ResourceBundle resourceBundle) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
    }

    @SuppressWarnings("unchecked")
    public Optional<File> showAndWait(List<FileHistory.Record> history) {
        Dialog<File> dialog = new Dialog<>();

        TableColumn<FileHistory.Record, String> fileColumn =
                new TableColumn<>(resourceBundle.getString(
                        "dialog.open.history.table.column.file"));
        fileColumn.setCellValueFactory(new PropertyValueFactory<>("file"));

        TableColumn<FileHistory.Record, String> timestampColumn =
                new TableColumn<>(resourceBundle.getString(
                        "dialog.open.history.table.column.timestamp"));
        timestampColumn.setCellValueFactory(
                new PropertyValueFactory<>("timestampInDefaultLocale"));

        TableView<FileHistory.Record> recordTableView = new TableView<>();
        recordTableView.setEditable(false);

        recordTableView.setRowFactory(tableView -> {
            TableRow<FileHistory.Record> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onRowDoubleClicked(dialog, row.getItem());
                }
            });
            return row;
        });

        recordTableView.getColumns().addAll(fileColumn, timestampColumn);
        recordTableView.setItems(FXCollections.observableArrayList(history));

        GridPane.setVgrow(recordTableView, Priority.ALWAYS);
        GridPane.setHgrow(recordTableView, Priority.ALWAYS);

        GridPane contentPane = new GridPane();
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.add(recordTableView, 0, 0);

        dialog.initOwner(primaryStage);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(
                ButtonType.OK);

        dialog.getDialogPane().setContent(contentPane);
        dialog.setTitle(resourceBundle.getString("dialog.open.history.title"));
        dialog.getDialogPane().setPrefSize(PREF_WIDTH, PREF_HEIGHT);

        okButton.addEventFilter(ActionEvent.ACTION, (ae) -> {
            ReadOnlyObjectProperty<FileHistory.Record> selectedItem =
                    recordTableView.getSelectionModel().selectedItemProperty();

            if (null == selectedItem.get()) {
                AlertUtils.error(primaryStage, resourceBundle.getString(
                        "dialog.open.history.error.msg.missing.selection"));
                ae.consume(); // not valid
            }
        });

        dialog.setResultConverter((dialogButton) -> {
            if (dialogButton == ButtonType.OK) {
                ReadOnlyObjectProperty<FileHistory.Record> selectedItem =
                        recordTableView.getSelectionModel().selectedItemProperty();

                if (null != selectedItem.get()) {
                    return selectedItem.get().getFile();
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void onRowDoubleClicked(Dialog<File> dialog,
            FileHistory.Record row) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(
                ButtonType.OK);
        Platform.runLater(okButton::fire);
    }
}
