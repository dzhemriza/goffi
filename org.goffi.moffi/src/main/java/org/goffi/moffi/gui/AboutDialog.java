package org.goffi.moffi.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

/**
 * Moffi about dialog
 */
public class AboutDialog extends JDialog {

    final private ResourceBundle resourceBundle;

    public AboutDialog() {
        resourceBundle = ResourceBundle.getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings");

        this.setTitle(resourceBundle.getString("about.dialog.title"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new MigLayout());

        JLabel labelAppData = new JLabel(getAboutText());
        centerPanel.add(labelAppData);

        JButton buttonOk = new JButton(resourceBundle.getString("button.ok"));
        buttonOk.setDefaultCapable(true);
        buttonOk.addActionListener((l) -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new MigLayout());
        bottomPanel.add(buttonOk);

        mainPanel.add(centerPanel, "dock center");
        mainPanel.add(bottomPanel, "dock south");

        // Enter, Esc
        mainPanel.registerKeyboardAction((l) -> this.dispatchEvent(
                new WindowEvent(this, WindowEvent.WINDOW_CLOSING)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainPanel.registerKeyboardAction((l) -> this.dispatchEvent(
                new WindowEvent(this, WindowEvent.WINDOW_CLOSING)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.add(mainPanel);

        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setMinimumSize(GuiConstants.MINIMUM_SIZE);
        this.pack();
    }

    private String getAboutText() {
        return resourceBundle.getString("about.dialog.html.text");
    }

    public static void showIt(Component parent) {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.setLocationRelativeTo(parent); // To the center of the frame
        aboutDialog.setVisible(true);
    }

}
