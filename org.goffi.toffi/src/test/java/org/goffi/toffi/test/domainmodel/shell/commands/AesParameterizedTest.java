/*
 * org.goffi.toffi
 *
 * File Name: AesParameterizedTest.java
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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AesParameterizedTest extends FileTestsBase {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PrintStream outputLog;

    @Mock
    private ConsoleKeyReader consoleKeyReader;

    @InjectMocks
    private Aes aes;

    private final String mode;
    private final boolean zip;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "gcm", true }, { "eax", true },
                { "gcm", false }, { "eax", false } });
    }

    public AesParameterizedTest(String mode, boolean zip) {
        this.mode = mode;
        this.zip = zip;
    }

    @Test
    public void encodeDecodeFileE2E() throws Exception {
        Path source = getTestFile(100);
        Path target = getTestFileName();
        Path mirrorOfSource = getTestFileName();

        Mockito.when(consoleKeyReader.read(Mockito.anyBoolean()))
                .thenReturn(PASSWORD.toCharArray());
        aes.encode(source.toFile(), target.toFile(), mode, zip,
                consoleKeyReader, null, 2);

        Assert.assertTrue(source.toFile().exists());
        Assert.assertTrue(target.toFile().exists());
        Assert.assertTrue(target.toFile().isFile());
        areNotSame(source.toFile(), target.toFile());

        aes.decode(target.toFile(), mirrorOfSource.toFile(), mode, zip,
                consoleKeyReader, null, 2);

        if (zip) {
            // For the zip case we need to get into the newly created dir
            areSame(source.toFile(), Paths.get(mirrorOfSource.toString(),
                    source.getFileName().toString()).toFile());
        } else {
            // For non zip case we need to use the decoded file
            areSame(source.toFile(), mirrorOfSource.toFile());
        }
    }

    @Test
    public void encodeDecodeDirectoryE2E() throws Exception {
        Path source = SUB_DIRS[0]; // use ./tmp-01/a
        Path target = getTestFileName();

        Assert.assertTrue(source.toFile().exists());
        Assert.assertTrue(source.toFile().isDirectory());

        aes.encode(source.toFile(), target.toFile(), mode, zip,
                consoleKeyReader, null, 2);

        Path mirrorOfSource = getTestFileName();

        if (zip) {
            Assert.assertTrue(target.toFile().exists());
            Assert.assertTrue(target.toFile().isFile());
        }

        aes.decode(target.toFile(), mirrorOfSource.toFile(), mode, zip,
                consoleKeyReader, null, 2);

        verifyDirectoriesAreIdentical(source, mirrorOfSource);
    }
}
