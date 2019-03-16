/*
 * org.goffi.moffi
 *
 * File Name: MemorableWord.java
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
package org.goffi.core.domainmodel;

import java.util.Arrays;

/**
 * Domain object MemorableWord used for long key generation
 */
public class MemorableWord {

    private final char[] word;

    private MemorableWord(char[] word) {
        this.word = word;
    }

    public static MemorableWord empty() {
        return of(new char[]{});
    }

    public static MemorableWord of(char[] word) {
        // No domain specific logic the word could be empty
        return new MemorableWord(word);
    }

    public char[] getWord() {
        return word;
    }

    public int length() {
        return word.length;
    }

    @Override
    public String toString() {
        return "MemorableWord{...}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemorableWord that = (MemorableWord) o;

        return Arrays.equals(word, that.word);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(word);
    }
}
