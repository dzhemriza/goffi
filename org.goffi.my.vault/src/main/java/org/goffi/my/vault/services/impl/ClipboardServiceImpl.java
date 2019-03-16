/*
 * org.goffi.my.vault
 *
 * File Name: ClipboardServiceImpl.java
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

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.goffi.my.vault.services.ClipboardService;

import java.util.Optional;

public class ClipboardServiceImpl implements ClipboardService {

    final Clipboard clipboard = Clipboard.getSystemClipboard();

    @Override
    public void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    @Override
    public Optional<String> readTextFromClipboard() {
        if (clipboard.hasString()) {
            return Optional.of(clipboard.getString());
        }
        return Optional.empty();
    }
}
