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

import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Simple Font selection dialog having only size and font name selection.
 * <p>
 * To use this class {@link ResourceBundle} must have the following keys:
 * dialog.font.select.font.name.label, dialog.font.select.font.size.label,
 * dialog.font.select.title and dialog.font.select.header.text
 */
public class FontPreferencesDialog {

    private static final int SPINNER_MIN = 8;
    private static final int SPINNER_MAX = 72;
    private static final double SPINNER_STEP = 0.5;

    private Stage primaryStage;
    private ResourceBundle resourceBundle;
    private String initialFontFamily;
    private double initialFontSize;

    public FontPreferencesDialog(Stage primaryStage,
            ResourceBundle resourceBundle, double initialFontSize,
            String initialFontFamily) {
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
        this.initialFontFamily = initialFontFamily;
        this.initialFontSize = initialFontSize;
    }

    public Optional<Font> showAndWait() {
        MigPane fontDataPane = new MigPane();
        fontDataPane.add(new Label(resourceBundle
                .getString("dialog.font.select.font.name.label")));

        ChoiceBox<String> fontsChoiceBox = new ChoiceBox<>();
        fontsChoiceBox.getItems().addAll(Font.getFamilies());
        fontsChoiceBox.getSelectionModel()
                .select(initialFontFamily);
        fontDataPane.add(fontsChoiceBox, "wrap");

        fontDataPane.add(new Label(resourceBundle
                .getString("dialog.font.select.font.size.label")));

        Spinner<Double> sizeSpinner = new Spinner<>();
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        SPINNER_MIN, SPINNER_MAX, initialFontSize,
                        SPINNER_STEP);
        sizeSpinner.setValueFactory(valueFactory);
        fontDataPane.add(sizeSpinner, "wrap");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(fontDataPane);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle(resourceBundle.getString("dialog.font.select.title"));
        dialog.setHeaderText(
                resourceBundle.getString("dialog.font.select.header.text"));
        dialog.initOwner(this.primaryStage);

        Optional<Font> result = Optional.empty();

        Optional<ButtonType> okPressed =
                dialog.showAndWait().filter(button -> button == ButtonType.OK);
        if (okPressed.isPresent()) {
            String family = fontsChoiceBox.getSelectionModel()
                    .getSelectedItem().toString();
            double size = sizeSpinner.getValue();
            result = Optional.of(Font.font(family, size));
        }

        return result;
    }

    public static Optional<Font> showAndWait(Stage primaryStage,
            ResourceBundle resourceBundle, double initialFontSize,
            String initialFontFamily) {
        return new FontPreferencesDialog(primaryStage, resourceBundle,
                initialFontSize, initialFontFamily).showAndWait();
    }
}
