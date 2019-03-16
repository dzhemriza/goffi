/*
 * org.goffi.toffi
 *
 * File Name: BcEaxV2Test.java
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
package org.goffi.core.domainmodel.encryptor.v2;

import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesEncoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Tests for v2 version of {@link BcEaxAesEncoder} and
 * {@link BcEaxAesDecoder}
 */
public class BcEaxV2Test {

    private final static int DATA_SIZE = 2048;

    private final BcPasswordBasedKey key = new BcPasswordBasedKey("!MegaDeath -> \\m/!".toCharArray());
    private byte[] data;

    @Before
    public void setup() {
        data = new byte[DATA_SIZE];
        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte) (i + 1);
        }
    }

    @Test
    public void testSimpleEncodeAndDecode() throws IOException {
        BcEaxAesEncoder bcGcmAesEncoder = new BcEaxAesEncoder(key);
        BcEaxAesDecoder bcGcmAesDecoder = new BcEaxAesDecoder(key);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
                bcGcmAesEncoder
                        .transform(byteArrayInputStream, byteArrayOutputStream);
            }

            // Verify the data and the encoded data differ
            byte[] encodedData = byteArrayOutputStream.toByteArray();
            Assert.assertFalse(Arrays.equals(data, encodedData));

            // Decode the data and verify with the source
            try (ByteArrayOutputStream byteArrayOutputStreamDecoded = new ByteArrayOutputStream()) {
                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encodedData)) {
                    bcGcmAesDecoder.transform(byteArrayInputStream, byteArrayOutputStreamDecoded);
                }

                byte[] decodedData = byteArrayOutputStreamDecoded.toByteArray();
                Assert.assertArrayEquals(data, decodedData);
            }
        }
    }
}
