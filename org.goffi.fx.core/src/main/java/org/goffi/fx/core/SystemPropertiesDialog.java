/*
 * org.goffi.fx.core
 *
 * File Name: FontPreferencesDialog.java
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
package org.goffi.fx.core;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Simple System Properties Dialog used to show java System Properties all in
 * single dialog.
 * <p>
 * Please note that {@link ResourceBundle} must have the following keys:
 * dialog.system.properties.table.column.key, dialog.system.properties.table
 * .column.value and dialog.system.properties.title
 */
public class SystemPropertiesDialog {

    private final int COLUMN_PREFERRED_WIDTH = 320;

    public static class SystemProperty {
        private final SimpleStringProperty key;
        private final SimpleStringProperty value;

        public SystemProperty(String key, String value) {
            this.key = new SimpleStringProperty(key);
            this.value = new SimpleStringProperty(value);
        }

        public String getKey() {
            return key.get();
        }

        public String getValue() {
            return value.get();
        }
    }

    private Stage primaryStage;
    private ResourceBundle resourceBundle;

    public SystemPropertiesDialog(Stage primaryStage,
            ResourceBundle resourceBundle) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
    }

    @SuppressWarnings("unchecked")
    public void showAndWait() {
        ArrayList<SystemProperty> systemProperties = new ArrayList<>();
        System.getProperties().forEach((k, v) -> systemProperties.add(
                new SystemProperty(k.toString(), v.toString())));

        TableColumn<SystemProperty, String> keyColumn =
                new TableColumn<>(resourceBundle.getString(
                        "dialog.system.properties.table.column.key"));
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyColumn.setPrefWidth(COLUMN_PREFERRED_WIDTH);

        TableColumn<SystemProperty, String> valueColumn =
                new TableColumn<>(resourceBundle.getString(
                        "dialog.system.properties.table.column.value"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setPrefWidth(COLUMN_PREFERRED_WIDTH);

        TableView<SystemProperty> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(keyColumn, valueColumn);
        tableView.setItems(FXCollections.observableArrayList(systemProperties));

        GridPane.setVgrow(tableView, Priority.ALWAYS);
        GridPane.setHgrow(tableView, Priority.ALWAYS);

        GridPane contentPane = new GridPane();
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.add(tableView, 0, 0);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(contentPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.setTitle(resourceBundle.getString(
                "dialog.system.properties.title"));
        dialog.setResizable(true);
        dialog.initOwner(this.primaryStage);
        dialog.showAndWait();
    }

    public static void showAndWait(Stage primaryStage,
            ResourceBundle resourceBundle) {
        new SystemPropertiesDialog(primaryStage, resourceBundle).showAndWait();
    }
}
