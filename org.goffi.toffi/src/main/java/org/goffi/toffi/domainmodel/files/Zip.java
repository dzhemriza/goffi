/*
 * org.goffi.toffi
 *
 * File Name: Zip.java
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
package org.goffi.toffi.domainmodel.files;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.toffi.domainmodel.files.exceptions.GeneralZipException;
import org.goffi.toffi.domainmodel.misc.Copy;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility for file/directory compression
 */
public class Zip {

    private static final Logger LOG = LogManager.getLogger(Zip.class);

    @FunctionalInterface
    private interface Consumer<T> {
        void accept(T t) throws IOException;
    }

    public void zip(Path source, Path zipFile) {
        try {
            if (source.toFile().isDirectory()) {
                LOG.debug("Zipping directory " + source + " target " + zipFile);

                List<Path> files = files(source);

                zipIt(source, zipFile, files);

            } else {
                LOG.debug("Zipping file " + source + " target " + zipFile);

                zipIt(source, zipFile);
            }
        } catch (IOException ioException) {
            LOG.error(ioException.getMessage(), ioException);
            throw new GeneralZipException(source, ioException);
        }
    }

    public void unzip(Path zipFile, Path target) {
        try {
            if (!target.toFile().exists()) {
                throw new IllegalArgumentException("Not exists - " + target);
            }

            if (!target.toFile().isDirectory()) {
                throw new IllegalArgumentException("Not a directory - " + target);
            }

            try (FileInputStream fileInputStream = new FileInputStream(zipFile.toFile())) {
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
                    try (ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream)) {

                        for (ZipEntry zipEntry = zipInputStream.getNextEntry();
                             zipEntry != null;
                             zipEntry = zipInputStream.getNextEntry()) {

                            unzipFileOrDir(target, zipEntry, zipInputStream);
                        }

                        zipInputStream.closeEntry();
                    }
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new GeneralZipException(zipFile, e);
        }
    }

    private void unzipFileOrDir(Path target, ZipEntry zipEntry,
            ZipInputStream zipInputStream) throws IOException {
        String fileName = zipEntry.getName();
        Path file = Paths.get(target.toString(), fileName);

        if (zipEntry.isDirectory()) {
            if (!file.toFile().mkdirs()) {
                LOG.warn("Unable to create directory {}", file);
            }
        } else {
            File newFile = file.toFile();

            LOG.debug("File unzip: " +
                    newFile.getAbsoluteFile());

            // Create non existing paths
            if (!new File(newFile.getParent()).mkdirs()) {
                LOG.warn("Unable to create directory {}", newFile.getParent());
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(
                    newFile)) {
                try (BufferedOutputStream bufferedOutputStream = new
                        BufferedOutputStream(
                        fileOutputStream)) {
                    IOUtils.copy(zipInputStream, bufferedOutputStream);
                }
            }
        }
    }

    private void zipIt(Path source, Path target, List<Path> files) throws IOException {
        zipIt(target, (zipOutputStream) -> {
            for (Path file : files) {
                String unixFileName =
                        FilenameUtils.separatorsToUnix(
                                source.relativize(file).toFile()
                                        .toString());

                if (file.toFile().isDirectory()) {
                    // This is a empty directory we need to keep the structure
                    // the same.

                    // Reformat the unixFileName to ends to / this indicates
                    // that this is a  directory entry
                    unixFileName = unixFileName.endsWith("/") ? unixFileName :
                            unixFileName + "/";

                    ZipEntry zipEntry = new ZipEntry(unixFileName);

                    zipOutputStream.putNextEntry(zipEntry);

                    zipOutputStream.closeEntry();
                } else {
                    ZipEntry zipEntry = new ZipEntry(unixFileName);

                    zipOutputStream.putNextEntry(zipEntry);

                    Copy.copy(file, zipOutputStream);

                    zipOutputStream.closeEntry();
                }
            }
        });
    }

    private void zipIt(Path file, Path target) throws IOException {
        zipIt(target, (zipOutputStream) -> {
            String unixFileName =
                    FilenameUtils.separatorsToUnix(
                            file.getFileName().toString());

            ZipEntry zipEntry = new ZipEntry(unixFileName);

            zipOutputStream.putNextEntry(zipEntry);

            Copy.copy(file, zipOutputStream);

            zipOutputStream.closeEntry();
        });
    }

    private void zipIt(Path target, Consumer<ZipOutputStream> zipOutputStreamConsumer) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(target.toFile())) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
                zipOutputStreamConsumer.accept(zipOutputStream);
            }
        }
    }

    private List<Path> files(Path directory) throws IOException {
        List<Path> files = new ArrayList<>();

        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                if (!files.isEmpty()) {
                    Path lastFile = files.get(files.size() - 1);

                    if (!dir.equals(lastFile.getParent())) {
                        // If we visited the directory but the directory is
                        // empty we have to preserve the structure
                        files.add(dir);
                    }
                } else {
                    // If the files list is empty this means that the directory
                    // is empty or this is the first directory in the parent
                    // and it's empty.
                    files.add(dir);
                }

                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                files.add(file);
                return super.visitFile(file, attrs);
            }
        });

        return files;
    }
}
