/*
 * org.goffi.my.vault
 *
 * File Name: SearchWindow.java
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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.goffi.core.domainmodel.text.SimpleTextMatcher;
import org.goffi.core.domainmodel.text.TextMatcher;
import org.goffi.fx.core.AlertUtils;
import org.goffi.my.vault.model.*;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.goffi.my.vault.gui.Constants.SEARCH_ALL_WINDOW_HEIGHT;
import static org.goffi.my.vault.gui.Constants.SEARCH_ALL_WINDOW_WIDTH;

/**
 * Class representing the search all window
 */
public class SearchAllWindow extends Stage {

    private ResourceBundle resourceBundle;
    private Stage primaryStage;
    private TextArea textArea;
    private Supplier<Document> documentSupplier;
    private Consumer<VaultNode> selectItemConsumer;
    private ExecutorService executorService;
    private WindowActivityTracker windowActivityTracker;
    private TextField searchTextField;
    private CheckBox matchCase;
    private TableView<SearchResult> tableView;

    public SearchAllWindow(Stage primaryStage, ResourceBundle resourceBundle,
            Supplier<Document> documentSupplier,
            Consumer<VaultNode> selectItemConsumer,
            ExecutorService executorService,
            WindowActivityTracker windowActivityTracker) throws IOException {
        this.resourceBundle = resourceBundle;
        this.primaryStage = primaryStage;
        this.documentSupplier = documentSupplier;
        this.selectItemConsumer = selectItemConsumer;
        this.executorService = executorService;
        this.windowActivityTracker = windowActivityTracker;

        initOwner(this.primaryStage);

        setTitle(resourceBundle.getString("search.all.title"));
        InputStream resourceIcon = this.getClass().getModule()
                .getResourceAsStream(
                        "org/goffi/my/vault/gui/images/search.png");
        getIcons().add(new Image(resourceIcon));

        MigPane root = new MigPane();
        setScene(new Scene(root, SEARCH_ALL_WINDOW_WIDTH,
                SEARCH_ALL_WINDOW_HEIGHT));

        textArea = new TextArea();
        textArea.setEditable(false);

        MigPane topPane = new MigPane();

        topPane.add(new Label(resourceBundle.getString(
                "search.all.options.label")));
        matchCase = new CheckBox(resourceBundle.getString(
                "search.all.match.case.check.box"));
        topPane.add(matchCase);
        Button searchButton = new Button(resourceBundle.getString(
                "search.all.start.search.button"));
        searchButton.setOnAction(event -> onSearch());
        searchButton.setDefaultButton(true);
        topPane.add(searchButton);

        MigPane searchPane = new MigPane();
        searchPane.add(new Label(resourceBundle.getString(
                "search.all.search.for.label")));
        searchTextField = new TextField();
        searchPane.add(searchTextField, "growx,pushx");

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPosition(0, 0.50);
        splitPane.getItems().addAll(createMatchTable(), textArea);

        MigPane centerPane = new MigPane();
        centerPane.add(splitPane, "dock center");

        root.add(topPane, "dock north");
        root.add(searchPane, "dock north");
        root.add(centerPane, "dock center");

        this.setOnShown(e -> {
            this.setX(this.getX());
            this.setY(this.getY());
            this.setWidth(this.getWidth());
            this.setHeight(this.getHeight());
        });

        this.focusedProperty().addListener(
                (observable, oldValue, newValue) -> windowActivityTracker
                        .recordActivity(this, newValue));

        Platform.runLater(() -> searchTextField.requestFocus());
    }

    @SuppressWarnings("unchecked")
    private TableView<SearchResult> createMatchTable() {
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SearchResult, String> nodeIdColumn =
                new TableColumn<>(resourceBundle.getString(
                        "search.all.match.table.column.node.id"));
        nodeIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("nodeId"));

