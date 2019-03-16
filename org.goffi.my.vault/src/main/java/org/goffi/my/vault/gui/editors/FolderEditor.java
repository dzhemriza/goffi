/*
 * org.goffi.my.vault
 *
 * File Name: FolderEditor.java
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

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.goffi.my.vault.gui.VaultNodeTableView;
import org.goffi.my.vault.gui.VaultNodeTableViewUtils;
import org.goffi.my.vault.model.Folder;
import org.goffi.my.vault.model.VaultNode;
import org.goffi.my.vault.services.UserPreferences;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class FolderEditor implements Editor {

    /**
     * Container inside the window
     */
    private MigPane container;
    private MigPane folderContainer;
    private Label folderLabel;
    private Folder folder;
    private TableView<VaultNodeTableView> tableView;
    private final ResourceBundle resourceBundle;
    private final Consumer<VaultNode> onNodeDoubleClicked;

    public FolderEditor(ResourceBundle resourceBundle,
            Consumer<VaultNode> onNodeDoubleClicked) {
        this.resourceBundle = resourceBundle;
        this.onNodeDoubleClicked = onNodeDoubleClicked;
    }

    @Override
    public boolean isSupports(VaultNode node) {
        return node.getClass().equals(Folder.class);
    }

    @Override
    public void startEdit(VaultNode node, MigPane container) {
        folder = (Folder) node;

        refresh();

        this.container = container;
        this.container.add(folderContainer, "dock center");
    }

    @Override
    public void updatePreferences(UserPreferences userPreferences) {
        // Not implemented
    }

    @Override
    public boolean stopEdit() {
        folder = null;

        this.container.remove(folderContainer);

        return false;
    }

    @Override
    public boolean save() {
        // Nothing to do
        return false;
    }

    @Override
    public void refresh() {
        createControls();
        folderLabel.setText(folder.getName());

        ArrayList<VaultNodeTableView> subNodes = new ArrayList<>();
        folder.getChildren().stream().map(VaultNodeTableView::new)
                .forEach(subNodes::add);
        tableView.setItems(FXCollections.observableArrayList(subNodes));
    }

    private void createControls() {
        if (folderContainer == null) {
            folderContainer = new MigPane();
            folderLabel = new Label();

            MigPane labelsContainer = new MigPane();
            labelsContainer.add(folderLabel);

            folderContainer.add(labelsContainer, "dock north");

            tableView = VaultNodeTableViewUtils.createVaultNodeTableView(
                    resourceBundle);
            tableView.setColumnResizePolicy(
                    TableView.CONSTRAINED_RESIZE_POLICY);

            tableView.setRowFactory(rf -> new TableRowImpl());

            folderContainer.add(tableView, "dock center");
        }
    }

    private class TableRowImpl extends TableRow<VaultNodeTableView> {

        public TableRowImpl() {
            this.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !this.isEmpty()) {
                    onNodeDoubleClicked.accept(this.getItem().getVaultNode());
                }
            });
        }
    }

}
