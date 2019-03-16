/*
 * org.goffi.toffi
 *
 * File Name: DirectoryEncoder.java
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

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.toffi.domainmodel.files.exceptions.FileAccessException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class SafeDelete {

    private static final Logger LOG = LogManager.getLogger(SafeDelete.class);
    public static final int DEFAULT_BLOCK_SIZE = 2048;
    public static final int DEFAULT_NUMBER_OF_PASSES = 35;
    private final NoiseGenerator noiseGenerator;

    public SafeDelete(NoiseGenerator noiseGenerator) {
        this.noiseGenerator = noiseGenerator;
    }

    public void deleteFile(Path fileToDelete) {
        deleteFile(fileToDelete, DEFAULT_NUMBER_OF_PASSES, DEFAULT_BLOCK_SIZE);
    }

    public void deleteFile(File fileToDelete) {
        deleteFile(fileToDelete.toPath(), DEFAULT_NUMBER_OF_PASSES,
                DEFAULT_BLOCK_SIZE);
    }

    public void generateNoise(Path sourceFile) {
        generateNoise(sourceFile, DEFAULT_NUMBER_OF_PASSES, DEFAULT_BLOCK_SIZE);
    }

    public void deleteFile(File fileToDelete, int numberOfPasses,
            int blockSize) {
        deleteFile(fileToDelete.toPath(), numberOfPasses, blockSize);
    }

    public void deleteFile(Path fileToDelete, int numberOfPasses,
            int blockSize) {
        generateNoise(fileToDelete, numberOfPasses, blockSize);

        if (!fileToDelete.toFile().delete()) {
            throw new FileAccessException("Unable to delete - " + fileToDelete);
        }
    }

    public void deleteTree(Path source) {
        deleteTree(source, DEFAULT_NUMBER_OF_PASSES, DEFAULT_BLOCK_SIZE);
    }

    public void deleteTree(File source) {
        deleteTree(source.toPath(), DEFAULT_NUMBER_OF_PASSES,
                DEFAULT_BLOCK_SIZE);
    }

    public void delete(File source) {
        delete(source, DEFAULT_NUMBER_OF_PASSES, DEFAULT_BLOCK_SIZE);
    }

    public void delete(Path source) {
        delete(source, DEFAULT_NUMBER_OF_PASSES, DEFAULT_BLOCK_SIZE);
    }

    public void delete(Path source, int numberOfPasses, int blockSize) {
        delete(source.toFile(), numberOfPasses, blockSize);
    }

    public void delete(File source, int numberOfPasses, int blockSize) {
        if (source.isFile()) {
            deleteFile(source, numberOfPasses, blockSize);
        } else {
            deleteTree(source, numberOfPasses, blockSize);
        }
    }

    public void deleteTree(File source, int numberOfPasses, int blockSize) {
        deleteTree(source.toPath(), numberOfPasses, blockSize);
    }

    public void deleteTree(Path source, int numberOfPasses, int blockSize) {
        final SafeDelete safeDelete = this;

        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attrs) throws IOException {

                    LOG.debug("Deleting file safely: " + file);
                    safeDelete.deleteFile(file, numberOfPasses, blockSize);

                    return super.visitFile(file, attrs);
                }
            });

            FileUtils.deleteDirectory(source.toFile());

        } catch (IOException ioException) {
            LOG.error(ioException.getMessage(), ioException);
            throw new FileAccessException(
                    "IO error while deleting tree - " + source + ", ",
                    ioException);
        }
    }

    public void generateNoise(Path sourceFile, int numberOfPasses,
            int blockSize) {
        File file = sourceFile.toFile();

        if (!file.exists()) {
            throw new FileAccessException(
                    "File doesn't exists - " + sourceFile);
        }

        if (!file.isFile()) {
            throw new FileAccessException(
                    "Provided file path is not a file - " + sourceFile);
        }

        if (blockSize <= 0) {
            throw new IllegalArgumentException(
                    "Zero or negative blockSize argument.");
        }

        // Before we start we should make sure that the file is writable
        if (!file.setWritable(true)) {
            LOG.info("Unable to set file '{}' flag to writable", sourceFile);
        }

        final long originalFileLength = file.length();
        byte[] noise = new byte[blockSize];

        try {
            for (int pass = 0; pass < numberOfPasses; ++pass) {
                try (RandomAccessFile randomAccessFile =
                             new RandomAccessFile(file, "rw")) {
                    for (long block = 0;
                         block < originalFileLength;
                         block += blockSize) {
                        // Generate some noise
                        noiseGenerator.generate(noise);

                        if (originalFileLength - block > blockSize) {
                            randomAccessFile.write(noise);
                        } else {
                            randomAccessFile.write(noise, 0,
                                    Math.toIntExact(
                                            originalFileLength - block));
                        }
                    }
                }
            }
        } catch (IOException ioException) {
            LOG.error(ioException.getMessage(), ioException);
            throw new FileAccessException(
                    "Unable to write random data to file - " + sourceFile,
                    ioException);
        }
    }
}
