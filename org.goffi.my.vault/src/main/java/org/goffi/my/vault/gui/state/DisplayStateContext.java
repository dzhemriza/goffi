/*
 * org.goffi.my.vault
 *
 * File Name: DisplayStateContext.java
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
package org.goffi.my.vault.gui.state;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.goffi.fx.core.AlertUtils;
import org.goffi.my.vault.gui.EncryptionDataReader;
import org.goffi.my.vault.gui.LockScreenView;
import org.goffi.my.vault.gui.SearchAllWindow;
import org.goffi.my.vault.model.EncryptionData;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class DisplayStateContext {

    private final Stage primaryStage;
    private final MigPane mainPane;
    private final SplitPane splitPane;
    private final LockScreenView lockScreenView;
    private final SearchAllWindow searchAllWindow;
    private final SimpleBooleanProperty screenLockProperty;
    private final ResourceBundle resourceBundle;
    private DisplayState displayState;
    private EncryptionData encryptionData;

    public DisplayStateContext(Stage primaryStage, MigPane mainPane,
            SplitPane splitPane, LockScreenView lockScreenView,
            SearchAllWindow searchAllWindow,
            SimpleBooleanProperty screenLockProperty,
            ResourceBundle resourceBundle) {
        this.primaryStage = primaryStage;
        this.mainPane = mainPane;
        this.splitPane = splitPane;
        this.lockScreenView = lockScreenView;
        this.searchAllWindow = searchAllWindow;
        this.screenLockProperty = screenLockProperty;
        this.displayState = new DocumentViewState();
        this.resourceBundle = resourceBundle;
    }

    public MigPane getMainPane() {
        return mainPane;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public LockScreenView getLockScreenView() {
        return lockScreenView;
    }

    public SearchAllWindow getSearchAllWindow() {
        return searchAllWindow;
    }

    public SimpleBooleanProperty screenLockProperty() {
        return screenLockProperty;
    }

    void setDisplayState(DisplayState newState) {
        this.displayState = newState;
    }

    public void lockUnlockScreen(EncryptionData encryptionData) {
        setEncryptionData(encryptionData);
        displayState.lockUnlock(this);
    }

    public EncryptionData getEncryptionData() {
        return encryptionData;
    }

    public void setEncryptionData(EncryptionData encryptionData) {
        this.encryptionData = encryptionData;
    }

    void controlsToLockView() {
        getMainPane().remove(getSplitPane());
        getMainPane().add(getLockScreenView(), "dock center");
        screenLockProperty().set(true);
    }

    void controlsToDocumentView() {
        getMainPane().remove(getLockScreenView());
        getMainPane().add(getSplitPane(), "dock center");
        screenLockProperty().set(false);
    }

    boolean allowUnlock() {
        EncryptionDataReader reader =
                new EncryptionDataReader(primaryStage, resourceBundle);
        Optional<EncryptionData> encryptionData = reader
                .setShowEncryptionMode(false)
                .setRepeatPassword(false)
                .read();

        if (encryptionData.isPresent()) {
            if (Arrays.equals(
                    getEncryptionData().getUserEncryptionKey().getCombinedKey(),
                    encryptionData.get().getUserEncryptionKey()
                            .getCombinedKey())) {
                return true;
            }

            AlertUtils.error(primaryStage, resourceBundle.getString(
                    "error.lock.unlock.screen.mismatched.user.key"));
        }
        return false;
    }
}
