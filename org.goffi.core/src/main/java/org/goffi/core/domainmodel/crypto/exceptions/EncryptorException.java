/*
 * org.goffi.toffi
 *
 * File Name: EncryptorException.java
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
package org.goffi.core.domainmodel.crypto.exceptions;

import org.goffi.core.domainmodel.exceptions.CoreDomainException;

/**
 * Domain model specific {@link RuntimeException} for encryptor package.
 */
public class EncryptorException extends CoreDomainException {

    private static final long serialVersionUID = 8070276377822986541L;

    public EncryptorException(String msg) {
        super(msg);
    }

    public EncryptorException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
