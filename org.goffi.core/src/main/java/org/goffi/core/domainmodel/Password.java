/*
 * org.goffi.moffi
 *
 * File Name: Password.java
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

import java.util.Arrays;

/**
 * Object enforcing password creation
 */
public class Password {

    private final char[] password;

    private Password(char[] password) {
        this.password = password;
    }

    public static Password of(char[] password) {
        if (password.length < 6) {
            throw new WeakPasswordException("Password must be at least 6 symbols!");
        }
        return new Password(password);
    }

    /**
     * Enforces the domain knowledge that the password and the repeatedPassword must be same.
     *
     * @param password
     * @param repeatedPassword
     * @return
     */
    public static Password of(char[] password, char[] repeatedPassword) {
        if (!Arrays.equals(password, repeatedPassword)) {
            throw new RepeatedPasswordDontMatchException("Repeated password doesn't match");
        }
        return of(password);
    }

    public static Password empty() {
        return new Password(new char[0]);
    }

    @Override
    public String toString() {
        return "Password{...}'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Password password1 = (Password) o;

        return Arrays.equals(password, password1.password);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(password);
    }

    public char[] getPassword() {
        return this.password;
    }

    public int length() {
        return this.password.length;
    }
}