/*
 * org.goffi.my.vault
 *
 * File Name: VaultNodes.java
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
package org.goffi.core.domainmodel;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;

/**
 * Utility class
 */
public class Trees {

    /**
     * Simple DFS implementation using {@link Stack} that travers a tree. That
     * means we don't need to keep track of traversed nodes. Please note that
     * this implementation is not going to work against graph with cycles.
     *
     * @param root            - root node
     * @param visitFunc       - where return type denotes to continue with DFS
     *                        or not {@code true} continue, {@code false}
     *                        otherwise
     * @param getChildrenFunc - Function returns a list of child nodes
     */
    public static <TNode> void dfs(TNode root,
            Function<TNode, Boolean> visitFunc,
            Function<TNode, List<TNode>> getChildrenFunc) {
        Stack<TNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TNode item = stack.pop();

            if (!visitFunc.apply(item)) {
                // visit function give up so we stop with DFS
                break;
            }

            getChildrenFunc.apply(item).stream().forEach(stack::push);
        }
    }
}
