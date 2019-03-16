/*
 * org.goffi.toffi
 *
 * File Name: License.java
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
package org.goffi.toffi.shell.commands;

import org.goffi.toffi.shell.Command;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
public class License extends Command {

    @CliCommand(value = "license", help = "Shows the license information")
    public String license() {
        log.println("This is a free software, licensed under the terms of Apache License Version 2.0.");
        log.println();
        log.println("Toffi includes other open source software components/libraries.");
        log.println("You can find the full list and licenses under \"legal\" directory.");
        log.println();
        log.println("Author");
        log.println("Dzhem Riza");

        return null;
    }
}
