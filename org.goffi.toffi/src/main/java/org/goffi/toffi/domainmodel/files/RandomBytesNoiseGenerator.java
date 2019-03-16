/*
 * org.goffi.toffi
 *
 * File Name: RandomBytesNoiseGenerator.java
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
package org.goffi.toffi.domainmodel.files;

import java.util.Random;

public class RandomBytesNoiseGenerator implements NoiseGenerator {

    private final Random random = new Random();

    @Override
    public void generate(byte[] bytes) {
        random.nextBytes(bytes);
    }
}
