/*
 * org.goffi.my.vault
 *
 * File Name: VaultNodeTableView.java
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
package org.goffi.my.vault.gui;

import org.goffi.my.vault.model.VaultNode;

import java.util.Date;

public class VaultNodeTableView {
    private final VaultNode vaultNode;

    public VaultNodeTableView(VaultNode vaultNode) {
        this.vaultNode = vaultNode;
    }

    public String getId() {
        return vaultNode.getId().toString();
    }

    public String getName() {
        return vaultNode.getName();
    }

    public String getUserName() {
        return vaultNode.getUserName();
    }

    public Date getTimestamp() {
        return new Date(vaultNode.getTimestamp());
    }

    public Date getLastUpdatedTimestamp() {
        return new Date(vaultNode.getLastUpdatedTimestamp());
    }

    public String getLastUpdatedUserName() {
        return vaultNode.getLastUpdatedUserName();
    }

    public VaultNode getVaultNode() {
        return vaultNode;
    }
}