        TableColumn<SearchResult, String> nodeNameColumn =
                new TableColumn<>(resourceBundle.getString(
                        "search.all.match.table.column.node.name"));
        nodeNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("nodeName"));

        TableColumn<SearchResult, String> matchFieldColumn =
                new TableColumn<>(resourceBundle.getString(
                        "search.all.match.table.column.match.field"));
        matchFieldColumn.setCellValueFactory(
                new PropertyValueFactory<>("matchField"));

        TableColumn<SearchResult, String> matchStartColumn =
                new TableColumn<>(resourceBundle.getString(
                        "search.all.match.table.column.match.start"));
        matchStartColumn.setCellValueFactory(
                new PropertyValueFactory<>("matchStart"));

        TableColumn<SearchResult, String> matchLenColumn =
                new TableColumn<>(resourceBundle.getString(
                        "search.all.match.table.column.match.len"));
        matchLenColumn.setCellValueFactory(
                new PropertyValueFactory<>("matchLen"));

        TableColumn<SearchResult, String> userNameColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.user"));
        userNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("userName"));

        TableColumn<SearchResult, String> timestampColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.timestamp"));
        timestampColumn.setCellValueFactory(
                new PropertyValueFactory<>("timestamp"));

        TableColumn<SearchResult, String> lastUpdatedTimestampColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.lastupdatedtimestamp"));
        lastUpdatedTimestampColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastUpdatedTimestamp"));

        TableColumn<SearchResult, String> lastUpdatedUserNameColumn =
                new TableColumn<>(resourceBundle.getString(
                        "vault.node.table.view.column.lastupdatedusername"));
        lastUpdatedUserNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastUpdatedUserName"));

        tableView.getColumns().addAll(nodeIdColumn, nodeNameColumn,
                matchFieldColumn, matchStartColumn, matchLenColumn,
                userNameColumn, timestampColumn, lastUpdatedUserNameColumn,
                lastUpdatedTimestampColumn);

        tableView.setRowFactory(tableView -> {
            TableRow<SearchResult> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onRowDoubleClicked(row);
                }
            });
            return row;
        });

        tableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        onTableViewItemSelected(newValue));

        return tableView;
    }

    private void onRowDoubleClicked(TableRow<SearchResult> row) {
        SearchResult rowData = row.getItem();

        Platform.runLater(() -> {
            selectItemConsumer.accept(rowData.getNode());
            hide();
        });
    }

    private void onTableViewItemSelected(SearchResult searchResult) {
        if (searchResult == null) {
            textArea.setText("");
        } else {
            textArea.setText(searchResult.getTextPreview());
            textArea.selectRange(searchResult.getMatchStart(),
                    searchResult.getMatchStart() + searchResult.getMatchLen());
        }
    }

    public TextArea getTextArea() {
        return textArea;
    }

    /**
     * @return {@link Object} if user canceled the operation {@code null}
     * otherwise
     */
    private Dialog<Object> cancelDialog() {
        Dialog<Object> cancelDialog = new Dialog<>();
        cancelDialog.setTitle(resourceBundle.getString(
                "dialog.cancel.search.title"));
        cancelDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        cancelDialog.initOwner(this);

        MigPane contentPane = new MigPane();
        contentPane.add(new Label(resourceBundle.getString(
                "dialog.cancel.search.content")));

        cancelDialog.getDialogPane().setContent(contentPane);

        cancelDialog.setResultConverter((button) -> {
            if (button == ButtonType.CANCEL) {
                return new Object();
            }
            return null;
        });

        return cancelDialog;
    }

    @SuppressWarnings("unchecked")
    private void onSearch() {
        String text = searchTextField.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        Dialog<Object> cancelDialog = cancelDialog();

        SearchService searchService = new SearchService(new SimpleTextMatcher(
                matchCase.isSelected(),
                searchTextField.getText()));
        searchService.setExecutor(executorService);

        searchService.setOnFailed(event -> {
            cancelDialog.hide();
            Platform.runLater(() -> {
                // Do the update in the JavaFX thread
                AlertUtils.error(primaryStage, event.getSource().getException()
                                .getMessage(),
                        event.getSource().getException());
            });
        });
        searchService.setOnSucceeded(event -> {
            cancelDialog.hide();

            Platform.runLater(() -> {
                List<SearchResult> results =
                        (List<SearchResult>) event.getSource()
                                .getValue();
                tableView.setItems(FXCollections.observableArrayList(results));
            });
        });

        searchService.start();
        cancelDialog.showAndWait().ifPresent(o -> searchService.cancel());
    }

    public static class SearchResult {

        private VaultNode node;
        private String textPreview;
        private TextMatcher.Match match;
        private String matchField;

        public SearchResult(VaultNode node, String textPreview,
                TextMatcher.Match match, String matchField) {
            this.node = node;
            this.textPreview = textPreview;
            this.match = match;
            this.matchField = matchField;
        }

        public VaultNode getNode() {
            return node;
        }

        public String getTextPreview() {
            return textPreview;
        }

        public TextMatcher.Match getMatch() {
            return match;
        }

        // Properties used in TableView

        public String getNodeId() {
            return node.getId().toString();
        }

        public String getNodeName() {
            return node.getName();
        }

        public String getMatchField() {
            return matchField;
        }

        public int getMatchStart() {
            return match.getStart();
        }

        public int getMatchLen() {
            return match.getLen();
        }

        public String getUserName() {
            return this.node.getUserName();
        }

        public String getTimestamp() {
            return new Date(this.node.getTimestamp()).toString();
        }

        public String getLastUpdatedTimestamp() {
            return new Date(this.node.getLastUpdatedTimestamp()).toString();
        }

        public String getLastUpdatedUserName() {
            return this.node.getLastUpdatedUserName();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("node", node)
                    .append("textPreview", textPreview)
                    .append("match", match)
                    .append("matchField", matchField)
                    .toString();
        }
    }

    public static class TextSearchVisitor extends VaultNodeReflectiveVisitor {

        private final TextMatcher textMatcher;
        private ResourceBundle resourceBundle;

        private List<SearchResult> searchResults = new ArrayList<>();

        public TextSearchVisitor(ResourceBundle resourceBundle,
                TextMatcher textMatcher) {
            this.resourceBundle = resourceBundle;
            this.textMatcher = textMatcher;
            // Requirements:
            // 1. Match on the name itself (this includes all types)
            // 2. If this is note match in the content
            // 3. If this is a password match the text description
            // field
            // 4. If this is a password match the text url field
        }

        public void visit(Note note) {
            findAllMatches(note, note.getName(), resourceBundle
                    .getString("search.all.match.field.name")); // node name

            if (note.getNote() != null) {
                findAllMatches(note, note.getNote(), resourceBundle.getString(
                        "search.all.match.field.note"));
            }
        }

        public void visit(Password password) {
            findAllMatches(password, password.getName(), resourceBundle
                    .getString("search.all.match.field.name")); // node name

            findAllMatches(password, password.getDescription(), resourceBundle
                    .getString("search.all.match.field.password.description"));

            if (password.getUrl() != null) {
                findAllMatches(password, password.getUrl().toString(),
                        resourceBundle.getString(
                                "search.all.match.field.password.uri"));
            }
        }

        public List<SearchResult> getSearchResults() {
            return searchResults;
        }

        private void findAllMatches(VaultNode vaultNode, String preview,
                String matchField) {
            List<TextMatcher.Match> allMatches =
                    textMatcher.allMatches(preview);

            allMatches.stream()
                    .map(match -> new SearchResult(vaultNode, preview,
                            match, matchField))
                    .forEach(searchResult -> searchResults.add(searchResult));
        }
    }

    private class SearchService extends Service<List<SearchResult>> {

        private final TextMatcher textMatcher;

        public SearchService(TextMatcher textMatcher) {
            this.textMatcher = textMatcher;
        }

        @Override
        protected Task<List<SearchResult>> createTask() {
            return new Task<>() {

                private void search(List<SearchResult> result, Folder folder) {
                    // Simple BFS implementation
                    Queue<VaultNode> queue = new LinkedList<>();
                    queue.add(folder);
                    while (!queue.isEmpty()) {
                        if (isCancelled()) {
                            // If the task is canceled we just terminate
                            return;
                        }

                        VaultNode vaultNode = queue.poll();
                        queue.addAll(vaultNode.getChildren());

                        TextSearchVisitor visitor = new TextSearchVisitor(
                                resourceBundle, textMatcher);
                        visitor.visit(vaultNode);
                        result.addAll(visitor.getSearchResults());
                    }
                }

                @Override
                protected List<SearchResult> call() throws Exception {
                    List<SearchResult> result = new ArrayList<>();
                    Document document = documentSupplier.get();
                    search(result, document.getRootFolder());
                    return result;
                }
            };
        }
    }
}
