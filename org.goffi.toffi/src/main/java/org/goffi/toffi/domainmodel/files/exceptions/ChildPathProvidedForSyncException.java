/*
 * org.goffi.toffi
 *
 * File Name: ChildPathProvidedForSyncException.java
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

public class ChildPathProvidedForSyncException extends ToffiException {

    private static final long serialVersionUID = -2908682031846020870L;

    private final Path source;
    private final Path target;

    public ChildPathProvidedForSyncException(Path source, Path target) {
        this(source, target, null);
    }

    public ChildPathProvidedForSyncException(Path source, Path target, Throwable cause) {
        super("'" + target + "' is a child of '" + source + "'");
        this.source = source;
        this.target = target;
    }

    public Path getSource() {
        return source;
    }

    public Path getTarget() {
        return target;
    }
}
