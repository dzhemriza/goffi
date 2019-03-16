/*
 * org.goffi.moffi
 *
 * File Name: UserEncryptionKey.java
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
package org.goffi.core.domainmodel;

/**
 * Represents the crypto key convert user point of view
 */
public class UserEncryptionKey {

    private static final String SALT =
            "4c15822e-3d38-45fc-83f5-6c1753c772fd--2db15c1d-9ed2-4946-8ccf" +
                    "-01a4fa434aa4";

    private final Password password;
    private final Pin pin;
    private final MemorableWord memorableWord;

    public UserEncryptionKey(Password password, Pin pin,
            MemorableWord memorableWord) {
        this.password = password;
        this.pin = pin;
        this.memorableWord = memorableWord;
    }

    public Password getPassword() {
        return password;
    }

    public Pin getPin() {
        return pin;
    }

    public MemorableWord getMemorableWord() {
        return memorableWord;
    }

    public char[] getCombinedKey() {
        char[] salt = SALT.toCharArray();
        char[] p = pin.toString().toCharArray();

        int len = password.length() + p.length + memorableWord.length() +
                salt.length;
        char[] result = new char[len];

        int j = 0;

        System.arraycopy(password.getPassword(), 0, result, j,
                password.length());
        j += password.length();
        System.arraycopy(p, 0, result, j, p.length);
        j += p.length;
        System.arraycopy(memorableWord.getWord(), 0, result, j,
                memorableWord.length());
        j += memorableWord.length();
        System.arraycopy(salt, 0, result, j, salt.length);

        return result;
    }
}
