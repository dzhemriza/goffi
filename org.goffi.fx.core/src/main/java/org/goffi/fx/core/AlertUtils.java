/*
 * org.goffi.fx.core
 *
 * File Name: AlertUtils.java
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
package org.goffi.fx.core;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class AlertUtils {

    public static void error(Window owner, String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(error);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    public static void error(Window owner, String error, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(error);
        alert.initOwner(owner);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static boolean confirm(Window owner, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(msg);
        alert.initOwner(owner);
        Optional<ButtonType> result = alert.showAndWait();
        return  result.get() == ButtonType.OK;
    }

    public static boolean question(Window owner, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg,
                ButtonType.YES, ButtonType.NO);
        alert.initOwner(owner);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }

    public static void info(Window owner, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg,
                ButtonType.CLOSE);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
