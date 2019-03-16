/*
 * org.goffi.text.encoder
 *
 * File Name: EncoderTextTransformerBase.java
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
package org.goffi.text.encoder.gui;

import org.apache.commons.codec.binary.Base64;
import org.goffi.core.domainmodel.Constants;
import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.Key;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;

public abstract class EncoderTextTransformerBase extends TextTransformerBase {

    protected abstract DataTransformer createDataTransformer(Key key);

    @Override
    public String transform(UserEncryptionKey key, boolean useCompression,
            String input) throws IOException {
        // Generate the key based on supplied data from UserEncryptionKey
        Key passwordBasedKey = new BcPasswordBasedKey(key.getCombinedKey());

        // Create encoder
        DataTransformer encoder = createDataTransformer(passwordBasedKey);

        if (useCompression) {
            try (ByteArrayInputStream byteArrayInputStream =
                         new ByteArrayInputStream(
                                 input.getBytes(Constants.DEFAULT_CHARSET));
                 DeflaterInputStream deflaterInputStream =
                         new DeflaterInputStream(byteArrayInputStream);
                 ByteArrayOutputStream byteArrayOutputStream = new
                         ByteArrayOutputStream()) {
                // Encode the string
                encoder.transform(deflaterInputStream,
                        byteArrayOutputStream);

                return Base64.encodeBase64String(
                        byteArrayOutputStream.toByteArray());
            }
        } else {
            try (ByteArrayInputStream byteArrayInputStream =
                         new ByteArrayInputStream(
                                 input.getBytes(
                                         Constants.DEFAULT_CHARSET));
                 ByteArrayOutputStream byteArrayOutputStream = new
                         ByteArrayOutputStream()) {
                // Encode the string
                encoder.transform(byteArrayInputStream,
                        byteArrayOutputStream);

                return Base64.encodeBase64String(
                        byteArrayOutputStream.toByteArray());
            }
        }
    }
}
