/*
 * org.goffi.toffi
 *
 * File Name: Constants.java
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

public class Constants {
    public static final byte[] DEFAULT_SALT = new byte[]{(byte) -103, (byte) 75, (byte) -27, (byte) 72, (byte) 23,
            (byte) -6, (byte) -39, (byte) 98, (byte) 6, (byte) -70, (byte) 66, (byte) -112, (byte) 17, (byte) -99,
            (byte) 61, (byte) -77, (byte) -118, (byte) -48, (byte) 20, (byte) -68, (byte) 37, (byte) 47, (byte) -13,
            (byte) 73, (byte) -76, (byte) -43, (byte) 3, (byte) -126, (byte) -37, (byte) 10, (byte) 73, (byte) -5};

    public static final int GCM_MAC_SIZE = 128;
}
