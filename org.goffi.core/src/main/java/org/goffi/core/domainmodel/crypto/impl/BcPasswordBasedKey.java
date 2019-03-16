/*
 * org.goffi.toffi
 *
 * File Name: BcPasswordBasedKey.java
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
package org.goffi.core.domainmodel.crypto.impl;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.goffi.core.domainmodel.crypto.Key;

public class BcPasswordBasedKey implements Key {

    public static final byte[] DEFAULT_SALT = new byte[]{
            (byte) 12, (byte) 23, (byte) 143, (byte) 255, (byte) 131, (byte) 33,
            (byte) 221, (byte) 23, (byte) 143, (byte) 33, (byte) 154, (byte) 66,
            (byte) 191, (byte) 4, (byte) 73, (byte) 62, (byte) 208, (byte) 239,
            (byte) 170, (byte) 251, (byte) 67, (byte) 77, (byte) 51, (byte) 133,
            (byte) 154, (byte) 219, (byte) 192, (byte) 254, (byte) 120,
            (byte) 205, (byte) 90, (byte) 244
    };
    public static final int DEFAULT_ITERATIONS = 16384;
    public static final int DEFAULT_KEY_SIZE = 256;

    private final KeyParameter secretKey;

    /**
     * Creates a key of size {@link #DEFAULT_KEY_SIZE}
     *
     * @param password
     */
    public BcPasswordBasedKey(char[] password) {
        this(password, DEFAULT_KEY_SIZE);
    }

    public BcPasswordBasedKey(char[] password, int keySize) {
        this(password, keySize, DEFAULT_ITERATIONS, DEFAULT_SALT);
    }

    public BcPasswordBasedKey(char[] password, int keySize, int iterations) {
        this(password, keySize, iterations, DEFAULT_SALT);
    }

    public BcPasswordBasedKey(char[] password, int keySize, int iterations,
            byte[] salt) {
        byte[] pkcsPasswordBytes = PBEParametersGenerator
                .PKCS5PasswordToUTF8Bytes(password);
        PBEParametersGenerator keyGenerator = new PKCS5S2ParametersGenerator();
        keyGenerator.init(pkcsPasswordBytes, salt, iterations);
        this.secretKey = (KeyParameter) keyGenerator
                .generateDerivedParameters(keySize);
    }

    @Override
    public byte[] getKey() {
        return secretKey.getKey();
    }
}
