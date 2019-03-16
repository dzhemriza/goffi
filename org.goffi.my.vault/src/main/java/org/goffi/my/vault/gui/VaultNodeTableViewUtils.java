/*
 * org.goffi.my.vault
 *
 * File Name: VaultNodeTableViewUtils.java
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

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ResourceBundle;

public class VaultNodeTableViewUtils {

    public static TableView<VaultNodeTableView> createVaultNodeTableView(
            ResourceBundle resourceBundle) {
        TableView<VaultNodeTableView> tableView = new TableView<>();
        buildTableViewDefaultColumns(resourceBundle, tableView);
        return tableView;
    }

    @SuppressWarnings("unchecked")
    public static void buildTableViewDefaultColumns(
            ResourceBundle resourceBundle,
            TableView<VaultNodeTableView> tableView) {
        TableColumn<VaultNodeTableView, String> idColumn = new TableColumn<>(
                resourceBundle.getString("vault.node.table.view.column.id"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VaultNodeTableView, String> nameColumn = new TableColumn<>(
                resourceBundle.getString("vault.node.table.view.column.name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<VaultNodeTableView, String> userNameColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.user"));
        userNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("userName"));

        TableColumn<VaultNodeTableView, String> timestampColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.timestamp"));
        timestampColumn.setCellValueFactory(
                new PropertyValueFactory<>("timestamp"));

        TableColumn<VaultNodeTableView, String> lastUpdatedTimestampColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.lastupdatedtimestamp"));
        lastUpdatedTimestampColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastUpdatedTimestamp"));

        TableColumn<VaultNodeTableView, String> lastUpdatedUserNameColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.lastupdatedusername"));
        lastUpdatedUserNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastUpdatedUserName"));

        tableView.getColumns().addAll(idColumn, nameColumn, userNameColumn,
                timestampColumn, lastUpdatedUserNameColumn,
                lastUpdatedTimestampColumn);
    }
}
