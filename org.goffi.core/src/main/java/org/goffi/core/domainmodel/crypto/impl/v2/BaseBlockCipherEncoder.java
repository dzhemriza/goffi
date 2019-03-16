/*
 * org.goffi.toffi
 *
 * File Name: BaseBlockCipherEncoder.java
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

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.Key;
import org.goffi.core.domainmodel.crypto.exceptions.EncryptorException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Advantage of using version 2 Encoder/Decoder is that the nonce value
 * is randomly generated and prepended to the cipher text. Which means the
 * client is not gonna need to manual remember the nonce.
 */
public abstract class BaseBlockCipherEncoder implements DataTransformer {

    private static final Logger LOG = LogManager.getLogger(BaseBlockCipherEncoder.class);
    private final Key key;
    private final byte[] nonce;
    private final byte[] nonSecretPayload;

    public BaseBlockCipherEncoder(Key key, byte[] nonce, byte[] nonSecretPayload) {
        this.key = key;
        this.nonce = nonce;
        this.nonSecretPayload = nonSecretPayload;
    }

    public BaseBlockCipherEncoder(Key key, byte[] nonce) {
        this(key, nonce, Constants.DEFAULT_SALT);
    }

    public BaseBlockCipherEncoder(Key key, int nonce) {
        this(key, ByteBuffer.allocate(4).putInt(nonce).array());
    }

    public BaseBlockCipherEncoder(Key key) {
        this(key, Utils.randomNonce(), Constants.DEFAULT_SALT);
    }

    protected abstract AEADBlockCipher createAEADBlockCipher();

    @Override
    public void transform(InputStream in, OutputStream out) {
        try {
            AEADBlockCipher aeadBlockCipher = createAEADBlockCipher();

            AEADParameters aeadParameters = new AEADParameters(new KeyParameter(key.getKey()),
                    Constants.GCM_MAC_SIZE, nonce, nonSecretPayload);

            aeadBlockCipher.init(true, aeadParameters);

            // Write nonce as first 4 bytes
            out.write(this.nonce);
            try (CipherInputStream cipherInputStream = new CipherInputStream(in, aeadBlockCipher)) {
                IOUtils.copy(cipherInputStream, out);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new EncryptorException(ex.getMessage(), ex);
        }
    }
}
