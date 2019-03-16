/*
 * org.goffi.my.vault
 *
 * File Name: PreferencesServiceImpl.java
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
package org.goffi.my.vault.services.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.my.vault.exceptions.MyVaultException;
import org.goffi.my.vault.services.FileHistory;
import org.goffi.my.vault.services.PreferencesService;
import org.goffi.my.vault.services.UserPreferences;

import java.io.File;
import java.io.IOException;

public class PreferencesServiceImpl implements PreferencesService {

    private final static Logger LOG = LogManager.getLogger(PreferencesServiceImpl.class);
    private static final String PREFERENCES_FILE_NAME = ".my-vault.json";

    private ObjectMapper objectMapper;

    /**
     * Data represented in JSON
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PreferencesData {
        @JsonProperty
        private boolean wordWrap;

        @JsonProperty
        private double fontSize;

        @JsonProperty
        private String fontFamilyName;

        @JsonProperty
        private String lastKnownOpenSaveDirectory;

        @JsonProperty
        private FileHistory fileHistory;

        @JsonProperty
        private Integer autoSaveInterval;

        @JsonProperty
        private Integer autoLockInterval;

        public boolean isWordWrap() {
            return wordWrap;
        }

        public void setWordWrap(boolean wordWrap) {
            this.wordWrap = wordWrap;
        }

        public double getFontSize() {
            return fontSize;
        }

        public void setFontSize(double fontSize) {
            this.fontSize = fontSize;
        }

        public String getFontFamilyName() {
            return fontFamilyName;
        }

        public void setFontFamilyName(String fontFamilyName) {
            this.fontFamilyName = fontFamilyName;
        }

        public String getLastKnownOpenSaveDirectory() {
            return lastKnownOpenSaveDirectory;
        }

        public void setLastKnownOpenSaveDirectory(
                String lastKnownOpenSaveDirectory) {
            this.lastKnownOpenSaveDirectory = lastKnownOpenSaveDirectory;
        }

        public FileHistory getFileHistory() {
            return fileHistory;
        }

        public void setFileHistory(FileHistory fileHistory) {
            this.fileHistory = fileHistory;
        }

        public Integer getAutoSaveInterval() {
            return autoSaveInterval;
        }

        public void setAutoSaveInterval(Integer autoSaveInterval) {
            this.autoSaveInterval = autoSaveInterval;
        }

        public Integer getAutoLockInterval() {
            return autoLockInterval;
        }

        public void setAutoLockInterval(Integer autoLockInterval) {
            this.autoLockInterval = autoLockInterval;
        }
    }

    public PreferencesServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private File getPreferencesFile() {
        return new File(System.getProperty("user.home")
                + File.separator + PREFERENCES_FILE_NAME);
    }

    @Override
    public void write(UserPreferences userPreferences) {
        try {
            PreferencesData preferencesData = new PreferencesData();
            preferencesData.setFontFamilyName(
                    userPreferences.getFont().getFamily());
            preferencesData.setFontSize(userPreferences.getFont().getSize());
            preferencesData.setWordWrap(userPreferences.isWordWrap());
            preferencesData.setLastKnownOpenSaveDirectory(
                    userPreferences.getLastKnownOpenSaveDirectory().toString());
            preferencesData.setFileHistory(userPreferences.getFileHistory());
            preferencesData.setAutoSaveInterval(
                    userPreferences.getAutoSaveInterval());
            preferencesData.setAutoLockInterval(
                    userPreferences.getAutoLockInterval());

            objectMapper.writeValue(getPreferencesFile(), preferencesData);
        } catch (IOException e) {
            throw new MyVaultException(e.getMessage(), e);
        }
    }

    @Override
    public UserPreferences read() {
        try {
            File preferencesFile = getPreferencesFile();

            if (!preferencesFile.exists()) {
                // Create default UserPreferences
                return new UserPreferences();
            }

            PreferencesData preferencesData =
                    objectMapper.readValue(preferencesFile,
                            PreferencesData.class);

            UserPreferences userPreferences = new UserPreferences();
            userPreferences.setWordWrap(preferencesData.isWordWrap());
            userPreferences.setFont(Font.font(
                    preferencesData.getFontFamilyName(),
                    preferencesData.getFontSize()));

            if (preferencesData.getLastKnownOpenSaveDirectory() != null) {
                userPreferences.setLastKnownOpenSaveDirectory(new File(
                        preferencesData.getLastKnownOpenSaveDirectory()));
            }

            if (preferencesData.getFileHistory() != null) {
                userPreferences.setFileHistory(
                        preferencesData.getFileHistory());
            }

            if (preferencesData.getAutoSaveInterval() != null) {
                userPreferences.setAutoSaveInterval(
                        preferencesData.getAutoSaveInterval());
            }

            if (preferencesData.getAutoLockInterval() != null) {
                userPreferences.setAutoLockInterval(
                        preferencesData.getAutoLockInterval());
            }

            return userPreferences;
        } catch (IOException e) {
            LOG.warn("Unable to read the preferences file '{}', failback to default!",
                    PREFERENCES_FILE_NAME, e);
            // Create default UserPreferences
            return new UserPreferences();
        }
    }
}
