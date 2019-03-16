/*
 * org.goffi.toffi
 *
 * File Name: MultiFactoryUserEncryptionKeyReader.java
 *
 * Copyright 2018 Dzhem Riza
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
import org.goffi.core.domainmodel.MemorableWord;
import org.goffi.core.domainmodel.Password;
import org.goffi.core.domainmodel.Pin;
import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.exceptions.CoreDomainException;
import org.goffi.toffi.shell.ConsoleKeyReader;

import java.io.IOException;
import java.io.PrintStream;

public class MultiFactorUserEncryptionKeyReader implements ConsoleKeyReader {

    private static final Logger LOG =
            LogManager.getLogger(MultiFactorUserEncryptionKeyReader.class);
    private final PrintStream log;

    public MultiFactorUserEncryptionKeyReader(PrintStream log) {
        this.log = log;
    }

    @Override
    public char[] read(boolean askForConfirmation) {
        try {
            ConsoleReader consoleReader = new ConsoleReader();

            Password password;

            log.println("Please enter password: ");
            String passwordStr = consoleReader.readLine('*');

            if (askForConfirmation) {
                log.println("Please re-enter password: ");
                String passwordStrConfirm = consoleReader.readLine('*');

                password = Password.of(passwordStr.toCharArray(),
                        passwordStrConfirm.toCharArray());
            } else {
                password = Password.of(passwordStr.toCharArray());
            }

            log.println("Please enter memorable word (ex. favorite colour, " +
                    "first car): ");
            String memorableWordStr = consoleReader.readLine('*');

            MemorableWord memorableWord = MemorableWord.of(
                    memorableWordStr.toCharArray());

            log.println("Please enter Pin (32bit integer): ");
            String pinStr = consoleReader.readLine('*');
            Pin pin = Pin.of(Integer.parseInt(pinStr));

            return new UserEncryptionKey(password, pin, memorableWord)
                    .getCombinedKey();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new CoreDomainException(e.getMessage(), e);

        }
    }
}
