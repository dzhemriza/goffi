/*
 * org.goffi.toffi
 *
 * File Name: CryptographyTest.java
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesEncoder;
import org.goffi.core.domainmodel.files.FileTestsBase;
import org.goffi.toffi.domainmodel.files.DirectoryDecoder;
import org.goffi.toffi.domainmodel.files.DirectoryEncoder;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class DirectoryEncoderTest extends FileTestsBase {

    protected static final String ROOT_DIR2 = "." + FileSystems.getDefault().getSeparator() + "tmp-02";
    protected static final String ROOT_DIR3 = "." + FileSystems.getDefault().getSeparator() + "tmp-03";

    @After
    public void cleanupTestDirectory2() throws Exception {
        org.apache.commons.io.FileUtils.deleteDirectory(new File(ROOT_DIR2));
        org.apache.commons.io.FileUtils.deleteDirectory(new File(ROOT_DIR3));
    }

    private void testDirectoryEncodeDecodeInitialSync(DataTransformer encoder, DataTransformer decoder) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        DirectoryEncoder directoryEncoder = new DirectoryEncoder(encoder, objectMapper);
        directoryEncoder.encode(ROOT_DIR, Paths.get(ROOT_DIR2));

        DirectoryDecoder directoryDecoder = new DirectoryDecoder(decoder, objectMapper);
        directoryDecoder.decode(Paths.get(ROOT_DIR2), Paths.get(ROOT_DIR3));

        // Analyze tmp-01 and tmp-03 directories all the files
        verifyDirectoriesAreIdentical(ROOT_DIR, Paths.get(ROOT_DIR3));
    }

    @Test
    public void testBcAesEncodeDecodeDirectoryInitialSyncV2Encryption() throws Exception {
        BcPasswordBasedKey key = new BcPasswordBasedKey(PASSWORD.toCharArray());
        testDirectoryEncodeDecodeInitialSync(new BcGcmAesEncoder(key),
                new BcGcmAesDecoder(key));
    }
}
