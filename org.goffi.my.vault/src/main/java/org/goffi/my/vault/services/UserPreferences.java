/*
 * org.goffi.my.vault
 *
 * File Name: UserPreferences.java
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
package org.goffi.my.vault.services;

import javafx.scene.text.Font;

import java.io.File;

/**
 * Value class representing user's custom settings
 */
public class UserPreferences {

    private static final int DEFAULT_TIMED_ACTION_INTERVAL = 0; // no auto save

    private long stamp;
    private boolean wordWrap;
    private Font font;
    private File lastKnownOpenSaveDirectory;
    private FileHistory fileHistory;
    private int autoSaveInterval; // in minutes
    private int autoLockInterval; // in minutes

    public UserPreferences(boolean wordWrap, Font font,
            File lastKnownOpenSaveDirectory, FileHistory fileHistory,
            int autoSaveInterval, int autoLockInterval) {
        this.stamp = System.currentTimeMillis();
        this.wordWrap = wordWrap;
        this.font = font;
        this.lastKnownOpenSaveDirectory = lastKnownOpenSaveDirectory;
        this.fileHistory = fileHistory;
        this.autoSaveInterval = autoSaveInterval;
        this.autoLockInterval = autoLockInterval;
    }

    public UserPreferences() {
        this(false, Font.getDefault(),
                new File(System.getProperty("user.home")), new FileHistory(),
                DEFAULT_TIMED_ACTION_INTERVAL, DEFAULT_TIMED_ACTION_INTERVAL);
    }

    public long getStamp() {
        return stamp;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
        this.stamp = System.currentTimeMillis();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        this.stamp = System.currentTimeMillis();
    }

    public File getLastKnownOpenSaveDirectory() {
        return lastKnownOpenSaveDirectory;
    }

    public void setLastKnownOpenSaveDirectory(File lastKnownOpenSaveDirectory) {
        this.lastKnownOpenSaveDirectory = lastKnownOpenSaveDirectory;
        this.stamp = System.currentTimeMillis();
    }

    public FileHistory getFileHistory() {
        return this.fileHistory;
    }

    public void setFileHistory(FileHistory fileHistory) {
        this.fileHistory = fileHistory;
    }

    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }

    public void setAutoSaveInterval(int autoSaveInterval) {
        this.autoSaveInterval = autoSaveInterval;
        this.stamp = System.currentTimeMillis();
    }

    public int getAutoLockInterval() {
        return autoLockInterval;
    }

    public void setAutoLockInterval(int autoLockInterval) {
        this.autoLockInterval = autoLockInterval;
    }
}
