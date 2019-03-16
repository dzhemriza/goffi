/*
 * org.goffi.toffi
 *
 * File Name: Aes.java
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
package org.goffi.toffi.shell.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.core.domainmodel.crypto.Key;
import org.goffi.core.domainmodel.crypto.TransformUtils;
import org.goffi.core.domainmodel.crypto.impl.BcPasswordBasedKey;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcEaxAesEncoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesDecoder;
import org.goffi.core.domainmodel.crypto.impl.v2.BcGcmAesEncoder;
import org.goffi.core.domainmodel.exceptions.CoreDomainException;
import org.goffi.toffi.domainmodel.files.*;
import org.goffi.toffi.shell.Command;
import org.goffi.toffi.shell.ConsoleKeyReader;
import org.goffi.toffi.shell.exceptions.InvalidAesModeException;
import org.goffi.toffi.shell.exceptions.PathDoesntExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class Aes extends Command {

    private final static Logger LOG = LogManager.getLogger(Aes.class);

    private static final String ENCODE_HELP =
            "Encrypt file/directory using AES 256 cipher";
    private static final String DECODE_HELP =
            "Decrypt archive or directory created by 'encode' command using " +
                    "AES 256 cipher";
    private static final String SOURCE_HELP = "Source path (file/directory)";
    private static final String TARGET_HELP = "Target path (file/directory)";
    private static final String MODE_HELP = "AES Mode: gcm or eax";
    private static final String ZIP_HELP =
            "Zip first before encode: true, false";
    private static final String ARCHIVE_EXT = ".archive";
    private static final String TFD_HELP =
            "Specify a directory where the temporary file will be created for" +
                    " zip purposes. If unspecified will use system default " +
                    "temporary location.";
    private static final String NCP_HELP =
            "Number of cleanup overrides of the file with random data";

    private static final Map<String, Function<Key, DataTransformer>>
            ENCODE_MODE;

    static {
        ENCODE_MODE = new HashMap<>();
        ENCODE_MODE.put("gcm", BcGcmAesEncoder::new);
        ENCODE_MODE.put("eax", BcEaxAesEncoder::new);
    }

    private static final Map<String, Function<Key, DataTransformer>>
            DECODE_MODE;

    static {
        DECODE_MODE = new HashMap<>();
        DECODE_MODE.put("gcm", BcGcmAesDecoder::new);
        DECODE_MODE.put("eax", BcEaxAesDecoder::new);
    }

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper objectMapper;

    @CliCommand(value = "encode", help = ENCODE_HELP)
    public String encode(
            @CliOption(key = {"s", "source"}, mandatory = true,
                       help = SOURCE_HELP)
                    File source,
            @CliOption(key = {"t", "target"}, help = TARGET_HELP)
                    File target,
            @CliOption(key = {"m", "mode"},
                       unspecifiedDefaultValue = "gcm",
                       help = MODE_HELP)
                    String mode,
            @CliOption(key = {"z", "zip"},
                       unspecifiedDefaultValue = "true",
                       specifiedDefaultValue = "true",
                       help = ZIP_HELP)
                    boolean zip,
            @CliOption(key = {"ckr", "consoleKeyReader"},
                       help = Constants.CKR_HELP,
                       unspecifiedDefaultValue = "multiFactorUserEncryptionKey")
                    ConsoleKeyReader consoleKeyReader,
            @CliOption(key = {"tfd", "tempFileDirectory"},
                       help = TFD_HELP)
                    File tempFileDirectory,
            @CliOption(key = {"ncp", "numberOfCleanupPasses"},
                       unspecifiedDefaultValue = "35",
                       help = NCP_HELP)
                    int numberOfCleanupPasses) {
        if (!source.exists()) {
            throw new PathDoesntExistsException(source.toPath());
        }

        Function<Key, DataTransformer> transformerFactory =
                ENCODE_MODE.computeIfAbsent(mode.toLowerCase(), key -> {
                    throw new InvalidAesModeException(mode);
                });

        Path targetPath = calculateTarget(source.toPath(), target);

        reportOperationInfo(source.toPath(), targetPath, mode, zip);

        char[] key = consoleKeyReader.read(true);

        cleanupSafely((cleanupLog) -> {
            Path src = source.toPath();

            if (zip) {
                File tempFile = generateTempZipFileName(targetPath,
                        tempFileDirectory);
                // Before we start the zip process we have to make sure
                // that we are going to cleanup later
                cleanupLog.add(() -> {
                    try {
                        cleanupTempFile(tempFile, numberOfCleanupPasses);
                    } catch (Exception e) {
                        LOG.error("Encounter an error while trying to " +
                                "delete file: {}", tempFile.getName(), e);
                    }
                });

                compress(src, tempFile.toPath());
                // Compression is now done we are going to encrypt the
                // compressed file so we have to change sourcePath to point to
                // the tempFile
                src = tempFile.toPath();
            }

            DataTransformer dataTransformer = transformerFactory.apply(
                    new BcPasswordBasedKey(key));

            encodeImpl(src, targetPath, dataTransformer);
        });

        return Constants.DONE;
    }

    @CliCommand(value = "decode", help = DECODE_HELP)
    public String decode(
            @CliOption(key = {"s", "source"}, mandatory = true,
                       help = SOURCE_HELP)
                    File source,
            @CliOption(key = {"t", "target"}, mandatory = true,
                       help = TARGET_HELP)
                    File target,
            @CliOption(key = {"m", "mode"},
                       unspecifiedDefaultValue = "gcm",
                       help = MODE_HELP)
                    String mode,
            @CliOption(key = {"z", "zip"},
                       unspecifiedDefaultValue = "true",
                       specifiedDefaultValue = "true",
                       help = ZIP_HELP)
                    boolean zip,
            @CliOption(key = {"ckr", "consoleKeyReader"},
                       help = Constants.CKR_HELP,
                       unspecifiedDefaultValue = "multiFactorUserEncryptionKey")
                    ConsoleKeyReader consoleKeyReader,
            @CliOption(key = {"tfd", "tempFileDirectory"},
                       help = TFD_HELP)
                    File tempFileDirectory,
            @CliOption(key = {"ncp", "numberOfCleanupPasses"},
                       unspecifiedDefaultValue = "35",
                       help = NCP_HELP)
                    int numberOfCleanupPasses) {
        if (!source.exists()) {
            throw new PathDoesntExistsException(source.toPath());
        }

        Function<Key, DataTransformer> transformerFactory =
                DECODE_MODE.computeIfAbsent(mode.toLowerCase(), key -> {
                    throw new InvalidAesModeException(mode);
                });

        reportOperationInfo(source.toPath(), target.toPath(), mode, zip);

        char[] key = consoleKeyReader.read(false);

        cleanupSafely((cleanupLog) -> {
            Path dest = target.toPath();

            if (zip) {
                // If zip is requested we need to decode the zip archive in the
                // temp directory first.

                File tempFile = generateTempZipFileName(source.toPath(),
                        tempFileDirectory);

                // Update the dest pointer used for decryption process
                dest = tempFile.toPath();

                // Before we start the unzip process we have to make sure
                // that we are going to cleanup later
                cleanupLog.add(() -> {
                    try {
                        cleanupTempFile(tempFile, numberOfCleanupPasses);
                    } catch (Exception e) {
                        LOG.error("Encounter an error while trying to " +
                                "delete file: {}", tempFile.getName(), e);
                    }
                });
            }

            DataTransformer dataTransformer = transformerFactory.apply(
                    new BcPasswordBasedKey(key));

            decodeImpl(source.toPath(), dest, dataTransformer);

            if (zip) {
                // Before we unzip we need to make sure that the dest directory
                // exists
                mkdir(target.toPath());
                // If zip was requested dest points now to the unencrypted zip
                // archive. So we have to unzip it.
                decompress(dest, target.toPath());
            }
        });

        return Constants.DONE;
    }

    private void mkdir(Path path) {
        try {
            FileUtils.forceMkdir(path.toFile());
        } catch (IOException e) {
            throw new CoreDomainException(e.getMessage(), e);
        }
    }

    private void cleanupSafely(Consumer<List<Runnable>> consumer) {
        List<Runnable> cleanupLog = new ArrayList<>();

        try {
            consumer.accept(cleanupLog);
        } finally {
            if (!cleanupLog.isEmpty()) {
                log.println("Cleanup started...");
                cleanupLog.forEach(Runnable::run);
            }
        }
    }

    private void reportOperationInfo(Path sourcePath, Path targetPath,
            String mode, boolean zip) {
        log.println("=============================");
        log.println("Source Path ............: " + sourcePath);
        log.println("Target Path ............: " + targetPath);
        log.println("Zip requested ..........: " + zip);
        log.println("Encryption .............: AES 256 " + mode.toUpperCase());
        log.println("=============================");
    }

    private String timeInUtc() {
        DateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_Z_z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private Path calculateTarget(Path sourcePath, File target) {
        if (target == null) {
            // Target is not provided so we need to create one
            // Steps:
            // Get the root path from sourcePath
            // Generate new name of form DateInUTC-sourcePathName.archive

            String newTargetName = timeInUtc() +
                    "-" +
                    sourcePath.getFileName().toString() +
                    ARCHIVE_EXT;

            return Paths.get(sourcePath.getParent().toString(),
                    newTargetName);
        }
        return target.toPath();
    }

    private void encodeImpl(Path sourcePath, Path targetPath,
            DataTransformer dataTransformer) {
        if (sourcePath.toFile().isFile()) {
            // Encode file
            encodeFile(sourcePath.toFile(), targetPath.toFile(),
                    dataTransformer);
        } else {
            // Encode directory
            encodeDirectory(sourcePath, targetPath, dataTransformer);
        }
    }

    private void decodeImpl(Path sourcePath, Path target,
            DataTransformer dataTransformer) {
        if (sourcePath.toFile().isFile()) {
            // Decode file
            decodeFile(sourcePath.toFile(), target.toFile(),
                    dataTransformer);
        } else {
            // Decode directory
            decodeDirectory(sourcePath, target, dataTransformer);
        }
    }

    private void compress(Path sourcePath, Path targetPath) {
        log.println("Zip...started at " + getCurrentTime());
        log.println("Zip source: " + sourcePath);
        log.println("Zip target: " + targetPath);
        Zip zip = new Zip();
        zip.zip(sourcePath, targetPath);
        log.println("Zip...finished at " + getCurrentTime());
    }

    private void decompress(Path sourcePath, Path targetPath) {
        log.println("Unzip...started at " + getCurrentTime());
        log.println("Unzip source: " + sourcePath);
        log.println("Unzip target: " + targetPath);
        Zip zip = new Zip();
        zip.unzip(sourcePath, targetPath);
        log.println("Unzip...finished at " + getCurrentTime());
    }

    /**
     * @param targetPath
     * @param tempFileDirectory
     * @return Generated random file name in the OS temp directory
     */
    private File generateTempZipFileName(Path targetPath,
            File tempFileDirectory) {
        try {
            return File.createTempFile(
                    targetPath.getFileName().toFile().toString(),
                    "." + UUID.randomUUID().toString() + ".zip",
                    tempFileDirectory);
        } catch (IOException e) {
            throw new CoreDomainException(e.getMessage(), e);
        }
    }

    private void cleanupTempFile(File tempFile, int numberOfCleanupPasses) {
        log.println("Cleanup...started at " + getCurrentTime());
        log.println("Cleanup temp file: " + tempFile.toPath().toString());

        // Cleanup the temporary file
        RandomBytesNoiseGenerator noiseGenerator =
                new RandomBytesNoiseGenerator();
        SafeDelete safeDelete = new SafeDelete(noiseGenerator);

        safeDelete.deleteFile(tempFile.toPath(),
                numberOfCleanupPasses,
                SafeDelete.DEFAULT_BLOCK_SIZE);
        log.println("Cleanup...finished at " + getCurrentTime());
    }

    private void encodeFile(File sourceFile, File targetFile,
            DataTransformer encoder) {
        log.println("File encoding...started at " + getCurrentTime());
        TransformUtils.transformFile(sourceFile, targetFile, encoder);
        log.println("File encoding...finished at " + getCurrentTime());
    }

    private void decodeFile(File sourceFile, File targetFile,
            DataTransformer decoder) {
        log.println("File decoding...started at " + getCurrentTime());
        TransformUtils.transformFile(sourceFile, targetFile, decoder);
        log.println("File decoding...finished at " + getCurrentTime());
    }

    private void encodeDirectory(Path sourceDirectory, Path targetDirectory,
            DataTransformer encoder) {
        DirectoryEncoder directoryEncoder =
                new DirectoryEncoder(encoder, objectMapper);

        log.println("Full directory encoding...started at " + getCurrentTime());

        directoryEncoder.encode(sourceDirectory, targetDirectory);

        log.println(
                "Full directory encoding...finished at " + getCurrentTime());
    }

    private void decodeDirectory(Path sourceDirectory, Path targetDirectory,
            DataTransformer decoder) {
        DirectoryDecoder directoryDecoder =
                new DirectoryDecoder(decoder, objectMapper);

        log.println("Full directory decoding...started at " + getCurrentTime());

        directoryDecoder.decode(sourceDirectory, targetDirectory);

        log.println(
                "Full directory decoding...finished at " + getCurrentTime());
    }
}
