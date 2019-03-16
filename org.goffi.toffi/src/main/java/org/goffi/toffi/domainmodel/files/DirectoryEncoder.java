/*
 * org.goffi.toffi
 *
 * File Name: DirectoryEncoder.java
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
package org.goffi.toffi.domainmodel.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.TransformUtils;
import org.goffi.toffi.domainmodel.files.exceptions.FileAccessException;
import org.goffi.toffi.domainmodel.files.exceptions.UnableToCreateDirectoryException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.UUID;

public class DirectoryEncoder extends AbstractDirectoryMirrorMaker {

    private static final Logger LOG = LogManager.getLogger(DirectoryEncoder.class);

    public DirectoryEncoder(DataTransformer dataTransformer,
            ObjectMapper objectMapper) {
        super(dataTransformer, objectMapper);
    }

    private class EncodingVisitor extends SimpleFileVisitor<Path> {

        private final Path source;
        private final Path target;
        private final LinkedList<StackData> pathStack = new LinkedList<>();

        public EncodingVisitor(Path source, Path target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {

            // Update mask
            String targetFileName = UUID.randomUUID().toString();

            File sourceFile = file.toFile();

            try (FileInputStream fileInputStream = new FileInputStream(
                    sourceFile)) {
                pathStack.peek().getDirectoryMetadata().getFiles().add(
                        FileMetadata.builder()
                                .fileMask(targetFileName)
                                .lastModified(sourceFile.lastModified())
                                .fileRealName(file.getFileName().toString())
                                .sha1Sum(DigestUtils.sha1(fileInputStream))
                                .build());
            }

            LOG.debug("Visit File: " + file + ", marshmallow: "
                    + targetFileName);

            TransformUtils.transformFile(file,
                    Paths.get(pathStack.peek().getTarget().toString(),
                            targetFileName),
                    dataTransformer);

            return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) throws IOException {
            StackData preVisitDirectory;

            if (source.equals(dir)) {
                // If we just start to iterate through the directory this
                // means we have empty stack and we simply push source and
                // target directories

                preVisitDirectory = new StackData(source, target);
                preVisitDirectory
                        .getDirectoryMetadata()
                        .setDirectoryRealName(target.getFileName().toString());
            } else {
                // We have to calculate the new target which is based on all
                // dir.getName() and all the paths in the stack

                String originalName = dir.getFileName().toString();
                String marshmallow = UUID.randomUUID().toString();
                Path targetDir = Paths.get(
                        pathStack.peek().getTarget().toString(), marshmallow);

                preVisitDirectory = new StackData(dir, targetDir);
                preVisitDirectory.getDirectoryMetadata()
                        .setDirectoryRealName(originalName);
            }
            LOG.debug("PreVisit - " + preVisitDirectory);
            pathStack.push(preVisitDirectory);

            // Create target directory if it doesn't exists
            if (!preVisitDirectory.getTarget().toFile().mkdirs()) {
                throw new UnableToCreateDirectoryException(
                        preVisitDirectory.getTarget());
            }

            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {

            StackData top = pathStack.pop();
            LOG.debug("PostVisit - " + top);

            // Seal the directory
            writeMetadataFile(top.getTarget(), top.getDirectoryMetadata());

            return super.postVisitDirectory(dir, exc);
        }
    }

    /**
     * Create a encrypted clone of the directory without following symbolic
     * links
     *
     * @param source
     * @param target
     */
    public void encode(Path source, Path target) {
        validatePath(source, target);

        try {
            Files.walkFileTree(source, new EncodingVisitor(source, target));
        } catch (IOException ex) {
            throw new FileAccessException(ex.getMessage(), ex);
        }
    }
}
