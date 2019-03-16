/*
 * org.goffi.toffi
 *
 * File Name: DirectoryMetadata.java
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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Note: Do not change existing data because this will cause a broken
 * serialization.
 */
public class DirectoryMetadata {

    /**
     * Directory real name
     */
    @JsonProperty
    private String directoryRealName;

    /**
     * Files in current directory
     */
    @JsonProperty
    private final List<FileMetadata> files = new LinkedList<>();

    public String getDirectoryRealName() {
        return directoryRealName;
    }

    public void setDirectoryRealName(String directoryRealName) {
        this.directoryRealName = directoryRealName;
    }

    public List<FileMetadata> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectoryMetadata that = (DirectoryMetadata) o;
        return Objects
                .equals(directoryRealName, that.directoryRealName) &&
                Objects.equals(files, that.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directoryRealName, files);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("directoryRealName", directoryRealName)
                .append("files", files)
                .toString();
    }
}
