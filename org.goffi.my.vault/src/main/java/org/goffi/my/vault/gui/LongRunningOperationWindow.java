/*
 * org.goffi.my.vault
 *
 * File Name: LongRunningOperationWindow.java
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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.ResourceBundle;

public class LongRunningOperationWindow extends Stage {

    private ResourceBundle resourceBundle;
    private Stage primaryStage;

    public LongRunningOperationWindow(Stage primaryStage,
            ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.primaryStage = primaryStage;

        initOwner(this.primaryStage);

        MigPane root = new MigPane();
        root.add(new Label(resourceBundle.getString(
                "longrunningoperation.label")));

        setScene(new Scene(root));

        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
        sizeToScene();

        Platform.runLater(() -> {
            final double centerX = primaryStage.getX() +
                    (primaryStage.getWidth() / 2);
            final double centerY = primaryStage.getY() +
                    (primaryStage.getHeight() / 2);

            setX(centerX - (getWidth() / 2));
            setY(centerY - (getHeight() / 2));
        });
    }
}
