/*
 * org.goffi.toffi
 *
 * File Name: DirectoryDecoder.java
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
import org.goffi.core.domainmodel.crypto.TransformUtils;
import org.goffi.toffi.domainmodel.files.exceptions.FileAccessException;
import org.goffi.toffi.domainmodel.files.exceptions.MissingFileMaskInDirectoryMetadataException;
import org.goffi.toffi.domainmodel.files.exceptions.UnableToCreateDirectoryException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

public class DirectoryDecoder extends AbstractDirectoryMirrorMaker {

    private static final Logger LOG = LogManager.getLogger(DirectoryDecoder.class);

    public DirectoryDecoder(DataTransformer dataTransformer,
            ObjectMapper objectMapper) {
        super(dataTransformer, objectMapper);
    }

    private class DecodingVisitor extends SimpleFileVisitor<Path> {

        private final Path source;
        private final Path target;
        private LinkedList<StackData> pathStack = new LinkedList<>();

        public DecodingVisitor(Path source, Path target) {
            this.source = source;
            this.target = target;
            this.pathStack = pathStack;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            // Ignore the metadata file and decode the file
            if (!Constants.METADATA_FILE_NAME.equals(
                    file.getFileName().toString())) {
                String realFileName = getFileRealName(
                        pathStack.peek().getDirectoryMetadata(),
                        file.getFileName().toString());

                Path targetFile = Paths.get(
                        pathStack.peek().getTarget().toString(),
                        realFileName);

                TransformUtils.transformFile(file, targetFile, dataTransformer);
            }

            return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) throws IOException {

            LOG.debug("PreVisit Encrypted Directory: " + dir);

            StackData stackData = new StackData();

            stackData.setSource(source);

            // Before we start iterating through files we need to read the
            // metadata
            stackData.setDirectoryMetadata(readMetadataFile(dir));

            // Meta data is initialized

            if (source.equals(dir)) {
                // We just start to iterate through the encrypted mirror
                stackData.setTarget(target);
            } else {
                Path target = Paths.get(
                        pathStack.peek().getTarget().toString(),
                        stackData.getDirectoryMetadata()
                                .getDirectoryRealName());

                stackData.setTarget(target);
            }

            // Create target directory if its not created
            if (!stackData.getTarget().toFile().mkdirs()) {
                throw new UnableToCreateDirectoryException(
                        stackData.getTarget());
            }

            pathStack.push(stackData);

            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {

            StackData top = pathStack.pop();
            LOG.debug("PostVisit - " + top);

            return super.postVisitDirectory(dir, exc);
        }
    }

    /**
     * Reads the encoded directory and creates a decoded mirror
     *
     * @param source
     * @param target
     */
    public void decode(Path source, Path target) {
        validatePath(source, target);

        try {
            Files.walkFileTree(source, new DecodingVisitor(source, target));
        } catch (IOException ex) {
            throw new FileAccessException(ex.getMessage(), ex);
        }
    }

    private static String getFileRealName(DirectoryMetadata directoryMetadata,
            String fileMask) {
        for (FileMetadata fileMetadata : directoryMetadata.getFiles()) {
            if (fileMask.equals(fileMetadata.getFileMask())) {
                return fileMetadata.getFileRealName();
            }
        }
        throw new MissingFileMaskInDirectoryMetadataException(fileMask);
    }
}
