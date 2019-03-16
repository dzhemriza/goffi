/*
 * org.goffi.moffi
 *
 * File Name: DocumentMetadata.java
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
package org.goffi.moffi.gui;

import org.goffi.core.domainmodel.UserEncryptionKey;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Value object containing document's metadata such as file name, password, dirty etc.
 */
public class DocumentMetadata {

    private Path file;
    private final UserEncryptionKey userEncryptionKey;
    private boolean dirty;
    private long timestamp;

    public DocumentMetadata(Path file, UserEncryptionKey userEncryptionKey) {
        this(file, userEncryptionKey, false);
    }

    public DocumentMetadata(Path file, UserEncryptionKey userEncryptionKey, boolean dirty) {
        this.file = file;
        this.userEncryptionKey = userEncryptionKey;
        this.dirty = dirty;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Empty doc doesn't contains any encryption related data and it's not dirty by default
     *
     * @return An empty {@link DocumentMetadata}
     */
    public static DocumentMetadata emptyDoc() {
        return new DocumentMetadata(null, null, false);
    }

    public String getDocumentTitle() {
        if (file != null) {
            return file.toString();
        } else {
            return "untitled.moffi";
        }
    }

    public UserEncryptionKey getUserEncryptionKey() {
        return userEncryptionKey;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isBrainNewDocument() {
        return file == null;
    }

    /**
     * Indicates that document is changed
     */
    public void update() {
        setDirty(true);
        updateTimestamp();
    }

    /**
     * Update last time the document has been accessed (this includes read-only or write access)
     */
    public void updateTimestamp() {
        timestamp = System.currentTimeMillis();
    }

    /**
     * Calculate how much time document data is idle
     *
     * @param time
     * @param timeUnit
     * @return
     */
    public boolean isIdleForAtLeast(long time, TimeUnit timeUnit) {
        return timestamp + timeUnit.toMillis(time) < System.currentTimeMillis();
    }

    public boolean isEligibleForAutoSave() {
        boolean result;

        result = !isBrainNewDocument(); // Existing document

        if (result) {
            result = isDirty(); // Document is dirty (not saved)
        }

        if (result) {
            // Document is idle for at least 1 min, future refactor (policy)
            result = isIdleForAtLeast(1, TimeUnit.MINUTES);
        }

        return result;
    }
}
