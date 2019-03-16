/*
 * org.goffi.moffi
 *
 * File Name: UserEncryptionKeyTest.java
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

import org.junit.Assert;
import org.junit.Test;

public class UserEncryptionKeyTest {

    @Test
    public void testValidKeyGeneration() {
        Password password = Password.of("SwordFish".toCharArray());
        Pin pin = Pin.of(12);
        MemorableWord memorableWord =
                MemorableWord.of("MemorableWord".toCharArray());

        UserEncryptionKey userEncryptionKey =
                new UserEncryptionKey(password, pin, memorableWord);
        String sb = String.valueOf(password.getPassword()) +
                pin +
                String.valueOf(memorableWord.getWord()) +
                "4c15822e-3d38-45fc-83f5-6c1753c772fd--2db15c1d-9ed2-4946" +
                "-8ccf-01a4fa434aa4";
        Assert.assertArrayEquals(sb // SALT convert UserEncryptionKey
                        .toCharArray(),
                userEncryptionKey.getCombinedKey());
    }
}
