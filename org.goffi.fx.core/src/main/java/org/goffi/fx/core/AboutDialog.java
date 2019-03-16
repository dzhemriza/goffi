/*
 * org.goffi.fx.core
 *
 * File Name: AboutDialog.java
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.ResourceBundle;

/**
 * Common About Dialog Template
 * <p>
 * The data that has being shown in this dialog is retrieved from the following
 * fields: dialog.help.about.license.text, dialog.help.about.title and
 * dialog.help.about.header.text
 * <p>
 * If those fields are missing from {@link ResourceBundle} provided in this
 * class this is going to throw {@link Exception}
 */
public class AboutDialog {

    private Stage primaryStage;
    private ResourceBundle resourceBundle;

    public AboutDialog(Stage primaryStage, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.primaryStage = primaryStage;
    }

    public void showAndWait() {
        MigPane fontDataPane = new MigPane();
        fontDataPane.add(new Label(
                resourceBundle.getString("dialog.help.about.license.text")));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(fontDataPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.setTitle(resourceBundle.getString("dialog.help.about.title"));
        dialog.setHeaderText(
                resourceBundle.getString("dialog.help.about.header.text"));
        dialog.initOwner(this.primaryStage);
        dialog.showAndWait();
    }

    public static void showAndWait(Stage primaryStage,
            ResourceBundle resourceBundle) {
        new AboutDialog(primaryStage, resourceBundle).showAndWait();
    }
}
