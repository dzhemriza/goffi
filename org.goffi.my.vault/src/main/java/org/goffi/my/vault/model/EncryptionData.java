/*
 * org.goffi.my.vault
 *
 * File Name: EncryptionData.java
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
package org.goffi.my.vault.model;

import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.Key;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesEncoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesEncoder;

import java.util.function.Function;

public class EncryptionData {

    public enum Mode {
        GCM(BcGcmAesEncoder::new, BcGcmAesDecoder::new),
        EAX(BcEaxAesEncoder::new, BcEaxAesDecoder::new);

        private Function<Key, DataTransformer> encoder;
        private Function<Key, DataTransformer> decoder;

        Mode(Function<Key, DataTransformer> encoder,
                Function<Key, DataTransformer> decoder) {
            this.encoder = encoder;
            this.decoder = decoder;
        }

        Function<Key, DataTransformer> encoder() {
            return encoder;
        }

        Function<Key, DataTransformer> decoder() {
            return decoder;
        }
    }

    private UserEncryptionKey userEncryptionKey;
    private Mode mode;

    public UserEncryptionKey getUserEncryptionKey() {
        return userEncryptionKey;
    }

    public void setUserEncryptionKey(
            UserEncryptionKey userEncryptionKey) {
        this.userEncryptionKey = userEncryptionKey;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public DataTransformer createEncoder() {
        return this.mode.encoder().apply(new BcPasswordBasedKey(
                userEncryptionKey.getCombinedKey()));
    }

    public DataTransformer createDecoder() {
        return this.mode.decoder().apply(new BcPasswordBasedKey(
                userEncryptionKey.getCombinedKey()));
    }
}
