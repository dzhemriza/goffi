/*
 * org.goffi.moffi
 *
 * File Name: PasswordTest.java
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

import org.goffi.core.domainmodel.exceptions.RepeatedPasswordDontMatchException;
import org.goffi.core.domainmodel.exceptions.WeakPasswordException;
import org.junit.Test;

public class PasswordTest {

    @Test
    public void testPasswordGeneralCasePasswordAndRepeatedPassword() {
        Password.of("SwordFish".toCharArray(), "SwordFish".toCharArray());
        // No exception both passwords match
    }

    @Test
    public void testPasswordGeneralCaseSinglePasswordOnly() {
        Password.of("SwordFish".toCharArray());
        // No exception both passwords match
    }

    @Test(expected = WeakPasswordException.class)
    public void testWeakPasswordException() {
        Password.of("a".toCharArray());
        // No exception both passwords match
    }

    @Test(expected = RepeatedPasswordDontMatchException.class)
    public void testPasswordMissmatch() {
        Password.of("123456".toCharArray(), "654321".toCharArray());
    }

    @Test(expected = RepeatedPasswordDontMatchException.class)
    public void testPasswordMissmatch2() {
        Password.of("123456".toCharArray(), "1".toCharArray());
    }

    @Test(expected = WeakPasswordException.class)
    public void testWeakPassword2() {
        Password.of("1".toCharArray(), "1".toCharArray());
    }
}
