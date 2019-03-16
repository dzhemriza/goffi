/*
 * org.goffi.moffi
 *
 * File Name: FindReplaceDialog.java
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
package org.goffi.moffi.gui;

import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.moffi.gui.events.GuiEventProcessor;
import org.goffi.moffi.gui.events.mode.ShadowModeEnabledEvent;
import org.goffi.moffi.gui.events.mode.TextModeEnabledEvent;
import org.goffi.moffi.gui.events.text.FindNextEvent;
import org.goffi.moffi.gui.events.text.ReplaceAllEvent;
import org.goffi.moffi.gui.events.text.ReplaceEvent;
import org.goffi.moffi.gui.text.TransferFocus;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ResourceBundle;

public class FindReplaceDialog extends JDialog {

    private static final Logger LOG = LogManager.getLogger(FindReplaceDialog.class);

    private final GuiEventProcessor eventProcessor;
    private boolean dialogVisible = true;

    public FindReplaceDialog(Frame owner, GuiEventProcessor eventProcessor) {
        super(owner);
        this.eventProcessor = eventProcessor;
        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings");
        this.setTitle(resourceBundle.getString("find.replace.dialog.title"));
        this.setMinimumSize(GuiConstants.WINDOW_MIN_DIMENSIONS);

        try {
            InputStream inputStream = this.getClass().getModule()
                    .getResourceAsStream("org/goffi/moffi/gui/images/search.png");
            if (inputStream != null) {
                BufferedImage appIcon = ImageIO.read(inputStream);
                this.setIconImage(appIcon);
            }
        } catch (Exception e) {
            LOG.error("Unable to load ico file: " + e.getMessage(), e);
            ExceptionDialog.of(e);
        }

        setLayout(new MigLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        JLabel searchForLabel = new JLabel(
                resourceBundle.getString("find.replace.dialog.search.for.label"));
        JTextArea searchForTextArea = new JTextArea();
        searchForTextArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        JScrollPane searchForTextAreaScrollPane = new JScrollPane(searchForTextArea);

        JPanel searchForPanel = new JPanel();
        searchForPanel.setLayout(new MigLayout());
        searchForPanel.add(searchForLabel, "dock north");
        searchForPanel.add(searchForTextAreaScrollPane, "dock center");

        JLabel replaceWithLabel = new JLabel(
                resourceBundle.getString("find.replace.dialog.replace.with.label"));
        JTextArea replaceWithTextArea = new JTextArea();
        replaceWithTextArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        JScrollPane replaceWithTextAreaScrollPane = new JScrollPane(replaceWithTextArea);

        JPanel replaceWithPanel = new JPanel();
        replaceWithPanel.setLayout(new MigLayout());
        replaceWithPanel.add(replaceWithLabel, "dock north");
        replaceWithPanel.add(replaceWithTextAreaScrollPane, "dock center");

        centerPanel.add(searchForPanel);
        centerPanel.add(replaceWithPanel);

        JButton findButton = new JButton(
                resourceBundle.getString("find.replace.dialog.find.next.button"));

        JButton replaceButton = new JButton(
                resourceBundle.getString("find.replace.dialog.replace.button"));

        JButton replaceAllButton = new JButton(
                resourceBundle.getString("find.replace.dialog.replace.all.button"));

        JButton closeButton = new JButton(
                resourceBundle.getString("find.replace.dialog.close.button"));
        closeButton.addActionListener((a) -> this.setVisible(false));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new MigLayout());
        rightPanel.add(findButton, "wrap, growx");
        rightPanel.add(replaceButton, "wrap, growx");
        rightPanel.add(replaceAllButton, "wrap, growx");
        rightPanel.add(closeButton, "wrap, growx");

        JCheckBox matchCaseCheckBox = new JCheckBox(
                resourceBundle.getString("find.replace.dialog.mach.case.check.box"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new MigLayout());
        bottomPanel.add(matchCaseCheckBox);

        add(centerPanel, "dock center");
        add(rightPanel, "dock east");
        add(bottomPanel, "dock south");

        Runnable onFindNext = () -> {
            LOG.debug("Firing FindNextEvent, searchFor: "
                    + searchForTextArea.getText()
                    + ", matchCase: "
                    + matchCaseCheckBox.isSelected());

            this.eventProcessor.fire(new FindNextEvent(this,
                    searchForTextArea.getText(),
                    matchCaseCheckBox.isSelected()));
        };

        // button listeners
        findButton.addActionListener((l) -> onFindNext.run());

        replaceAllButton.addActionListener((l) -> {
            LOG.debug("Firing ReplaceAllEvent, searchFor: "
                    + searchForTextArea.getText()
                    + ", matchCase: "
                    + matchCaseCheckBox.isSelected());

            this.eventProcessor.fire(new ReplaceAllEvent(this,
                    searchForTextArea.getText(),
                    matchCaseCheckBox.isSelected(),
                    replaceWithTextArea.getText()));
        });

        replaceButton.addActionListener((l) -> {
            LOG.debug("Firing ReplaceEvent, searchFor: "
                    + searchForTextArea.getText()
                    + ", matchCase: "
                    + matchCaseCheckBox.isSelected());

            this.eventProcessor.fire(new ReplaceEvent(this,
                    searchForTextArea.getText(),
                    matchCaseCheckBox.isSelected(),
                    replaceWithTextArea.getText()));
        });

        // Register events
        this.eventProcessor.add((TextModeEnabledEvent e) -> {
            // Back in text mode
            this.setVisible(dialogVisible);
        });
        this.eventProcessor.add((ShadowModeEnabledEvent e) -> {
            // Shadow mode
            dialogVisible = this.isVisible(); // save visibility to restore it later
            this.setVisible(false);
        });

        // Focus path for JTextArea
        TransferFocus.patch(searchForTextArea);
        TransferFocus.patch(replaceWithTextArea);

        // Keyboard keys

        // Esc key
        this.getRootPane().registerKeyboardAction(
                (l) -> this.setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Enter in JTextArea
        searchForTextArea.registerKeyboardAction(
                (l) -> onFindNext.run(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_FOCUSED);

        replaceWithTextArea.registerKeyboardAction(
                (l) -> onFindNext.run(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_FOCUSED);
    }
}
