/*
 * org.goffi.toffi
 *
 * File Name: AbstractDirectoryMirrorMaker.java
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.toffi.domainmodel.files.exceptions.ChildPathProvidedForSyncException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Base class used for making a encrypted/decrypted mirror
 */
public abstract class AbstractDirectoryMirrorMaker {

    private static final Logger LOG =
            LogManager.getLogger(AbstractDirectoryMirrorMaker.class);
    protected final DataTransformer dataTransformer;
    protected final ObjectMapper objectMapper;

    public AbstractDirectoryMirrorMaker(DataTransformer dataTransformer,
            ObjectMapper objectMapper) {
        this.dataTransformer = dataTransformer;
        this.objectMapper = objectMapper;
    }

    protected void validatePath(Path source, Path target) {
        if (source.startsWith(target)) {
            throw new ChildPathProvidedForSyncException(source, target);
        }

        if (!source.toFile().isDirectory()) {
            throw new IllegalArgumentException(
                    "Non directory source path provided - " + source);
        }

        File targetFile = target.toFile();

        if (targetFile.exists() && !targetFile.isDirectory()) {
            throw new IllegalArgumentException(
                    "Non directory target path provided - " + target);
        }
    }

    protected void writeMetadataFile(Path dir,
            DirectoryMetadata directoryMetadata) throws IOException {
        // Seal the directory
        String json = objectMapper.writeValueAsString(directoryMetadata);
        LOG.debug("Write directory metadata: " + json);

        // Write metadata to target directory
        try (ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(
                org.goffi.core.domainmodel.Constants
                        .DEFAULT_CHARSET)
        )) {
            try (FileOutputStream out = new FileOutputStream(
                    Paths.get(dir.toString(), Constants.METADATA_FILE_NAME)
                            .toFile())) {
                dataTransformer.transform(in, out);
            }
        }
    }

    protected DirectoryMetadata readMetadataFile(Path dir) throws IOException {
        try (FileInputStream in = new FileInputStream(
                Paths.get(dir.toString(), Constants.METADATA_FILE_NAME)
                        .toFile())) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                // Decrypt data from metadata file
                dataTransformer.transform(in, out);

                String json = out.toString(
                        org.goffi.core.domainmodel.Constants.DEFAULT_CHARSET);
                LOG.debug("Json from metadata fle: " + json);

                return objectMapper.readValue(json, DirectoryMetadata.class);
            }
        }
    }
}
