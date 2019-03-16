/*
 * org.goffi.moffi
 *
 * File Name: BcTextFileManagerService.java
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
package org.goffi.moffi.domain.model.services.impl;

import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.Key;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesEncoder;
import org.goffi.moffi.domain.model.exceptions.FileManagerException;
import org.goffi.moffi.domain.model.services.TextFileManagerService;

import java.io.*;
import java.nio.file.Path;

public class BcTextFileManagerService implements TextFileManagerService {

    private static final String DEFAULT_CHARSET = "UTF8";

    @Override
    public void save(String text, Path file, UserEncryptionKey key) {
        try {
            Key cryptoKey = new BcPasswordBasedKey(key.getCombinedKey());

            DataTransformer encoder = new BcGcmAesEncoder(cryptoKey);

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(text.getBytes(DEFAULT_CHARSET))) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file.toFile())) {
                    encoder.transform(byteArrayInputStream, fileOutputStream);
                }
            }
        } catch (IOException ioException) {
            throw new FileManagerException(ioException.getMessage(), ioException);
        }
    }

    @Override
    public String open(Path file, UserEncryptionKey key) {
        try {
            Key cryptoKey = new BcPasswordBasedKey(key.getCombinedKey());

            DataTransformer decoder = new BcGcmAesDecoder(cryptoKey);

            String text;

            try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    decoder.transform(fileInputStream, byteArrayOutputStream);

                    text = new String(byteArrayOutputStream.toByteArray(), DEFAULT_CHARSET);
                }
            }

            return text;
        } catch (IOException ioException) {
            throw new FileManagerException(ioException.getMessage(), ioException);
        }
    }
}
