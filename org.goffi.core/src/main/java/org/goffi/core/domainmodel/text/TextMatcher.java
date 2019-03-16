/*
 * org.goffi.moffi
 *
 * File Name: TextMatcher.java
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
package org.goffi.core.domainmodel.text;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Common interface for text matching
 */
public interface TextMatcher {

    class Match {
        private final int start;
        private final int len;

        public Match(int start, int len) {
            this.start = start;
            this.len = len;
        }

        public static Match no() {
            return new Match(-1, 0);
        }

        public int getStart() {
            return start;
        }

        public int getLen() {
            return len;
        }

        public boolean isNoMatch() {
            return start == -1;
        }

        public boolean isMatch() {
            return !isNoMatch();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("start", start)
                    .append("len", len)
                    .toString();
        }
    }

    /**
     * First index of given string
     *
     * @return index of position -1 if it's not found
     */
    Match indexOf(CharSequence str);

    /**
     * First index of given string after provided start position.
     *
     * @return index of position -1 if it's not found
     */
    Match indexOf(CharSequence str, int startPos);

    /**
     * @param str
     * @return All matches
     */
    List<Match> allMatches(CharSequence str);
}
