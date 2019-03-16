/*
 * org.goffi.toffi
 *
 * File Name: BcGcmV2Test.java
 *
 * Copyright 2016 Dzhem Riza
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
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesEncoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Tests for v2 version of {@link BcGcmAesEncoder} and {@link BcGcmAesDecoder}
 */
public class BcGcmV2Test {

    private final static int DATA_SIZE = 2048;

    private final BcPasswordBasedKey key =
            new BcPasswordBasedKey("!MegaDeath -> \\m/!".toCharArray());
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
        BcGcmAesEncoder bcGcmAesEncoder = new BcGcmAesEncoder(key);
        BcGcmAesDecoder bcGcmAesDecoder = new BcGcmAesDecoder(key);

        try (ByteArrayOutputStream byteArrayOutputStream = new
                ByteArrayOutputStream()) {
            try (ByteArrayInputStream byteArrayInputStream = new
                    ByteArrayInputStream(
                    data)) {
                bcGcmAesEncoder
                        .transform(byteArrayInputStream, byteArrayOutputStream);
            }

            // Verify the data and the encoded data differ
            byte[] encodedData = byteArrayOutputStream.toByteArray();
            Assert.assertFalse(Arrays.equals(data, encodedData));

            // Decode the data and verify with the source
            try (ByteArrayOutputStream byteArrayOutputStreamDecoded = new
                    ByteArrayOutputStream()) {
                try (ByteArrayInputStream byteArrayInputStream = new
                        ByteArrayInputStream(
                        encodedData)) {
                    bcGcmAesDecoder.transform(byteArrayInputStream,
                            byteArrayOutputStreamDecoded);
                }

                byte[] decodedData = byteArrayOutputStreamDecoded.toByteArray();
                Assert.assertArrayEquals(data, decodedData);
            }
        }
    }
}
