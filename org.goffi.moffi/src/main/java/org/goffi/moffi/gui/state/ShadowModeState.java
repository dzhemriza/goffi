/*
 * org.goffi.moffi
 *
 * File Name: ShadowModeState.java
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
package org.goffi.moffi.gui.state;

import org.goffi.moffi.gui.PinDialog;
import org.goffi.moffi.gui.events.mode.TextModeEnabledEvent;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * Represents Shadow Mode State
 */
public class ShadowModeState implements TextEditorDisplayState {

    @Override
    public void lockUnlockScreen(MoffiDisplayStateContext context) {
        // Don't ask for PIN if this is a brain new document because
        // PIN doesn't exists yet
        if (context.getDocumentMetadata().isBrainNewDocument()) {
            context.setCurrentState(new TextModeState());
            context.getEventProcessor().fire(new TextModeEnabledEvent(this));
        } else {
            PinDialog dialog = new PinDialog();
            dialog.setTitle("Enter PIN");

            dialog.showDialog();

            if (dialog.isOkPressed()) {
                // Is pin valid
                if (context.getDocumentMetadata().getUserEncryptionKey().getPin().equals(dialog.getPin())) {
                    // Pin is valid
                    context.setCurrentState(new TextModeState());
                    context.getEventProcessor().fire(new TextModeEnabledEvent(this));
                } else {
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings");
                    // Pin is not valid
                    JOptionPane.showMessageDialog(null,
                            resourceBundle.getString("message.error.invalid.pin"),
                            resourceBundle.getString("message.error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
