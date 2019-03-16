/*
 * org.goffi.toffi
 *
 * File Name: AesGeneralTest.java
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
package org.goffi.toffi.test.domainmodel.shell.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goffi.core.domainmodel.files.FileTestsBase;
import org.goffi.toffi.shell.ConsoleKeyReader;
import org.goffi.toffi.shell.commands.Aes;
import org.goffi.toffi.shell.exceptions.InvalidAesModeException;
import org.goffi.toffi.shell.exceptions.PathDoesntExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class AesGeneralTest extends FileTestsBase {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PrintStream outputLog;

    @Mock
    private ConsoleKeyReader consoleKeyReader;

    @InjectMocks
    private Aes aes;

    @Test(expected = PathDoesntExistsException.class)
    public void encodeCommandSourceDoNotExists() throws Exception {
        aes.encode(Paths.get(UUID.randomUUID().toString()).toFile(), null,
                "gcm", true, consoleKeyReader, null, 2);
    }

    @Test(expected = InvalidAesModeException.class)
    public void encodeInvalidMode() throws Exception {
        aes.encode(getTestFile().toFile(), null, "TEST", true,
                consoleKeyReader, null, 2);
    }

    @Test(expected = PathDoesntExistsException.class)
    public void decodeCommandSourceDoNotExists() throws Exception {
        aes.decode(Paths.get(UUID.randomUUID().toString()).toFile(), null,
                "gcm", true,
                consoleKeyReader, null, 2);
    }

    @Test(expected = InvalidAesModeException.class)
    public void decodeInvalidMode() throws Exception {
        aes.decode(getTestFile().toFile(), null, "TEST", true, consoleKeyReader,
                null, 2);
    }
}
