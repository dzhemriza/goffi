/*
 * org.goffi.text.encoder
 *
 * File Name: TextTransformerBase.java
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

import java.io.IOException;

/**
 * Base class for {@link TextTransformer} derived classes that accommodates
 * shared code.
 */
public abstract class TextTransformerBase implements TextTransformer {

    private UserEncryptionKey createKey(char[] password, char[] repeatPassword,
            char[] memorableWord, char[] pin) {
        Password pass = Password.of(password, repeatPassword);
        MemorableWord m = MemorableWord.of(memorableWord);
        Pin p = Pin.of(Integer.parseInt(new String(pin)));

        return new UserEncryptionKey(pass, p, m);
    }

    @Override
    public String transform(char[] password, char[] repeatPassword,
            char[] memorableWord, char[] pin, boolean useCompression,
            String input) throws IOException {
        UserEncryptionKey userEncryptionKey =
                createKey(password, repeatPassword, memorableWord, pin);
        return transform(userEncryptionKey, useCompression, input);
    }
}
