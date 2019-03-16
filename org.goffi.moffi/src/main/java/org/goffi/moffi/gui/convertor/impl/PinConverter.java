/*
 * org.goffi.moffi
 *
 * File Name: PinConverter.java
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
package org.goffi.moffi.gui.convertor.impl;

import org.apache.commons.lang3.StringUtils;
import org.goffi.core.domainmodel.Pin;
import org.goffi.moffi.gui.convertor.VisualDataConverter;
import org.goffi.moffi.gui.convertor.impl.exceptions.EmptyPinException;
import org.goffi.moffi.gui.convertor.impl.exceptions.NonNumericPinValueException;

public class PinConverter implements VisualDataConverter<Pin, char[]> {

    @Override
    public Pin convert(char[] value) {
        if (0 == value.length) {
            throw new EmptyPinException();
        }

        String valueOf = String.valueOf(value); // don't like this

        if (!StringUtils.isNumeric(valueOf)) {
            throw new NonNumericPinValueException();
        }

        return Pin.of(Integer.parseInt(valueOf));
    }
}
