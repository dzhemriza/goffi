/*
 * org.goffi.moffi
 *
 * File Name: AbstractMoffiDialog.java
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

/**
 * Common class for all dialogs in Moffi
 */
public abstract class AbstractMoffiDialog extends JDialog {

    //
    // This is how dialog looks like
    //
    // -----------------------------------------------
    // |    Title Here                           | X |
    // -----------------------------------------------
    // |                                             |
    // |                                             |
    // |                                             |
    // |             MAIN PANEL HERE                 |
    // |          All components have to be          |
    // |          added here                         |
    // |                                             |
    // |                                             |
    // | [ OK ] [ Cancel]                            |
    // -----------------------------------------------
    //

    private final JPanel mainPanel;
    private boolean okPressed = false;
    protected final ResourceBundle resourceBundle;

    public AbstractMoffiDialog() {
        this.resourceBundle = ResourceBundle.getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings");

        JPanel globalPanel = new JPanel();
        globalPanel.setLayout(new MigLayout());

        mainPanel = new JPanel();

        globalPanel.add(mainPanel, "wrap");

        // Buttons - OK and Cancel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton(resourceBundle.getString("button.ok"));
        okButton.addActionListener((l) -> onOk());
        okButton.setDefaultCapable(true);
        bottomPanel.add(okButton);

        JButton cancelButton = new JButton(resourceBundle.getString("button.cancel"));
        cancelButton.addActionListener((l) -> onCancel());
        bottomPanel.add(cancelButton);

        globalPanel.add(bottomPanel);

        // Enter, Esc
        globalPanel.registerKeyboardAction((l) -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        globalPanel.registerKeyboardAction((l) -> onOk(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Dialog related properties
        this.add(globalPanel);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false); // hides the icon
        this.setMinimumSize(GuiConstants.MINIMUM_SIZE);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * @return {@code true} if the data is valid, {@code false} otherwise
     */
    protected abstract boolean validateDataOnOk();

    private void onOk() {
        if (validateDataOnOk()) {
            okPressed = true;
            dispatchCloseEvent();
        }
    }

    private void onCancel() {
        dispatchCloseEvent();
    }

    private void dispatchCloseEvent() {
        // Reuse this code
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void showDialog(Component parent) {
        this.pack();
        this.setLocationRelativeTo(parent); // To the center of the frame
        this.setVisible(true);
    }

    public void showDialog() {
        showDialog(null);
    }

    public boolean isOkPressed() {
        return this.okPressed;
    }
}
