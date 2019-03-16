/*
 * org.goffi.toffi
 *
 * File Name: BcGcmAesDecoder.java
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
package org.goffi.core.domainmodel.crypto.impl.v2;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.goffi.core.domainmodel.crypto.Key;

/**
 * AES + GCM
 */
public class BcGcmAesEncoder extends BaseBlockCipherEncoder {

    public BcGcmAesEncoder(Key key, byte[] nonce, byte[] nonSecretPayload) {
        super(key, nonce, nonSecretPayload);
    }

    public BcGcmAesEncoder(Key key, byte[] nonce) {
        super(key, nonce);
    }

    public BcGcmAesEncoder(Key key, int nonce) {
        super(key, nonce);
    }

    public BcGcmAesEncoder(Key key) {
        super(key);
    }

    @Override
    protected AEADBlockCipher createAEADBlockCipher() {
        return new GCMBlockCipher(new AESEngine());
    }
}
