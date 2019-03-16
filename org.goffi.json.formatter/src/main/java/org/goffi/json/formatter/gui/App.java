/*
 * org.goffi.json.formatter
 *
 * File Name: App.java
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
package org.goffi.json.formatter.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.goffi.fx.core.AboutDialog;
import org.goffi.fx.core.FontPreferencesDialog;
import org.goffi.fx.core.SystemPropertiesDialog;

import java.io.InputStream;
import java.util.HashMap;
import java.util.ResourceBundle;

public class App extends Application {

    private static final int MIN_WIDTH = 520;
    private static final int MIN_HEIGHT = 520;

    private ResourceBundle resourceBundle;
    private Stage primaryStage;
    private TextArea textAreaLeft;
    private TextArea textAreaRight;

    public static void main(String[] arg) throws Exception {
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        resourceBundle = ResourceBundle.getBundle(
                "org/goffi/json/formatter/gui/i18n/ApplicationStrings");

        BorderPane root = new BorderPane();
        root.setCenter(createMainPane());
        root.setTop(createMainMenu());

        Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle(resourceBundle.getString("app.name"));

        InputStream resourceIcon = this.getClass().getModule()
                .getResourceAsStream(
                        "org/goffi/json/formatter/gui/images/json_formatter_icon" +
                                ".png");

        primaryStage.getIcons().add(new Image(resourceIcon));
        primaryStage.show();
    }

    private MenuBar createMainMenu() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu(resourceBundle.getString("menu.file"));
        MenuItem format = new MenuItem(
                resourceBundle.getString("menu.file.format"));
        format.setOnAction((actionEvent) -> onJsonFormat());

        MenuItem clear = new MenuItem(
                resourceBundle.getString("menu.file.clear"));
        clear.setOnAction((actionEvent) -> onClear());

        MenuItem exit = new MenuItem(
                resourceBundle.getString("menu.file.exit"));
        exit.setOnAction(this::onExit);
        file.getItems().addAll(format, clear, new SeparatorMenuItem(), exit);

        Menu preferences = new Menu(
                resourceBundle.getString("menu.preferences"));
        CheckMenuItem wordWrap = new CheckMenuItem(
                resourceBundle.getString("menu.preferences.word.wrap"));
        wordWrap.setSelected(textAreaLeft.isWrapText());
        wordWrap.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    textAreaLeft.setWrapText(newValue);
                    textAreaRight.setWrapText(newValue);
                });

        MenuItem font = new MenuItem(
                resourceBundle.getString("menu.preferences.font"));
        font.setOnAction(this::onSelectFont);

        preferences.getItems().addAll(wordWrap, font);

        Menu help = new Menu(resourceBundle.getString("menu.help"));
        MenuItem systemProperties = new MenuItem(
                resourceBundle.getString("menu.help.system.properties"));
        systemProperties.setOnAction(a -> SystemPropertiesDialog
                .showAndWait(primaryStage, resourceBundle));
        MenuItem about = new MenuItem(
                resourceBundle.getString("menu.help.about"));
        about.setOnAction(
                event -> AboutDialog.showAndWait(primaryStage, resourceBundle));
        help.getItems().addAll(systemProperties, about);

        menuBar.getMenus().addAll(file, preferences, help);

        return menuBar;
    }

    private Control createMainPane() {
        textAreaLeft = new TextArea();
        textAreaLeft.setText("{}");
        textAreaRight = new TextArea();
        textAreaRight.setEditable(false);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(textAreaLeft, textAreaRight);
        return splitPane;
    }

    private void onExit(ActionEvent event) {
        primaryStage.close();
    }

    private void onSelectFont(ActionEvent event) {
        FontPreferencesDialog.showAndWait(primaryStage, resourceBundle,
                textAreaLeft.getFont().getSize(),
                textAreaLeft.getFont().getFamily()).ifPresent(font -> {
            textAreaLeft.setFont(font);
            textAreaLeft.requestLayout();
            textAreaRight.setFont(font);
            textAreaRight.requestLayout();
        });
    }

    private void onJsonFormat() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<>() {
            };

            HashMap<String, Object> stringObjectHashMap =
                    objectMapper.readValue(textAreaLeft.getText(), typeRef);

            textAreaRight.setText(
                    objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(stringObjectHashMap));
        } catch (Exception e) {
            textAreaRight.setText(ExceptionUtils.getStackTrace(e));
        }
    }

    private void onClear() {
        textAreaLeft.setText("");
        textAreaRight.setText("");
    }
}
