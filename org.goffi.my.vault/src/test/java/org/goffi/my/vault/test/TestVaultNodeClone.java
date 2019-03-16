/*
 * org.goffi.my.vault
 *
 * File Name: TestVaultNodeClone.java
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
package org.goffi.my.vault.test;

import org.goffi.my.vault.model.Folder;
import org.goffi.my.vault.model.Note;
import org.goffi.my.vault.model.Password;
import org.goffi.my.vault.model.VaultNode;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class TestVaultNodeClone {

    @Test
    public void testCopyFolderWithSubNodes() throws CloneNotSupportedException,
            MalformedURLException {
        Folder folder = new Folder();
        folder.setName("root");

        Note note1 = new Note();
        note1.setName("note1");
        note1.setNote("the note");
        folder.getChildren().add(note1);

        Password password1 = new Password();
        password1.setName("empty-pass");
        folder.getChildren().add(password1);

        Password password2 = new Password();
        password2.setName("pass");
        password2.setDescription("descr");
        password2.setPassword("Secret!".toCharArray());
        password2.setUrl(new URL("https://google.com"));
        folder.getChildren().add(password2);

        Folder sub = new Folder();
        sub.setName("Sub");

        Note note2 = new Note();
        note2.setName("note2");
        note2.setNote("the note");
        sub.getChildren().add(note2);

        folder.getChildren().add(sub);

        compareTree(folder, folder.clone());
    }

    private class Pair {
        VaultNode first;
        VaultNode second;

        Pair(VaultNode first, VaultNode second) {
            this.first = first;
            this.second = second;
        }
    }

    private void compareTree(VaultNode original, VaultNode clone) {
        Queue<Pair> queue = new LinkedList<>();

        queue.add(new Pair(original, clone));
        while (!queue.isEmpty()) {
            Pair element = queue.poll();

            Assert.assertNotEquals(element.first, element.second);
            Assert.assertEquals(element.first.getChildren().size(),
                    element.second.getChildren().size());

            for (int i = 0; i < element.first.getChildren().size(); ++i) {
                Pair pair = new Pair(element.first.getChildren().get(i),
                        element.second.getChildren().get(i));
                queue.add(pair);
            }
        }
    }
}
