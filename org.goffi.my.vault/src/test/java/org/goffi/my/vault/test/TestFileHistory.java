/*
 * org.goffi.text.encoder
 *
 * File Name: DummyTest.java
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
package org.goffi.my.vault.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goffi.my.vault.services.FileHistory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestFileHistory {

    @Test
    public void addTheSameFileTwice() {
        FileHistory fileHistory = new FileHistory();

        fileHistory.addFile(new File("/tmp"));
        fileHistory.addFile(new File("/tmp"));

        Assert.assertEquals(1, fileHistory.size());
    }

    @Test
    public void serializeDeserialize() throws IOException {
        FileHistory fileHistory = new FileHistory();
        fileHistory.addFile(new File("a"));
        fileHistory.addFile(new File("b"));
        fileHistory.addFile(new File("c"));

        ObjectMapper objectMapper = new ObjectMapper();
        String fileHistoryAsString =
                objectMapper.writeValueAsString(fileHistory);

        FileHistory newHistory = objectMapper.readValue(fileHistoryAsString,
                FileHistory.class);

        Assert.assertEquals(3, newHistory.size());

        List<FileHistory.Record> recordList = newHistory
                .getAllRecords()
                .stream()
                .sorted(Comparator.comparing(FileHistory.Record::getFile))
                .collect(Collectors.toList());

        Assert.assertEquals(new File("a"),
                recordList.get(0).getFile());
        Assert.assertEquals(new File("b"),
                recordList.get(1).getFile());
        Assert.assertEquals(new File("c"),
                recordList.get(2).getFile());
    }
}
