/*
 * org.goffi.toffi
 *
 * File Name: UnableToCreateDirectoryException.java
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
package org.goffi.toffi.domainmodel.files.exceptions;

import org.goffi.toffi.domainmodel.exceptions.ToffiException;

import java.nio.file.Path;

public class UnableToCreateDirectoryException extends ToffiException {

    private static final long serialVersionUID = -7136010214393936878L;

    private final String directory;

    public UnableToCreateDirectoryException(String directory) {
        this(directory, null);
    }

    public UnableToCreateDirectoryException(Path directory) {
        this(directory.toString());
    }

    public UnableToCreateDirectoryException(String directory, Throwable cause) {
        super("Unable to create directory - " + directory, cause);
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}
