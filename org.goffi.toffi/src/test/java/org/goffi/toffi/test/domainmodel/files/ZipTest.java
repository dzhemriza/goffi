/*
 * org.goffi.toffi
 *
 * File Name: ZipTest.java
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

import org.goffi.core.domainmodel.files.FileTestsBase;
import org.goffi.toffi.domainmodel.files.Zip;
import org.goffi.toffi.domainmodel.files.exceptions.GeneralZipException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.goffi.core.domainmodel.files.FileUtils.mkdir;

public class ZipTest extends FileTestsBase {

    protected static final String ROOT_DIR2 = "." + FileSystems.getDefault().getSeparator() + "tmp-02";
    protected static final String ROOT_DIR3 = "." + FileSystems.getDefault().getSeparator() + "tmp-03";
    protected static final String ROOT_DIR4 = "." + FileSystems.getDefault().getSeparator() + "tmp-04";

    @Before
    public void setup() throws Exception {
        mkdir(ROOT_DIR2);
        mkdir(ROOT_DIR3);
        mkdir(ROOT_DIR4);
    }

    @After
    public void cleanupTestDirectory2() throws Exception {
        org.apache.commons.io.FileUtils.deleteDirectory(new File(ROOT_DIR2));
        org.apache.commons.io.FileUtils.deleteDirectory(new File(ROOT_DIR3));
        org.apache.commons.io.FileUtils.deleteDirectory(new File(ROOT_DIR4));
    }

    @Test
    public void testZipEmptyDirectory() throws Exception {
        Path subDir = Paths.get(ROOT_DIR2, "a");
        subDir.toFile().mkdirs();

        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR3, "zipped-root-dir.zip");
        zip.zip(Paths.get(ROOT_DIR2), target);

        Assert.assertTrue(target.toFile().exists());

        zip.unzip(target, Paths.get(ROOT_DIR4));

        // Analyze tmp-01 and tmp-03 directories all the files
        verifyDirectoriesAreIdentical(Paths.get(ROOT_DIR2), Paths.get(ROOT_DIR4));
    }

    @Test
    public void testZipDirectory() throws Exception {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, "zipped-root-dir.zip");
        zip.zip(ROOT_DIR, target);

        Assert.assertTrue(target.toFile().exists());

        zip.unzip(target, Paths.get(ROOT_DIR3));

        // Analyze tmp-01 and tmp-03 directories all the files
        verifyDirectoriesAreIdentical(ROOT_DIR, Paths.get(ROOT_DIR3));
    }

    @Test(expected = GeneralZipException.class)
    public void testSourceDoesntExist() {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, UUID.randomUUID().toString());
        zip.zip(Paths.get(UUID.randomUUID().toString()), target);
    }

    @Test(expected = GeneralZipException.class)
    public void testZipDirectoryTargetDoesntExists() {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, "aa", "bb", "zipped-root-dir.zip");
        zip.zip(ROOT_DIR, target);
    }

    @Test
    public void testZipDirectoryAbsoluteSource() {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, "zipped-root-dir.zip");
        zip.zip(ROOT_DIR.toAbsolutePath(), target);

        Assert.assertTrue(target.toFile().exists());
    }

    @Test
    public void testZipDirectoryAbsoluteTarget() {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, "zipped-root-dir.zip").toAbsolutePath();
        zip.zip(ROOT_DIR, target);

        Assert.assertTrue(target.toFile().exists());
    }

    @Test
    public void testZipDirectoryAbsoluteSourceAndTarget() {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, "zipped-root-dir.zip").toAbsolutePath();
        zip.zip(ROOT_DIR.toAbsolutePath(), target);

        Assert.assertTrue(target.toFile().exists());
    }

    @Test
    public void testZipFile() throws Exception {
        Zip zip = new Zip();
        Path target = Paths.get(ROOT_DIR2, "zipped-file.zip");

        Path source = null;

        Path testDir = Paths.get(ROOT_DIR.toString(), "a");

        for (File file : testDir.toFile().listFiles()) {
            if (file.isFile()) {
                source = file.toPath();
                break;
            }
        }

        zip.zip(source, target);
        Assert.assertTrue(target.toFile().exists());

        zip.unzip(target, Paths.get(ROOT_DIR3));

        Path unzippedFile = Paths.get(ROOT_DIR3, source.getFileName().toString());

        areSame(source.toFile(), unzippedFile.toFile());
    }

    @Test(expected = GeneralZipException.class)
    public void testUnzipMissingFIle() throws Exception {
        Zip zip = new Zip();
        zip.unzip(Paths.get(UUID.randomUUID().toString(), UUID.randomUUID().toString()), Paths.get(ROOT_DIR3));
    }
}
