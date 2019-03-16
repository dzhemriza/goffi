/*
 * org.goffi.moffi
 *
 * File Name: TextAreaMatchIterator.java
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
package org.goffi.fx.core.text;

import javafx.scene.control.TextArea;
import org.goffi.core.domainmodel.text.MatchIterator;
import org.goffi.core.domainmodel.text.TextMatcher;

public class TextAreaMatchIterator implements MatchIterator {
    private final TextArea textArea;
    private final TextMatcher textMatcher;

    private TextMatcher.Match match;
    private int currentCaretPosition;

    @FunctionalInterface
    public interface CaretPosition {
        int position(TextArea textArea);
    }

    public static class Caret {
        public static final CaretPosition Current =
                TextArea::getCaretPosition;
        public static final CaretPosition Begin = (textArea) -> 0;
    }

    public TextAreaMatchIterator(TextArea textArea, TextMatcher textMatcher) {
        this(textArea, textMatcher, Caret.Current);
    }

    public TextAreaMatchIterator(TextArea textArea, TextMatcher textMatcher,
            CaretPosition caretPosition) {
        this.textArea = textArea;
        this.textMatcher = textMatcher;
        this.match = TextMatcher.Match.no();
        this.currentCaretPosition = caretPosition.position(textArea);
    }

    @Override
    public boolean hasNext() {
        // This is a naive implementation of find next
        match = TextMatcher.Match.no(); // Reset the match

        final int lineEnd = 1;
        int walkingOffset = 0;
        for (CharSequence paragraph : textArea.getParagraphs()) {
            if (walkingOffset <= currentCaretPosition &&
                    currentCaretPosition <= walkingOffset
                            + paragraph.length()) {

                int caretStartPosition = currentCaretPosition - walkingOffset;
                CharSequence slicedLine = paragraph.subSequence(
                        caretStartPosition, paragraph.length());

                TextMatcher.Match indexOfMatchOnTheLine = textMatcher.indexOf(
                        slicedLine);

                if (!indexOfMatchOnTheLine.isNoMatch()) {
                    // Update the match to point to the actual position
                    match = new TextMatcher.Match(currentCaretPosition +
                            indexOfMatchOnTheLine.getStart(),
                            indexOfMatchOnTheLine.getLen());
                    // Prevent infinite loop
                    currentCaretPosition = match.getStart() + match.getLen();
                    // We have a match so we return
                    return true;
                } else {
                    // Move position to the end of the line
                    currentCaretPosition = walkingOffset + paragraph.length()
                            + lineEnd;
                }
            }

            // Moving to the next paragraph and next line
            walkingOffset += paragraph.length() + lineEnd;
        }

        return !match.isNoMatch();
    }

    @Override
    public TextMatcher.Match next() {
        return match;
    }

    /**
     * Iterator state aware text replace facility
     *
     * @param str
     */
    public void replace(String str) {
        if (!match.isNoMatch()) {
            textArea.replaceText(match.getStart(),
                    match.getLen() + match.getStart(), str);

            // Update iterator state
            currentCaretPosition = match.getStart() + str.length();
        }
    }
}