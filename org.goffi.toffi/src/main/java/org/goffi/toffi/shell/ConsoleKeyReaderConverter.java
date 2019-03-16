/*
 * org.goffi.toffi
 *
 * File Name: ConsoleKeyReaderConverter.java
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
package org.goffi.toffi.shell;

import org.goffi.core.domainmodel.exceptions.CoreDomainException;
import org.goffi.toffi.console.MultiFactorUserEncryptionKeyReader;
import org.goffi.toffi.console.PasswordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.List;

/**
 * Simple converter from {@link String} to {@link ConsoleKeyReader}
 */
@Component
public class ConsoleKeyReaderConverter implements Converter<ConsoleKeyReader> {

    private static final String SIMPLE_PASSWORD = "password";
    private static final String MULTI_FACTOR = "multiFactorUserEncryptionKey";

    @Autowired
    @Qualifier("outputLog")
    protected PrintStream log;

    @Override
    public boolean supports(
            @SuppressWarnings("rawtypes")
                    Class aClass,
            String s) {
        return ConsoleKeyReader.class.isAssignableFrom(aClass);
    }

    @Override
    public ConsoleKeyReader convertFromText(String cli,
            @SuppressWarnings("rawtypes")
                    Class aClass, String s1) {
        if (SIMPLE_PASSWORD.equalsIgnoreCase(cli)) {
            return new PasswordReader(log);
        } else if (MULTI_FACTOR.equalsIgnoreCase(cli)) {
            return new MultiFactorUserEncryptionKeyReader(log);
        }
        throw new CoreDomainException(
                "Unable to convert '" + cli + "' to ConsoleKeyReader");
    }

    @Override
    public boolean getAllPossibleValues(
            @SuppressWarnings("rawtypes")
                    List list,
            @SuppressWarnings("rawtypes")
                    Class aClass, String s, String s1,
            MethodTarget methodTarget) {
        return false;
    }
}
