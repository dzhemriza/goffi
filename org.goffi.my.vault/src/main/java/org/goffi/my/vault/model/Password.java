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

import java.net.URL;
import java.util.Collections;
import java.util.List;

public class Password extends VaultNode {

    @JsonProperty
    private String description = "";

    @JsonProperty
    private URL url;

    @JsonProperty
    private char[] password;

    @Override
    public List<VaultNode> getChildren() {
        return Collections.emptyList();
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public void setDescription(String description) {
        this.description = description;
        update();
    }

    public URL getUrl() {
        return url;
    }

    @JsonIgnore
    public void setUrl(URL url) {
        this.url = url;
        update();
    }

    public char[] getPassword() {
        return password;
    }

    @JsonIgnore
    public void setPassword(char[] password) {
        this.password = password;
        update();
    }

}
