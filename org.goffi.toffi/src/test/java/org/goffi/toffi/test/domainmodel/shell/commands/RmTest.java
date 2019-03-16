/*
 * org.goffi.toffi
 *
 * File Name: RmTest.java
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

import org.goffi.core.domainmodel.files.FileTestsBase;
import org.goffi.toffi.shell.commands.Rm;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;

@RunWith(MockitoJUnitRunner.class)
public class RmTest extends FileTestsBase {

    private final static int NUMBER_OF_PASSES = 2;
    private final static int BLOCK_SIZE = 1024;

    @Mock
    private PrintStream outputLog;

    @InjectMocks
    private Rm rm;

    @Test
    public void deleteDirectory() {
        Path dirToDelete = SUB_DIRS[0];

        Assert.assertTrue(dirToDelete.toFile().exists());
        rm.rm(new File[] { dirToDelete.toFile() }, NUMBER_OF_PASSES,
                BLOCK_SIZE);
        Assert.assertFalse(dirToDelete.toFile().exists());
    }

    @Test
    public void deleteAllSubDirectories() {
        File[] subDirs = new File[] { SUB_DIRS[0].toFile(),
                SUB_DIRS[1].toFile() };

        for (File dirToDelete : subDirs) {
            Assert.assertTrue(dirToDelete.exists());
        }

        rm.rm(subDirs, NUMBER_OF_PASSES, BLOCK_SIZE);

        for (File dirToDelete : subDirs) {
            Assert.assertFalse(dirToDelete.exists());
        }
    }

    @Test
    public void deleteFile() {
        File[] files = SUB_DIRS[0].toFile().listFiles();

        File firstFile = files[0];
        Assert.assertTrue(firstFile.exists());

        rm.rm(new File[] { firstFile }, NUMBER_OF_PASSES, BLOCK_SIZE);

        Assert.assertFalse(firstFile.exists());
    }
}
