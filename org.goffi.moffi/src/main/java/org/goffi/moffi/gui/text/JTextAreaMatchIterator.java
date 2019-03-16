/*
 * org.goffi.moffi
 *
 * File Name: TextAreaMatchIterator.java
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
package org.goffi.moffi.gui.text;

import org.goffi.core.domainmodel.text.MatchIterator;
import org.goffi.core.domainmodel.text.TextMatcher;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class JTextAreaMatchIterator implements MatchIterator {

    private final JTextArea textArea;
    private final TextMatcher textMatcher;

    private TextMatcher.Match match;
    private int currentCaretPosition;

    @FunctionalInterface
    public interface CaretPosition {
        int position(JTextArea textArea);
    }

    public static class Caret {
        public static final CaretPosition Current =
                JTextComponent::getCaretPosition;
        public static final CaretPosition Begin = (textArea) -> 0;
    }

    public JTextAreaMatchIterator(JTextArea textArea, TextMatcher textMatcher) {
        this(textArea, textMatcher, Caret.Current);
    }

    public JTextAreaMatchIterator(JTextArea textArea, TextMatcher textMatcher, CaretPosition caretPosition) {
        this.textArea = textArea;
        this.textMatcher = textMatcher;
        this.match = TextMatcher.Match.no();
        this.currentCaretPosition = caretPosition.position(textArea);
    }

    @Override
    public boolean hasNext() {
        // This is a naive implementation of find next
        try {
            match = TextMatcher.Match.no(); // Reset the match

            for (int lineNumber = textArea.getLineOfOffset(currentCaretPosition);
                 lineNumber < textArea.getLineCount() && match.isNoMatch();
                 lineNumber++) {
                // Iterate the text area line by line until text matches

                int lineEnd = textArea.getLineEndOffset(lineNumber);

                if (lineEnd < currentCaretPosition) {
                    throw new BadLocationException("lineEnd = "
                            + lineEnd + ", currentCaretPosition = "
                            + currentCaretPosition, lineEnd);
                }

                String textLineAfterTheCaret = textArea.getText(currentCaretPosition,
                        lineEnd - currentCaretPosition);

                TextMatcher.Match indexOfMatchOnTheLine = textMatcher.indexOf(textLineAfterTheCaret);

                if (!indexOfMatchOnTheLine.isNoMatch()) {
                    // Update the match to point to the actual position
                    match = new TextMatcher.Match(
                            currentCaretPosition + indexOfMatchOnTheLine.getStart(),
                            indexOfMatchOnTheLine.getLen());
                    // Prevent infinite loop
                    currentCaretPosition = match.getStart() + match.getLen();
                } else {
                    // Move position to the end of the line
                    currentCaretPosition = lineEnd;
                }
            }

            return !match.isNoMatch();
        } catch (BadLocationException e) {
            // todo: replace this with something more meaningful
            throw new RuntimeException(e.getMessage(), e);
        }
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
            textArea.replaceRange(str, match.getStart(), match.getLen() + match.getStart());
            // Update iterator state
            currentCaretPosition = match.getStart() + str.length();
        }
    }
}
