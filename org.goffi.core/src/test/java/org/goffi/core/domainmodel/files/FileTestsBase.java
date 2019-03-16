/*
 * org.goffi.toffi
 *
 * File Name: FilesTestBase.java
 *
 * Copyright 2014 Dzhem Riza
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
package org.goffi.core.domainmodel.files;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Test base for files package.
 */
public abstract class FileTestsBase {

    protected static final Path ROOT_DIR = Paths.get(".", "tmp-01");
    protected static final int FILE_SIZE_FACTOR = 1024; // KB
    protected static final String PASSWORD = "SwordFish";
    protected static final Path[] SUB_DIRS = new Path[]{
            Paths.get(ROOT_DIR.toString(), "a"),
            Paths.get(ROOT_DIR.toString(), "b"),
            Paths.get(ROOT_DIR.toString(), "b", "d"),
            Paths.get(ROOT_DIR.toString(), "c"),
            Paths.get(ROOT_DIR.toString(), "e")
    };

    /**
     * Creates temporary directory with pre-populated random files for testing
     * purposes.
     * <pre>
     * Directory structure:
     * ./tmp/a
     * ./tmp/b
     * ./tmp/b/d
     * ./tmp/c
     * ./tmp/e
     * </pre>
     *
     * @throws Exception
     */
    @Before
    public void setupTestDirectory() throws Exception {
        FileUtils.mkdir(ROOT_DIR);
        FileUtils.touch(ROOT_DIR, 8, FILE_SIZE_FACTOR);

        FileUtils.mkdir(SUB_DIRS[0]);
        FileUtils.touch(SUB_DIRS[0], 4,
                2 * FILE_SIZE_FACTOR);

        FileUtils.mkdir(SUB_DIRS[1]);
        FileUtils.touch(SUB_DIRS[1], 2,
                3 * FILE_SIZE_FACTOR);

        FileUtils.mkdir(SUB_DIRS[2]);
        FileUtils.touch(SUB_DIRS[2], 12, 4 * FILE_SIZE_FACTOR);

        FileUtils.mkdir(SUB_DIRS[3]);
        FileUtils.touch(SUB_DIRS[3], 6, FILE_SIZE_FACTOR);

        FileUtils.mkdir(SUB_DIRS[4]); // e is empty
    }

    /**
     * Deletes {@link #ROOT_DIR} directory and sub directories.
     *
     * @throws Exception
     */
    @After
    public void cleanupTestDirectory() throws Exception {
        org.apache.commons.io.FileUtils.deleteDirectory(ROOT_DIR.toFile());
    }

    protected void verifyDirectoriesAreIdentical(Path source,
            Path target) throws IOException {
        File[] sourceFiles = source.toFile().listFiles();
        File[] targetFiles = target.toFile().listFiles();

        int sourceLen = sourceFiles.length;
        int targetLen = targetFiles.length;

        Assert.assertEquals(sourceLen, targetLen);

        for (File sourceFile : sourceFiles) {
            boolean found = false;

            for (File targetFile : targetFiles) {
                if (sourceFile.getName().equals(targetFile.getName())) {
                    found = true;

                    if (sourceFile.isDirectory()) {
                        verifyDirectoriesAreIdentical(sourceFile.toPath(),
                                targetFile.toPath());
                    } else {
                        areSame(sourceFile, targetFile);
                    }
                }
            }

            Assert.assertTrue(found);
        }
    }

    /**
     * Verifies does 2 files are the same using sha1 sum
     *
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    protected void areSame(File sourceFile, File targetFile) throws
            IOException {
        try (FileInputStream sourceFileIn = new FileInputStream(sourceFile)) {
            try (FileInputStream targetFileIn = new FileInputStream(
                    targetFile)) {
                byte[] sourceFileSha1 = DigestUtils.sha1(sourceFileIn);
                byte[] targetFileSha1 = DigestUtils.sha1(targetFileIn);
                Assert.assertArrayEquals(sourceFileSha1, targetFileSha1);
            }
        }
    }

    protected void areNotSame(File sourceFile, File targetFile) throws
            IOException {
        try {
            areSame(sourceFile, targetFile);
        } catch (AssertionError e) {
            // Files are not the same which was what we wanted
            return;
        }

        Assert.fail("Files are same!");
    }

    /**
     * @return Random generated file name
     */
    protected Path getTestFileName() {
        return Paths.get(ROOT_DIR.toString(), UUID.randomUUID().toString()
                + ".random.file");
    }

    /**
     * @return New file of size 0
     * @throws Exception
     */
    protected Path getTestFile() throws Exception {
        return getTestFile(0);
    }

    /**
     * @param size File size in bytes
     * @return New file
     * @throws Exception
     */
    protected Path getTestFile(int size) throws Exception {
        Path file = getTestFileName();
        FileUtils.touch(file, size);
        return file;
    }
}
