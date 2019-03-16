/*
 * org.goffi.moffi
 *
 * File Name: Pin.java
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

/**
 * Represents a pin used for encryption
 */
public class Pin {

    private final int pin;

    private Pin(int pin) {
        this.pin = pin;
    }

    public static Pin of(int pin) {
        if (pin == 0) {
            throw new ZeroPinException("Pin must be non zero value.");
        }
        return new Pin(pin);
    }

    public int value() {
        return pin;
    }

    @Override
    public String toString() {
        return Integer.toString(pin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pin pin1 = (Pin) o;

        return pin == pin1.pin;

    }

    @Override
    public int hashCode() {
        return pin;
    }
}
