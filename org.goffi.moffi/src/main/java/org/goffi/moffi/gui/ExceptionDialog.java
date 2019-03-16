/*
 * org.goffi.moffi
 *
 * File Name: ExceptionDialog.java
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
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Represents a error dialog with Exception stacktrace
 */
public class ExceptionDialog extends JDialog {

    public ExceptionDialog(String title, Throwable throwable) {
        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());

        JPanel panelIconText = new JPanel();
        panelIconText.setLayout(new MigLayout());

        JLabel labelErrorIcon = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        panelIconText.add(labelErrorIcon);

        JLabel labelErrorText = new JLabel(throwable.getMessage());
        panelIconText.add(labelErrorText);

        mainPanel.add(panelIconText, "wrap");

        JTextArea textAreaError = new JTextArea();
        textAreaError.setText(ExceptionUtils.getStackTrace(throwable));
        textAreaError.setEditable(false);
        textAreaError.setColumns(80);
        textAreaError.setRows(22);
        textAreaError.setCaretPosition(0); // Move the text cursor to the begining of the text
        textAreaError.setFont(GuiConstants.DEFAULT_FONT);

        JScrollPane textAreaScrollPane = new JScrollPane(textAreaError,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(textAreaScrollPane, "wrap");

        JButton buttonOk = new JButton(resourceBundle.getString("button.ok"));
        buttonOk.setDefaultCapable(true);
        buttonOk.addActionListener((l) -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        mainPanel.add(buttonOk);

        // Enter, Esc
        mainPanel.registerKeyboardAction((l) -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainPanel.registerKeyboardAction((l) -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setTitle(Objects.requireNonNullElseGet(title, () -> resourceBundle
                .getString("exception.dialog.title")));

        this.add(mainPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false); // hides the icon
        this.setMinimumSize(GuiConstants.MINIMUM_SIZE);
        this.setMaximumSize(GuiConstants.MAXIMUM_SIZE);
    }

    public static void of(Throwable throwable) {
        of(null, throwable);
    }

    public static void of(Component parent, Throwable throwable) {
        of(parent, null, throwable);
    }

    public static void of(Component parent, String title, Throwable throwable) {
        ExceptionDialog dialog = new ExceptionDialog(title, throwable);
        dialog.pack();
        dialog.setLocationRelativeTo(parent); // To the center of the frame
        dialog.setVisible(true);
    }
}
