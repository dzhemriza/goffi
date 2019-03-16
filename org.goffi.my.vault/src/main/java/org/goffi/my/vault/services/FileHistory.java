/*
 * org.goffi.my.vault
 *
 * File Name: App.java
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
package org.goffi.my.vault.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileHistory {

    public static class Record {
        private File file;
        private long timestamp;

        public Record(File file, long timestamp) {
            this.file = file;
            this.timestamp = timestamp;
        }

        public File getFile() {
            return file;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getTimestampInDefaultLocale() {
            return new Date(this.timestamp).toString();
        }
    }

    /**
     * Map storing the file history, value represents UTC epoch in millis.
     */
    @JsonProperty
    private Map<File, Long> history = new HashMap<>();

    public void addFile(File file) {
        history.put(file, Instant.now().toEpochMilli());
    }

    public int size() {
        return history.size();
    }

    @JsonIgnore
    public List<Record> getAllRecords() {
        return history.entrySet().stream()
                .map(entry -> new Record(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
