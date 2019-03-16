/*
 * org.goffi.my.vault
 *
 * File Name: CustomFileChooser.java
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

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Optional;

/**
 * Custom File Chooser it remembers the last directory user where in.
 */
public class CustomFileChooser {

    private FileChooser fileChooser = new FileChooser();
    private Window ownerWindow;
    private File lastDirectory;
    private final File defaultHomeDirectory;

    public CustomFileChooser() {
        this(null);
    }

    public CustomFileChooser(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
        defaultHomeDirectory = new File(System.getProperty("user.home"));
        lastDirectory = defaultHomeDirectory;
    }

    public ObservableList<FileChooser.ExtensionFilter> getExtensionFilters() {
        return fileChooser.getExtensionFilters();
    }

    public Window getOwnerWindow() {
        return ownerWindow;
    }

    public void setOwnerWindow(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    public Optional<File> showOpenDialog() {
        if (lastDirectory.exists()) {
            fileChooser.setInitialDirectory(lastDirectory);
        } else {
            // Update the existing to points to the default home directory
            fileChooser.setInitialDirectory(defaultHomeDirectory);
        }

        File fileToOpen = fileChooser.showOpenDialog(this.ownerWindow);
        if (fileToOpen == null) {
            return Optional.empty();
        }
        lastDirectory = fileToOpen.getParentFile();
        return Optional.of(fileToOpen);
    }

    public Optional<File> showSaveDialog() {
        fileChooser.setInitialDirectory(lastDirectory);
        File fileToSave = fileChooser.showSaveDialog(this.ownerWindow);
        if (fileToSave == null) {
            return Optional.empty();
        }
        lastDirectory = fileToSave.getParentFile();
        return Optional.of(fileToSave);
    }

    public File getLastDirectory() {
        return lastDirectory;
    }

    public void setLastDirectory(File lastDirectory) {
        this.lastDirectory = lastDirectory;
    }
}
