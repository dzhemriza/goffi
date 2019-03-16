/*
 * org.goffi.toffi
 *
 * File Name: Aes.java
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
package org.goffi.core.domainmodel.crypto;

import org.goffi.core.domainmodel.crypto.exceptions.FileTransformationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransformUtils {

    /**
     * Transforms given source file into a given target based on DataTransformer
     * given
     *
     * @param source
     * @param target
     * @param dataTransformer
     * @throws FileTransformationException in case of error
     */
    public static void transformFile(String source, String target,
            DataTransformer dataTransformer) {
        transformFile(Paths.get(source), Paths.get(target), dataTransformer);
    }

    /**
     * Transforms given source file into a given target based on DataTransformer
     * given
     *
     * @param source
     * @param target
     * @param dataTransformer
     * @throws FileTransformationException in case of error
     */
    public static void transformFile(Path source, Path target,
            DataTransformer dataTransformer) {
        transformFile(source.toFile(), target.toFile(), dataTransformer);
    }

    /**
     * Transforms given source file into a given target based on DataTransformer
     * given
     *
     * @param source
     * @param target
     * @param dataTransformer
     * @throws FileTransformationException in case of error
     */
    public static void transformFile(File source, File target,
            DataTransformer dataTransformer) {
        try {
            try (FileInputStream fileInputStream = new FileInputStream(
                    source)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(
                        target)) {
                    dataTransformer
                            .transform(fileInputStream, fileOutputStream);
                }
            }
        } catch (IOException e) {
            throw new FileTransformationException(source.toPath(),
                    target.toPath(), e);
        }
    }
}
