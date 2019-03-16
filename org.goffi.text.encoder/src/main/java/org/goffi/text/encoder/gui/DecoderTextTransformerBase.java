/*
 * org.goffi.text.encoder
 *
 * File Name: DecoderTextTransformerBase.java
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
import java.util.zip.InflaterOutputStream;

public abstract class DecoderTextTransformerBase extends TextTransformerBase {

    protected abstract DataTransformer createDataTransformer(Key key);

    @Override
    public String transform(UserEncryptionKey key, boolean useCompression,
            String input) throws IOException {
        byte[] inputByteArray = Base64.decodeBase64(input);

        // Generate the key based on supplied data from UserEncryptionKey
        Key passwordBasedKey = new BcPasswordBasedKey(key.getCombinedKey());

        // Create decoder
        DataTransformer decoder = createDataTransformer(passwordBasedKey);

        if (useCompression) {
            try (ByteArrayInputStream byteArrayInputStream = new
                    ByteArrayInputStream(
                    inputByteArray);
                 ByteArrayOutputStream byteArrayOutputStream = new
                         ByteArrayOutputStream();
                 InflaterOutputStream inflaterOutputStream =
                         new InflaterOutputStream(byteArrayOutputStream)) {
                decoder.transform(byteArrayInputStream,
                        inflaterOutputStream);

                return byteArrayOutputStream
                        .toString(Constants.DEFAULT_CHARSET);
            }
        } else {
            try (ByteArrayInputStream byteArrayInputStream = new
                    ByteArrayInputStream(
                    inputByteArray);
                 ByteArrayOutputStream byteArrayOutputStream = new
                         ByteArrayOutputStream()) {
                decoder.transform(byteArrayInputStream,
                        byteArrayOutputStream);

                return byteArrayOutputStream
                        .toString(Constants.DEFAULT_CHARSET);
            }
        }
    }
}
