/*
 * org.goffi.moffi
 *
 * File Name: TextMatcherImpl.java
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

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generic implementation of {@link TextMatcher}
 */
public class SimpleTextMatcher implements TextMatcher {

    private final boolean matchCase;
    private final CharSequence search;

    public SimpleTextMatcher(boolean matchCase, CharSequence search) {
        this.matchCase = matchCase;
        this.search = search;
    }

    @Override
    public Match indexOf(CharSequence str) {
        final int startPos = 0;
        return indexOf(str, startPos);
    }

    @Override
    public Match indexOf(CharSequence str, int startPos) {
        if (search.length() == 0 || str.length() == 0) {
            // No match on empty strings
            return Match.no();
        }

        if (matchCase) {
            int pos = StringUtils.indexOf(str, this.search, startPos);
            if (pos == -1) {
                return Match.no();
            }

            return new Match(pos, this.search.length());
        }

        int pos = StringUtils.indexOfIgnoreCase(str, this.search, startPos);
        if (pos == -1) {
            return Match.no();
        }

        return new Match(pos, this.search.length());
    }

    @Override
    public List<Match> allMatches(CharSequence str) {
        return Stream.iterate(indexOf(str),
                Match::isMatch,
                match -> indexOf(str, match.getStart() + 1))
                .collect(Collectors.toList());
    }
}
