/*
 * org.goffi.moffi
 *
 * File Name: TextAreaUtils.java
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.text.SimpleTextMatcher;
import org.goffi.core.domainmodel.text.TextMatcher;

import javax.swing.*;

public class JTextAreaUtils {

    private static final Logger LOG = LogManager.getLogger(JTextAreaUtils.class);

    /**
     * Searches for the next occurrence of the matcher based on the current caret position.
     *
     * @param textArea
     * @param textMatcher
     */
    public static void findAndSelectNext(JTextArea textArea, TextMatcher textMatcher) {
        JTextAreaMatchIterator
                iterator = new JTextAreaMatchIterator(textArea, textMatcher);

        if (iterator.hasNext()) {
            TextMatcher.Match match = iterator.next();
            textArea.select(match.getStart(), match.getStart() + match.getLen());
        }
    }

    /**
     * Replace all occurrences of a given match
     *
     * @param textArea
     * @param textMatcher
     */
    public static void replaceAll(JTextArea textArea, TextMatcher textMatcher, String replaceWith) {
        JTextAreaMatchIterator iterator = new JTextAreaMatchIterator(textArea,
                textMatcher, JTextAreaMatchIterator.Caret.Begin);

        while (iterator.hasNext()) {
            TextMatcher.Match match = iterator.next();
            LOG.trace("Match - start=" + match.getStart() + ", len=" + match.getLen());
            iterator.replace(replaceWith);
        }
    }

    /**
     * Replace a text in a text selection
     *
     * @param textArea
     * @param textMatcher
     * @param replaceWith
     */
    public static void replace(JTextArea textArea, SimpleTextMatcher textMatcher, String replaceWith) {
        String selectedText = textArea.getSelectedText();
        if (selectedText == null) {
            findAndSelectNext(textArea, textMatcher);
            return;
        }

        selectedText = textArea.getSelectedText();
        TextMatcher.Match math = textMatcher.indexOf(selectedText);

        if (!math.isNoMatch() && math.getStart() == 0 && math.getLen() == selectedText.length()) {
            // The selection is the actual text for replacement
            textArea.replaceSelection(replaceWith);
            // and move the selection to the next occurrence
            findAndSelectNext(textArea, textMatcher);
        } else {
            // If this is not what we are looking for move next
            findAndSelectNext(textArea, textMatcher);
        }
    }
}
