/*
 * org.goffi.my.vault
 *
 * File Name: Editor.java
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
package org.goffi.my.vault.gui.editors;

import org.goffi.my.vault.model.VaultNode;
import org.goffi.my.vault.services.UserPreferences;
import org.tbee.javafx.scene.layout.MigPane;

public interface Editor {
    /**
     * @param node
     * @return Is this editor supports editing of the given {@link VaultNode}
     */
    boolean isSupports(VaultNode node);

    /**
     * Start editing {@link VaultNode}
     *
     * @param node
     * @param container
     */
    void startEdit(VaultNode node, MigPane container);

    /**
     * @param userPreferences
     */
    void updatePreferences(UserPreferences userPreferences);

    /**
     * Finish editing {@link VaultNode}
     *
     * @return true if this operation actually updated the underlying {@link
     * VaultNode}
     */
    boolean stopEdit();

    /**
     * Save data from the editor into the object being edited
     *
     * @return true if this operation actually updated the underlying {@link
     * VaultNode}
     */
    boolean save();

    /**
     * Refresh data from from the object being edited
     */
    void refresh();
}
