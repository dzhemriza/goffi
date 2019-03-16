/*
 * org.goffi.my.vault
 *
 * File Name: Note.java
 *
 * Copyright 2018 Dzhem Riza
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
package org.goffi.my.vault.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class Note extends VaultNode {

    @JsonProperty
    private String note;

    @Override
    public List<VaultNode> getChildren() {
        return Collections.emptyList();
    }

    public String getNote() {
        return note;
    }

    @JsonIgnore
    public void setNote(String note) {
        this.note = note;
        update();
    }
}
