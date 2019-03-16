/*
 * org.goffi.toffi
 *
 * File Name: GeneralPredefinedTest.java
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
package org.goffi.core.domainmodel.encryptor;

import org.apache.commons.codec.binary.Base64;
import org.goffi.core.domainmodel.Constants;
import org.goffi.core.domainmodel.MemorableWord;
import org.goffi.core.domainmodel.Password;
import org.goffi.core.domainmodel.Pin;
import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralPredefinedTest {

    private static String TEST_MSG = "This is a test message!";
    private static String CIPHER_TEXT_GCM_AES_BASE64 =
            "xVgBxkUS6KWnb2pGt0DydjKI4vWgNnj2EMQ0qQygAtqk83i/cfUNpoADWA==";
    private static String CIPHER_TEXT_EAX_AES_BASE64 =
            "6k2fAZXZ7hor1+uRQlm/bPPip8S6LNFTxH7cndAIMdxGTnRKLsZA25Ap+A==";

    private static UserEncryptionKey KEY =
            new UserEncryptionKey(Password.of("111111".toCharArray()),
                    Pin.of(1), MemorableWord.of("1".toCharArray()));

    @Test
    public void testGcmDecode() throws IOException {
        BcGcmAesDecoder bcGcmAesDecoder = new BcGcmAesDecoder(
                new BcPasswordBasedKey(KEY.getCombinedKey()));

        try (var in = new ByteArrayInputStream(
                Base64.decodeBase64(CIPHER_TEXT_GCM_AES_BASE64));
             var out = new ByteArrayOutputStream()) {
            bcGcmAesDecoder.transform(in, out);

            Assert.assertEquals(TEST_MSG,
                    out.toString(Constants.DEFAULT_CHARSET));
        }
    }

    @Test
    public void testEaxDecode() throws IOException {
        BcEaxAesDecoder bcEaxAesDecoder = new BcEaxAesDecoder(
                new BcPasswordBasedKey(KEY.getCombinedKey()));

        try (var in = new ByteArrayInputStream(
                Base64.decodeBase64(CIPHER_TEXT_EAX_AES_BASE64));
             var out = new ByteArrayOutputStream()) {
            bcEaxAesDecoder.transform(in, out);

            Assert.assertEquals(TEST_MSG,
                    out.toString(Constants.DEFAULT_CHARSET));
        }
    }

}
