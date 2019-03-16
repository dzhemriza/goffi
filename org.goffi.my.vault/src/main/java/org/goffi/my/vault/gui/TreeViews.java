/*
 * org.goffi.my.vault
 *
 * File Name: TreeViewUtils.java
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
package org.goffi.my.vault.gui;

import javafx.scene.control.TreeItem;
import org.goffi.core.domainmodel.Trees;

import java.util.function.Function;

public class TreeViews {

    /**
     * @param root      - root node
     * @param visitFunc - where return type denotes to continue with DFS or not
     *                  {@code true} continue, {@code false} otherwise
     */
    public static <TNode> void traverseTreeView(TreeItem<TNode> root,
            Function<TreeItem<TNode>, Boolean> visitFunc) {
        Trees.dfs(root, visitFunc, TreeItem::getChildren);
    }

    @SuppressWarnings("unchecked")
    public static <TNode> TreeItem<TNode> findByValue(TreeItem<TNode> root,
            TNode node) {
        TreeItem<TNode>[] hold = new TreeItem[1];
        TreeViews.traverseTreeView(root, (treeItem) -> {
            if (treeItem.getValue().equals(node)) {
                hold[0] = treeItem;
                return false; // stop the search
            }
            return true; // continue the search
        });
        return hold[0];
    }
}
