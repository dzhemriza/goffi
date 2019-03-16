/*
 * org.goffi.toffi
 *
 * File Name: PathDoesntExistsException.java
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
package org.goffi.toffi.shell.exceptions;

import java.nio.file.Path;

public class PathDoesntExistsException extends CommandException {

    public PathDoesntExistsException(Path path) {
        this(path, null);
    }

    public PathDoesntExistsException(Path path, Throwable cause) {
        super("Error: Path: \"" + path.toString() + "\" doesn't exists", cause);
    }
}
