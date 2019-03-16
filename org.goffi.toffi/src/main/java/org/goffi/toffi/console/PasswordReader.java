/*
 * org.goffi.toffi
 *
 * File Name: PasswordReader.java
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
package org.goffi.toffi.console;

import jline.console.ConsoleReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.Password;
import org.goffi.core.domainmodel.exceptions.CoreDomainException;
import org.goffi.toffi.shell.ConsoleKeyReader;

import java.io.IOException;
import java.io.PrintStream;

public class PasswordReader implements ConsoleKeyReader {

    private static final Logger LOG = LogManager.getLogger(PasswordReader.class);
    private final PrintStream log;

    public PasswordReader(PrintStream log) {
        this.log = log;
    }

    @Override
    public char[] read(boolean askForConfirmation) {
        try {
            log.println("Please enter password: ");
            ConsoleReader consoleReader = new ConsoleReader();
            String password = consoleReader.readLine('*');

            if (askForConfirmation) {
                log.println("Please re-enter password: ");
                String password2 = consoleReader.readLine('*');

                Password p = Password.of(password.toCharArray(), password2.toCharArray());

                return p.getPassword();
            }

            return Password.of(password.toCharArray()).getPassword();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new CoreDomainException(e.getMessage(), e);
        }
    }
}
