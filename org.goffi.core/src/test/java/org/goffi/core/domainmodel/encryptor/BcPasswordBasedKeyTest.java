/*
 * org.goffi.toffi
 *
 * File Name: BcPasswordBasedKeyTest.java
 *
 * Copyright 2016 Dzhem Riza
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class BcPasswordBasedKeyTest {

    private static final Logger LOG = LogManager.getLogger(BcPasswordBasedKeyTest.class);

    @Test
    public void keySize256() {
        BcPasswordBasedKey bcPasswordBasedKey = new BcPasswordBasedKey("test".toCharArray());
        byte[] key = bcPasswordBasedKey.getKey();
        LOG.info("Key " + Arrays.toString(key));
        Assert.assertEquals(32, key.length);
    }

    @Test
    public void keySize192() {
        BcPasswordBasedKey bcPasswordBasedKey = new BcPasswordBasedKey("test".toCharArray(), 192);
        byte[] key = bcPasswordBasedKey.getKey();
        LOG.info("Key " + Arrays.toString(key));
        Assert.assertEquals(24, key.length);
    }

    @Test
    public void keySize128() {
        BcPasswordBasedKey bcPasswordBasedKey = new BcPasswordBasedKey("test".toCharArray(), 128);
        byte[] key = bcPasswordBasedKey.getKey();
        LOG.info("Key " + Arrays.toString(key));
        Assert.assertEquals(16, key.length);
    }

    @Test
    public void keySize512() {
        BcPasswordBasedKey bcPasswordBasedKey = new BcPasswordBasedKey("test".toCharArray(), 512);
        byte[] key = bcPasswordBasedKey.getKey();
        LOG.info("Key " + Arrays.toString(key));
        Assert.assertEquals(64, key.length);
    }

    @Test
    public void keySizeDifferentIterations() {
        BcPasswordBasedKey bcPasswordBasedKey1Iterations = new BcPasswordBasedKey("test".toCharArray(), 512);
        byte[] key1 = bcPasswordBasedKey1Iterations.getKey();
        LOG.info("Key1 " + Arrays.toString(key1));
        Assert.assertEquals(64, key1.length);

        BcPasswordBasedKey bcPasswordBasedKey2Iterations = new BcPasswordBasedKey("test".toCharArray(), 512, 1024);
        byte[] key2 = bcPasswordBasedKey2Iterations.getKey();
        LOG.info("Key2 " + Arrays.toString(key2));
        Assert.assertEquals(64, key2.length);

        Assert.assertFalse(Arrays.equals(key1, key2));
    }

    @Test
    public void keySizeManyIterations() {
        BcPasswordBasedKey bcPasswordBasedKey2048Iterations = new BcPasswordBasedKey("test".toCharArray(), 512, 16384);
        byte[] key1 = bcPasswordBasedKey2048Iterations.getKey();
        LOG.info("Key1 " + Arrays.toString(key1));
        Assert.assertEquals(64, key1.length);
    }
}
