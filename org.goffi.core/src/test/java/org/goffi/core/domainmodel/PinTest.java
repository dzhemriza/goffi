/*
 * org.goffi.moffi
 *
 * File Name: PinTest.java
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

import org.goffi.core.domainmodel.exceptions.ZeroPinException;
import org.junit.Test;

public class PinTest {

    @Test
    public void testPinGeneralCase() {
        Pin.of(1);
        // No exception
    }

    @Test(expected = ZeroPinException.class)
    public void testZeroPin() {
        Pin.of(0);
        // No exception
    }
}
