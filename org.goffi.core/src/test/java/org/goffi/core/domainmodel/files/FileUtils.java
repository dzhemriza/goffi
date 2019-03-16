/*
 * org.goffi.toffi
 *
 * File Name: FilesTest.java
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

import org.junit.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

public class FileUtils {
    protected static final Random rand = new Random();

    /**
     * Creates new directory and it's sub directories.
     *
     * @param dir directory name for creation
     */
    public static void mkdir(String dir) {
        mkdir(Paths.get(dir));
    }

    /**
     * Creates new directory and it's sub directories.
     *
     * @param dir directory for creation
     */
    public static void mkdir(Path dir) {
        File f = dir.toFile();
        if (!f.mkdirs()) {
            if (!f.exists()) {
                Assert.fail("Unable to create directory: " + dir);
            } // else directory is already created
        }
    }

    /**
     * Creates new file with random generated data and predefined size.
     *
     * @param name file name
     * @param size size of the file in bytes
     * @throws Exception
     */
    public static void touch(String name, int size) throws Exception {
        touch(Paths.get(name), size);
    }

    /**
     * Creates new file with random generated data and predefined size.
     *
     * @param name file name
     * @param size size of the file in bytes
     * @throws Exception
     */
    public static void touch(Path name, int size) throws Exception {
        File f = name.toFile();
        if (!f.exists()) {
            byte[] bytes = new byte[size];
            rand.nextBytes(bytes);

            try (FileOutputStream out = new FileOutputStream(f)) {
                out.write(bytes);
            }
        }
    }

    /**
     * Creates number of files with random data in parent directory.
     *
     * @param parentDir  parent directory
     * @param numOfFiles number of files to be created in parent directory
     * @param size       size of the files
     * @throws Exception
     */
    public static void touch(String parentDir, int numOfFiles, int size) throws
            Exception {
        touch(Paths.get(parentDir), numOfFiles, size);
    }

    /**
     * Creates number of files with random data in parent directory.
     *
     * @param parentDir  parent directory
     * @param numOfFiles number of files to be created in parent directory
     * @param size       size of the files
     * @throws Exception
     */
    public static void touch(Path parentDir, int numOfFiles, int size) throws
            Exception {
        for (int i = 0; i < numOfFiles; ++i) {
            String fileName = "file-" + UUID.randomUUID().toString()
                    + ".random.file";
            touch(Paths.get(parentDir.toString(), fileName), size);
        }
    }
}
