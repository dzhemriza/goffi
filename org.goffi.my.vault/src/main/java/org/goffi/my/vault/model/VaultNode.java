/*
 * org.goffi.my.vault
 *
 * File Name: VaultNode.java
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
package org.goffi.my.vault.model;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * MyVault tree node
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
              include = JsonTypeInfo.As.PROPERTY,
              property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = Folder.class, name = "folder"),
               @JsonSubTypes.Type(value = Note.class, name = "note"),
               @JsonSubTypes.Type(value = Password.class, name = "password")
              })
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class VaultNode implements Cloneable {

    /**
     * The children nodes
     */
    @JsonProperty
    private List<VaultNode> children = new ArrayList<>();

    /**
     * The node name
     */
    @JsonProperty
    private String name;

    /**
     * The node unique ID
     */
    @JsonProperty
    private UUID id;

    /**
     * Machine user name created this node
     */
    @JsonProperty
    private String userName;

    /**
     * Timestamp when this node is created
     */
    @JsonProperty
    private long timestamp;

    /**
     * Last user updated this node
     */
    @JsonProperty
    private String lastUpdatedUserName;

    /**
     * Last updated timestamp epoch in millis in UTC.
     */
    @JsonProperty
    private long lastUpdatedTimestamp;

    public VaultNode() {
        initCommonFields();
    }

    private void initCommonFields() {
        // Always create unique id when creating new VaultNode
        this.id = UUID.randomUUID();
        this.userName = System.getProperty("user.name");
        this.lastUpdatedUserName = userName;
        this.timestamp = Instant.now().toEpochMilli();
        this.lastUpdatedTimestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
        update();
    }

    public List<VaultNode> getChildren() {
        return children;
    }

    @JsonIgnore
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    @JsonIgnore
    public boolean isContainer() {
        return false;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public String getLastUpdatedUserName() {
        return lastUpdatedUserName;
    }

    protected void update() {
        this.lastUpdatedTimestamp = Instant.now().toEpochMilli();
        this.lastUpdatedUserName = System.getProperty("user.name");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VaultNode node = (VaultNode) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public VaultNode clone() throws CloneNotSupportedException {
        VaultNode dolly = (VaultNode) super.clone();
        dolly.initCommonFields(); // force update the common fields

        ArrayList<VaultNode> cloneChildren = new ArrayList<>();
        // We need deep copy so we have to copy all nodes under
        for (int i = 0; i < dolly.children.size(); ++i) {
            VaultNode real = dolly.children.get(i);
            cloneChildren.add(real.clone());
        }

        dolly.children = cloneChildren;
        return dolly;
    }
}
