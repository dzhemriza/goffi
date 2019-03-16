/*
 * org.goffi.toffi
 *
 * File Name: Rm.java
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.toffi.domainmodel.files.Constants;
import org.goffi.toffi.domainmodel.files.RandomBytesNoiseGenerator;
import org.goffi.toffi.domainmodel.files.SafeDelete;
import org.goffi.toffi.shell.Command;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Rm extends Command {

    private static final Logger LOG = LogManager.getLogger(Rm.class);

    @CliCommand(value = "rm",
            help = "Safely remove files/directories.")
    public String rm(
            @CliOption(key = {"l", "list"},
                    mandatory = true,
                    help = "List of files/directories separated by ','.") File[] files,
            @CliOption(key = {"p", "numberOfPasses"},
                       unspecifiedDefaultValue = "35",
                    help = "Number of passes through the whole file.") int numberOfPasses,
            @CliOption(key = {"b", "blockSize"},
                       unspecifiedDefaultValue = "1024",
                    help = "Write block size.") int blockSize) {
        if (files != null) {

            RandomBytesNoiseGenerator noiseGenerator = new RandomBytesNoiseGenerator();
            SafeDelete safeDelete = new SafeDelete(noiseGenerator);

            for (File file : files) {
                log.println("Start deleting " + file);

                try {
                    safeDelete.delete(file, numberOfPasses, blockSize);

                    log.println("Successfully deleted " + file);
                } catch (Exception e) {
                    log.println("Error: Unable to delete " + file + ", " + e.getMessage());
                    LOG.error(e.getMessage(), e);
                }
            }
        } else {
            log.println("Error: No files/directories specified.");
        }

        return Constants.DONE;
    }
}
