/*
 * org.goffi.toffi
 *
 * File Name: FileMetadata.java
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

import java.util.Arrays;
import java.util.Objects;

/**
 * Note: Do not change existing data because this will cause a broken serialization.
 */
public class FileMetadata {

    /**
     * File Real Name
     */
    @JsonProperty
    private String fileRealName;

    /**
     * File mask
     */
    @JsonProperty
    private String fileMask;

    /**
     * File last modification
     */
    @JsonProperty
    private long lastModified;

    /**
     * SHA1 sum of the original file before encryption
     */
    @JsonProperty
    private byte[] sha1Sum;

    public static class Builder {
        private final FileMetadata fileMetadata = new FileMetadata();

        public Builder fileRealName(String fileRealName) {
            fileMetadata.setFileRealName(fileRealName);
            return this;
        }

        public Builder fileMask(String fileMask) {
            fileMetadata.setFileMask(fileMask);
            return this;
        }

        public Builder lastModified(long lastModified) {
            fileMetadata.setLastModified(lastModified);
            return this;
        }

        public Builder sha1Sum(byte[] sha1) {
            fileMetadata.setSha1Sum(sha1);
            return this;
        }

        public FileMetadata build() {
            return fileMetadata;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFileRealName() {
        return fileRealName;
    }

    public void setFileRealName(String fileRealName) {
        this.fileRealName = fileRealName;
    }

    public String getFileMask() {
        return fileMask;
    }

    public void setFileMask(String fileMask) {
        this.fileMask = fileMask;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public byte[] getSha1Sum() {
        return sha1Sum;
    }

    public void setSha1Sum(byte[] sha1Sum) {
        this.sha1Sum = sha1Sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata that = (FileMetadata) o;
        return lastModified == that.lastModified &&
                Objects.equals(fileRealName, that.fileRealName) &&
                Objects.equals(fileMask, that.fileMask) &&
                Arrays.equals(sha1Sum, that.sha1Sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileRealName, fileMask, lastModified, sha1Sum);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fileRealName", fileRealName)
                .append("fileMask", fileMask)
                .append("lastModified", lastModified)
                .append("sha1Sum", sha1Sum)
                .toString();
    }
}
