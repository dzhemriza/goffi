/*
 * org.goffi.my.vault
 *
 * File Name: NoteEditor.java
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.goffi.core.domainmodel.text.SimpleTextMatcher;
import org.goffi.core.domainmodel.text.TextMatcher;
import org.goffi.fx.core.AlertUtils;
import org.goffi.fx.core.text.TextAreaMatchIterator;
import org.goffi.my.vault.model.Note;
import org.goffi.my.vault.model.VaultNode;
import org.goffi.my.vault.services.UserPreferences;
import org.tbee.javafx.scene.layout.MigPane;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class NoteEditor implements Editor {

    /**
     * Container inside the window
     */
    private MigPane container;
    /**
     * {@link Note} being edited
     */
    private Note note;
    private MigPane noteContainer;
    private Label noteNameLabel;
    private TextArea textArea;
    private TextField searchTextField;
    private TextField replaceTextField;
    private CheckBox matchCaseCheckBox;
    private long preferencesStamp = 0L;
    private final ResourceBundle resourceBundle;
    private TextAreaMatchIterator.CaretPosition caretPositionState =
            TextAreaMatchIterator.Caret.Current;
    private Stage parent;
    private boolean dirty = false;

    public NoteEditor(Stage parent, ResourceBundle resourceBundle) {
        this.parent = parent;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public boolean isSupports(VaultNode node) {
        return node.getClass().equals(Note.class);
    }

    @Override
    public void startEdit(VaultNode node, MigPane container) {
        this.note = (Note) node;
        this.container = container;

        // Clear up the search and replace fields. Maybe we can do something
        // more clever here instead of clearing the those properties
        searchTextField.setText("");
        replaceTextField.setText("");

        refresh();
        caretPositionState = TextAreaMatchIterator.Caret.Current;

        this.container.add(noteContainer, "dock center");

        // The editor is refreshed
        dirty = false;
    }

    @Override
    public void updatePreferences(UserPreferences userPreferences) {
        if (preferencesStamp != userPreferences.getStamp()) {
            preferencesStamp = userPreferences.getStamp();
            createControls(); // make sure we have all controls created

            this.textArea.setFont(userPreferences.getFont());
            this.textArea.setWrapText(userPreferences.isWordWrap());
        }
    }

    @Override
    public boolean stopEdit() {
        boolean result = save();
        this.note = null;

        if (noteContainer != null) {
            this.container.remove(noteContainer);
        }
        return result;
    }

    @Override
    public boolean save() {
        boolean result = dirty;
        if (note != null && dirty) {
            // Update the note only if we have something to update
            note.setNote(textArea.getText());
            dirty = false;
        }
        return result;
    }

    private void onAddDateTime() {
        Date now = new Date(Instant.now().toEpochMilli());
        this.textArea.insertText(textArea.getCaretPosition(), now.toString());
    }

    private void onAddYearAndWeek() {
        final String PATTERN = "yyyy - 'Week' w";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
        LocalDateTime now = LocalDateTime.now();
        this.textArea.insertText(textArea.getCaretPosition(),
                now.format(formatter));
    }

    private void createControls() {
        if (noteContainer == null) {
            noteContainer = new MigPane();

            noteNameLabel = new Label();

            MenuItem addDateTimeMenu = new MenuItem(resourceBundle.getString(
                    "editor.note.menu.item.add.date.time"));
            addDateTimeMenu.setOnAction(a -> onAddDateTime());

            MenuItem addYearAndWeek = new MenuItem(resourceBundle.getString(
                    "editor.note.menu.item.add.year.week"));
            addYearAndWeek.setOnAction(a -> onAddYearAndWeek());

            MenuButton menuButton = new MenuButton(resourceBundle.getString(
                    "editor.note.menu.label"), null,
                    addDateTimeMenu, addYearAndWeek);

            MigPane labelsContainer = new MigPane();
            labelsContainer.add(noteNameLabel, "dock center");
            labelsContainer.add(menuButton, "dock east");

            textArea = new TextArea();
            // Indicate that the note is changed already
            textArea.textProperty().addListener(
                    (observableValue, s, t1) -> dirty = true);

            noteContainer.add(labelsContainer, "dock north");
            noteContainer.add(textArea, "dock center");

            searchTextField = new TextField();
            replaceTextField = new TextField();
            matchCaseCheckBox = new CheckBox(resourceBundle.getString(
                    "editor.note.matchcase"));

            Button findNextButton = new Button(resourceBundle.getString(
                    "editor.note.findnext"));
            findNextButton.setOnAction(a -> onFindNext());
            Button replaceButton = new Button(resourceBundle.getString(
                    "editor.note.replace"));
            replaceButton.setOnAction(a -> onReplace());
            Button replaceAllButton = new Button(resourceBundle.getString(
                    "editor.note.replaceall"));
            replaceAllButton.setOnAction(a -> onReplaceAll());

            MigPane searchReplacePane = new MigPane();
            searchReplacePane.add(new Label(resourceBundle.getString(
                    "editor.note.searchfor")));
            searchReplacePane.add(searchTextField, "wrap,growx,pushx");
            searchReplacePane.add(new Label(resourceBundle.getString(
                    "editor.note.replacewith")));
            searchReplacePane.add(replaceTextField, "growx,pushx");

            MigPane toolboxPane = new MigPane();
            toolboxPane.add(matchCaseCheckBox);
            toolboxPane.add(findNextButton);
            toolboxPane.add(replaceButton);
            toolboxPane.add(replaceAllButton);

            MigPane findReplacePane = new MigPane();
            findReplacePane.add(searchReplacePane, "wrap,growx,pushx");
            findReplacePane.add(toolboxPane);

            // Additional configuration
            textArea.setOnKeyPressed(event -> {
                if (event.isControlDown() && event.getCode() == KeyCode.F) {
                    // Control + F is down we need to move the focus to the
                    // search area
                    Platform.runLater(() -> searchTextField.requestFocus());
                }
            });

            searchTextField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    // Enter simulates click on "Find Next" button
                    Platform.runLater(findNextButton::fire);
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    // Moves the focus back to the text area
                    Platform.runLater(() -> textArea.requestFocus());
                }
            });

            noteContainer.add(findReplacePane, "dock south");
        }
    }

    private void onFindNext() {
        TextMatcher textMatcher = new SimpleTextMatcher(
                this.matchCaseCheckBox.isSelected(),
                this.searchTextField.getCharacters());

        TextAreaMatchIterator iterator = new TextAreaMatchIterator(
                textArea, textMatcher, caretPositionState);

        if (iterator.hasNext()) {
            // If we have a match keep the caret to current position
            caretPositionState = TextAreaMatchIterator.Caret.Current;
            TextMatcher.Match match = iterator.next();
            textArea.selectRange(match.getStart(),
                    match.getStart() + match.getLen());
        } else {
            // If we have no match try from the top
            caretPositionState = TextAreaMatchIterator.Caret.Begin;
            AlertUtils.info(parent, resourceBundle.getString(
                    "editor.note.info.search.will.resume.from.top"));
        }
    }

    private void onReplace() {
        TextMatcher textMatcher = new SimpleTextMatcher(
                this.matchCaseCheckBox.isSelected(),
                this.searchTextField.getCharacters());

        if (textArea.getSelectedText() == null ||
                "".equals(textArea.getSelectedText())) {
            onFindNext();
            return;
        }

        String selectedText = textArea.getSelectedText();
        TextMatcher.Match math = textMatcher.indexOf(selectedText);

        if (!math.isNoMatch() && math.getStart() == 0 &&
                math.getLen() == selectedText.length()) {
            // The selection is the actual text for replacement
            textArea.replaceSelection(replaceTextField.getText());
            // and move the selection to the next occurrence
            onFindNext();
        } else {
            // If this is not what we are looking for move next
            onFindNext();
        }
    }

    private void onReplaceAll() {
        TextMatcher textMatcher = new SimpleTextMatcher(
                this.matchCaseCheckBox.isSelected(),
                this.searchTextField.getCharacters());

        TextAreaMatchIterator iterator = new TextAreaMatchIterator(
                textArea, textMatcher, TextAreaMatchIterator.Caret.Begin);

        while (iterator.hasNext()) {
            iterator.next();
            iterator.replace(replaceTextField.getText());
        }
    }

    @Override
    public void refresh() {
        createControls();

        noteNameLabel.setText(this.note.getName());
        if (this.note.getNote() != null) {
            textArea.setText(this.note.getNote());
        } else {
            textArea.setText("");
        }
    }
}
