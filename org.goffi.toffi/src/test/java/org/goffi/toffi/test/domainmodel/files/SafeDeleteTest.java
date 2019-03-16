/*
 * org.goffi.toffi
 *
 * File Name: SafeDeleteTest.java
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
package org.goffi.toffi.test.domainmodel.files;

import org.apache.commons.codec.digest.DigestUtils;
import org.goffi.core.domainmodel.files.FileTestsBase;
import org.goffi.core.domainmodel.files.FileUtils;
import org.goffi.toffi.domainmodel.exceptions.ToffiException;
import org.goffi.toffi.domainmodel.files.RandomBytesNoiseGenerator;
import org.goffi.toffi.domainmodel.files.SafeDelete;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class SafeDeleteTest extends FileTestsBase {

    @Test(expected = ToffiException.class)
    public void testGenerateNoiseMissingFile() throws Exception {
        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.generateNoise(Paths.get("qwerty9897867655ytrtr")); // random
                                                                      // string
    }

    @Test(expected = ToffiException.class)
    public void testDeleteFileMissingFile() throws Exception {
        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.delete(Paths.get("qwerty9897867655ytrtr")); // random
                                                               // string
    }

    @Test(expected = ToffiException.class)
    public void testGenerateNoiseDirectory() throws Exception {
        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.generateNoise(ROOT_DIR);
    }

    @Test
    public void testGenerateNoiseOnFile() throws Exception {
        UUID uuid = UUID.randomUUID();
        Path fileUnderTest = Paths.get(ROOT_DIR.toString(), uuid.toString());
        FileUtils.touch(fileUnderTest.toString(), 10 * FILE_SIZE_FACTOR);

        byte[] sha1SumBefore;
        try (FileInputStream fileInputStream = new FileInputStream(
                fileUnderTest.toFile())) {
            sha1SumBefore = DigestUtils.sha1(fileInputStream);
        }
        long fileLengthBefore = fileUnderTest.toFile().length();

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.generateNoise(fileUnderTest);

        byte[] sha1SumAfter;
        try (FileInputStream fileInputStream = new FileInputStream(
                fileUnderTest.toFile())) {
            sha1SumAfter = DigestUtils.sha1(fileInputStream);
        }
        long fileLengthAfter = fileUnderTest.toFile().length();

        Assert.assertThat(sha1SumBefore,
                IsNot.not(IsEqual.equalTo(sha1SumAfter)));
        Assert.assertEquals(fileLengthBefore, fileLengthAfter);
    }

    @Test
    public void testGenerateNoiseReadOnlyFile() throws Exception {
        UUID uuid = UUID.randomUUID();
        Path fileUnderTest = Paths.get(ROOT_DIR.toString(), uuid.toString());
        FileUtils.touch(fileUnderTest.toString(), 10 * FILE_SIZE_FACTOR);

        fileUnderTest.toFile().setWritable(false);

        byte[] sha1SumBefore;
        try (FileInputStream fileInputStream = new FileInputStream(
                fileUnderTest.toFile())) {
            sha1SumBefore = DigestUtils.sha1(fileInputStream);
        }
        long fileLengthBefore = fileUnderTest.toFile().length();

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.generateNoise(fileUnderTest);

        byte[] sha1SumAfter;
        try (FileInputStream fileInputStream = new FileInputStream(
                fileUnderTest.toFile())) {
            sha1SumAfter = DigestUtils.sha1(fileInputStream);
        }
        long fileLengthAfter = fileUnderTest.toFile().length();

        Assert.assertThat(sha1SumBefore,
                IsNot.not(IsEqual.equalTo(sha1SumAfter)));
        Assert.assertEquals(fileLengthBefore, fileLengthAfter);
    }

    @Test
    public void testGenerateNoiseSmallFile() throws Exception {
        UUID uuid = UUID.randomUUID();
        Path fileUnderTest = Paths.get(ROOT_DIR.toString(), uuid.toString());
        FileUtils.touch(fileUnderTest.toString(), 10);

        byte[] sha1SumBefore;
        try (FileInputStream fileInputStream = new FileInputStream(
                fileUnderTest.toFile())) {
            sha1SumBefore = DigestUtils.sha1(fileInputStream);
        }
        long fileLengthBefore = fileUnderTest.toFile().length();

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.generateNoise(fileUnderTest);

        byte[] sha1SumAfter;
        try (FileInputStream fileInputStream = new FileInputStream(
                fileUnderTest.toFile())) {
            sha1SumAfter = DigestUtils.sha1(fileInputStream);
        }
        long fileLengthAfter = fileUnderTest.toFile().length();

        Assert.assertThat(sha1SumBefore,
                IsNot.not(IsEqual.equalTo(sha1SumAfter)));
        Assert.assertEquals(fileLengthBefore, fileLengthAfter);
    }

    @Test
    public void testSafeFileDeleteFile() throws Exception {
        UUID uuid = UUID.randomUUID();
        Path fileUnderTest = Paths.get(ROOT_DIR.toString(), uuid.toString());
        FileUtils.touch(fileUnderTest.toString(), 10);

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.deleteFile(fileUnderTest);

        Assert.assertFalse(fileUnderTest.toFile().exists());
    }

    @Test
    public void testSafeDeleteDirectory() throws Exception {
        Path directoryUnderTest = Paths.get(ROOT_DIR.toString(), "b");
        Assert.assertTrue(directoryUnderTest.toFile().exists());
        Assert.assertTrue(directoryUnderTest.toFile().isDirectory());

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.deleteTree(directoryUnderTest);

        Assert.assertFalse(directoryUnderTest.toFile().exists());
    }

    @Test
    public void testSafeFileDeleteUsingDelete() throws Exception {
        UUID uuid = UUID.randomUUID();
        Path fileUnderTest = Paths.get(ROOT_DIR.toString(), uuid.toString());
        FileUtils.touch(fileUnderTest.toString(), 10);

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.delete(fileUnderTest);

        Assert.assertFalse(fileUnderTest.toFile().exists());
    }

    @Test
    public void testSafeDeleteDirectoryUsingDelete() throws Exception {
        Path directoryUnderTest = Paths.get(ROOT_DIR.toString(), "b");
        Assert.assertTrue(directoryUnderTest.toFile().exists());
        Assert.assertTrue(directoryUnderTest.toFile().isDirectory());

        RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);
        safeDelete.delete(directoryUnderTest);

        Assert.assertFalse(directoryUnderTest.toFile().exists());
    }
}
