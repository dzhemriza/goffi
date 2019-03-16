/*
 * org.goffi.toffi
 *
 * File Name: BaseBlockCipherDecoder.java
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
package org.goffi.core.domainmodel.crypto.impl.v2;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.Key;
import org.goffi.core.domainmodel.crypto.exceptions.EncryptorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Advantage of using version 2 Encoder/Decoder is that the nonce value
 * is randomly generated and prepended to the cipher text. Which means the
 * client is not gonna need to manual remember the nonce.
 */
public abstract class BaseBlockCipherDecoder implements DataTransformer {

    private static final Logger LOG = LogManager.getLogger(BaseBlockCipherDecoder.class);
    private final Key key;
    private final byte[] nonSecretPayload;

    public BaseBlockCipherDecoder(Key key, byte[] nonSecretPayload) {
        this.key = key;
        this.nonSecretPayload = nonSecretPayload;
    }

    public BaseBlockCipherDecoder(Key key) {
        this(key, Constants.DEFAULT_SALT);
    }

    protected abstract AEADBlockCipher createAEADBlockCipher();

    @Override
    public void transform(InputStream in, OutputStream out) {
        try {
            AEADBlockCipher aeadBlockCipher = createAEADBlockCipher();

            // Read the first 4 bytes as they contains nonce
            byte[] nonce = new byte[4];
            int bytesRead = in.read(nonce);
            if (bytesRead < 4) {
                throw new RuntimeException("Failed to read nonce! Bytes read=" + bytesRead);
            }

            AEADParameters aeadParameters = new AEADParameters(new KeyParameter(key.getKey()),
                    Constants.GCM_MAC_SIZE, nonce, nonSecretPayload);

            aeadBlockCipher.init(false, aeadParameters);

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(out, aeadBlockCipher)) {
                IOUtils.copy(in, cipherOutputStream);
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new EncryptorException(ex.getMessage(), ex);
        }
    }
}
