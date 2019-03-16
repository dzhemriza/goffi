/*
 * org.goffi.toffi
 *
 * File Name: FileEncryptionTest.java
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
package org.goffi.core.domainmodel.encryptor;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.TransformUtils;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesEncoder;
import org.goffi.core.domainmodel.files.FileTestsBase;
import org.goffi.core.domainmodel.files.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileEncryptionTest extends FileTestsBase {

    private static final Logger LOG = LogManager.getLogger(FileEncryptionTest.class);

    private static final String FILE1 = ROOT_DIR + File.separator + "file1.random";
    private static final String FILE2 = ROOT_DIR + File.separator + "file1.random.encrypted";
    private static final String FILE3 = ROOT_DIR + File.separator + "file1.random.encrypted.decrypted";

    @Before
    public void setup() throws Exception {
        FileUtils.touch(FILE1, FILE_SIZE_FACTOR * 10);
    }

    private void testCrypto(DataTransformer encoder, DataTransformer decoder) throws IOException {
        LOG.info("Test, encoder: " + encoder.getClass().getCanonicalName() + ", decoder: "
                + decoder.getClass().getCanonicalName());

        byte[] file1Sha1;
        try (FileInputStream fileInputStream = new FileInputStream(FILE1)) {
            file1Sha1 = DigestUtils.sha1(fileInputStream);
            LOG.info("File 1 sha1 " + Arrays.toString(file1Sha1));
        }

        TransformUtils.transformFile(FILE1, FILE2, encoder);

        byte[] file2Sha1;
        try (FileInputStream fileInputStream = new FileInputStream(FILE2)) {
            file2Sha1 = DigestUtils.sha1(fileInputStream);
            LOG.info("File 2 sha1 " + Arrays.toString(file2Sha1));
        }

        Assert.assertFalse(Arrays.equals(file1Sha1, file2Sha1));

        TransformUtils.transformFile(FILE2, FILE3, decoder);

        byte[] file3Sha1;
        try (FileInputStream fileInputStream = new FileInputStream(FILE3)) {
            file3Sha1 = DigestUtils.sha1(fileInputStream);
            LOG.info("File 3 sha1 " + Arrays.toString(file3Sha1));
        }

        Assert.assertArrayEquals(file1Sha1, file3Sha1);
        Assert.assertFalse(Arrays.equals(file2Sha1, file3Sha1));
    }

    @Test
    public void testBcGcmAesFileEncoderAndAesFileDecoderV2Crypto() throws Exception {
        BcPasswordBasedKey key = new BcPasswordBasedKey(PASSWORD.toCharArray());
        testCrypto(new BcGcmAesEncoder(key),
                new BcGcmAesDecoder(key));
    }
}
