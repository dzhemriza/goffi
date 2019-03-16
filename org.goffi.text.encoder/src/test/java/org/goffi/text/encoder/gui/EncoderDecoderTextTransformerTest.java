/*
 * org.goffi.text.encoder
 *
 * File Name: GcmAesEncoderDecoderTextTransformerTest.java
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

import org.goffi.core.domainmodel.MemorableWord;
import org.goffi.core.domainmodel.Password;
import org.goffi.core.domainmodel.Pin;
import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.exceptions.RepeatedPasswordDontMatchException;
import org.goffi.core.domainmodel.exceptions.WeakPasswordException;
import org.goffi.core.domainmodel.exceptions.ZeroPinException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class EncoderDecoderTextTransformerTest {

    private static final String PASSWORD = "SwoRdF1$h";
    private static final String MEMORABLE_WORD = "M3t@||1c@";
    private static final String PIN = "123456789";
    private static final String MESSAGE =
            "Once Upon a Time is an American fantasy drama television " +
                    "series that premiered on October 23, 2011, on ABC. The " +
                    "show follows residents who " +
                    "are characters from various fairy tales transported to " +
                    "the \"real world\" and " +
                    "robbed of their original memories by a powerful curse. " +
                    "The first six seasons were " +
                    "set in the fictitious seaside town of Storybrooke, " +
                    "Maine, while the seventh takes " +
                    "place in a Seattle, Washington neighborhood called " +
                    "Hyperion Heights.";
    private static final boolean USE_COMPRESSION = true;

    @Test
    public void testGcmAesEncryptionE2EUserEncryptionKeyFunction() throws
            IOException {
        Password password = Password.of(PASSWORD.toCharArray());
        MemorableWord memorableWord =
                MemorableWord.of(MEMORABLE_WORD.toCharArray());
        Pin pin = Pin.of(Integer.parseInt(PIN));
        UserEncryptionKey userEncryptionKey =
                new UserEncryptionKey(password, pin, memorableWord);

        TextTransformer encoder = new GcmAesEncoderTextTransformer();

        String result = encoder.transform(userEncryptionKey, USE_COMPRESSION,
                MESSAGE);

        TextTransformer decoder = new GcmAesDecoderTextTransformer();

        Assert.assertEquals(MESSAGE,
                decoder.transform(userEncryptionKey, USE_COMPRESSION, result));
    }

    @Test
    public void testGcmAesEncryptionE2E() throws IOException {
        TextTransformer encoder = new GcmAesEncoderTextTransformer();

        String result = encoder.transform(
                PASSWORD.toCharArray(),
                PASSWORD.toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                PIN.toCharArray(),
                USE_COMPRESSION,
                MESSAGE);

        TextTransformer decoder = new GcmAesDecoderTextTransformer();

        Assert.assertEquals(MESSAGE, decoder.transform(
                PASSWORD.toCharArray(),
                PASSWORD.toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                PIN.toCharArray(),
                USE_COMPRESSION,
                result));
    }

    @Test
    public void testEaxAesEncryptionE2EUserEncryptionKeyFunction() throws
            IOException {
        Password password = Password.of(PASSWORD.toCharArray());
        MemorableWord memorableWord =
                MemorableWord.of(MEMORABLE_WORD.toCharArray());
        Pin pin = Pin.of(Integer.parseInt(PIN));
        UserEncryptionKey userEncryptionKey =
                new UserEncryptionKey(password, pin, memorableWord);

        TextTransformer encoder = new EaxAesEncoderTextTransformer();

        final String MESSAGE = "this is a test message";

        String result =
                encoder.transform(userEncryptionKey, USE_COMPRESSION, MESSAGE);

        TextTransformer decoder = new EaxAesDecoderTextTransformer();

        Assert.assertEquals(MESSAGE,
                decoder.transform(userEncryptionKey, USE_COMPRESSION, result));
    }

    @Test
    public void testEaxAesEncryptionE2E() throws IOException {
        TextTransformer encoder = new EaxAesEncoderTextTransformer();

        String result = encoder.transform(
                PASSWORD.toCharArray(),
                PASSWORD.toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                PIN.toCharArray(),
                USE_COMPRESSION,
                MESSAGE);

        TextTransformer decoder = new EaxAesDecoderTextTransformer();

        Assert.assertEquals(MESSAGE, decoder.transform(
                PASSWORD.toCharArray(),
                PASSWORD.toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                PIN.toCharArray(),
                USE_COMPRESSION,
                result));
    }

    @Test(expected = RepeatedPasswordDontMatchException.class)
    public void testPasswordDoesntMatch() throws Exception {
        TextTransformer encoder = new EaxAesEncoderTextTransformer();
        encoder.transform(PASSWORD.toCharArray(),
                "test".toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                PIN.toCharArray(),
                USE_COMPRESSION,
                MESSAGE);
    }

    @Test(expected = WeakPasswordException.class)
    public void testWeakPasswordException() throws Exception {
        TextTransformer encoder = new EaxAesEncoderTextTransformer();
        encoder.transform("test".toCharArray(),
                "test".toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                PIN.toCharArray(),
                USE_COMPRESSION,
                MESSAGE);
    }

    @Test(expected = ZeroPinException.class)
    public void testZeroPinException() throws Exception {
        TextTransformer encoder = new EaxAesEncoderTextTransformer();
        encoder.transform(PASSWORD.toCharArray(),
                PASSWORD.toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                "0".toCharArray(),
                USE_COMPRESSION,
                MESSAGE);
    }

    @Test(expected = NumberFormatException.class)
    public void testNonIntegerPin() throws Exception {
        TextTransformer encoder = new EaxAesEncoderTextTransformer();
        encoder.transform(PASSWORD.toCharArray(),
                PASSWORD.toCharArray(),
                MEMORABLE_WORD.toCharArray(),
                "test".toCharArray(),
                USE_COMPRESSION,
                MESSAGE);
    }
}
