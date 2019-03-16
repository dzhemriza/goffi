/*
 * org.goffi.toffi
 *
 * File Name: MissingFileMaskInDirectoryMetadataException.java
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

public class MissingFileMaskInDirectoryMetadataException extends ToffiException {

    private static final long serialVersionUID = -7648084856588041474L;

    private final String marshmallow;

    public MissingFileMaskInDirectoryMetadataException(String marshmallow) {
        this(marshmallow, null);
    }

    public MissingFileMaskInDirectoryMetadataException(String marshmallow, Throwable cause) {
        super("Missing marshmallow in directory metadata - " + marshmallow, cause);
        this.marshmallow = marshmallow;
    }

    public String getMarshmallow() {
        return marshmallow;
    }
}
