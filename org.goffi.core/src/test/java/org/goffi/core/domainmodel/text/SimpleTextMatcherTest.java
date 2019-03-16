/*
 * org.goffi.moffi
 *
 * File Name: TextMatcherImpl.java
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
package org.goffi.core.domainmodel.text;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SimpleTextMatcherTest {

    private static final String TEXT = "The quick brown fox jumps over the " +
            "lazy dog";

    @Test
    public void testIgnoreCase() {
        final boolean matchCase = false;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        TextMatcher.Match match = simpleTextMatcher.indexOf(TEXT);
        Assert.assertTrue(match.isMatch());
        Assert.assertEquals(3, match.getLen());
        Assert.assertEquals(0, match.getStart());
    }

    @Test
    public void testCaseSensitive() {
        final boolean matchCase = true;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        TextMatcher.Match match = simpleTextMatcher.indexOf(TEXT);
        Assert.assertTrue(match.isMatch());
        Assert.assertEquals(3, match.getLen());
        Assert.assertEquals(31, match.getStart());
    }

    @Test
    public void testIgnoreCaseNegativeStartPos() {
        final boolean matchCase = false;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        final int startPos = -1;
        TextMatcher.Match match = simpleTextMatcher.indexOf(TEXT, startPos);
        Assert.assertTrue(match.isMatch());
        Assert.assertEquals(3, match.getLen());
        Assert.assertEquals(0, match.getStart());
    }

    @Test
    public void testIgnoreCaseNegativeStartPos2() {
        final boolean matchCase = false;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        final int startPos = 2;
        TextMatcher.Match match = simpleTextMatcher.indexOf(TEXT, startPos);
        Assert.assertTrue(match.isMatch());
        Assert.assertEquals(3, match.getLen());
        Assert.assertEquals(31, match.getStart());
    }

    @Test
    public void testNegativeStartPos2() {
        final boolean matchCase = true;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        final int startPos = 2;
        TextMatcher.Match match = simpleTextMatcher.indexOf(TEXT, startPos);
        Assert.assertTrue(match.isMatch());
        Assert.assertEquals(3, match.getLen());
        Assert.assertEquals(31, match.getStart());
    }

    @Test
    public void testAllMatches() {
        final boolean matchCase = false;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        List<TextMatcher.Match> matches = simpleTextMatcher.allMatches(TEXT);

        Assert.assertEquals(2, matches.size());
        Assert.assertEquals(3, matches.get(0).getLen());
        Assert.assertEquals(0, matches.get(0).getStart());
        Assert.assertEquals(3, matches.get(1).getLen());
        Assert.assertEquals(31, matches.get(1).getStart());
    }

    @Test
    public void testAllMatchesCase() {
        final boolean matchCase = true;
        final String SEARCH = "the";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);

        List<TextMatcher.Match> matches = simpleTextMatcher.allMatches(TEXT);

        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(3, matches.get(0).getLen());
        Assert.assertEquals(31, matches.get(0).getStart());
    }

    @Test
    public void testAllMatchesNoMatch() {
        final boolean matchCase = true;
        final String SEARCH = "*******";
        SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(matchCase,
                SEARCH);
        List<TextMatcher.Match> matches = simpleTextMatcher.allMatches(TEXT);
        Assert.assertEquals(0, matches.size());
    }
}
