/*
 * org.goffi.moffi
 *
 * File Name: CryptoInfoDialog.java
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
import org.goffi.core.domainmodel.MemorableWord;
import org.goffi.core.domainmodel.Password;
import org.goffi.core.domainmodel.Pin;
import org.goffi.core.domainmodel.UserEncryptionKey;
import org.goffi.core.domainmodel.exceptions.RepeatedPasswordDontMatchException;
import org.goffi.core.domainmodel.exceptions.WeakPasswordException;
import org.goffi.core.domainmodel.exceptions.ZeroPinException;
import org.goffi.moffi.gui.convertor.impl.PinConverter;
import org.goffi.moffi.gui.convertor.impl.exceptions.EmptyPinException;
import org.goffi.moffi.gui.convertor.impl.exceptions.NonNumericPinValueException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class CryptoInfoDialog extends AbstractMoffiDialog {

    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField repeatPasswordField = new JPasswordField();
    private final JPasswordField memorableWordField = new JPasswordField();
    private final JPasswordField pinField = new JPasswordField();
    private boolean showRepeatPasswordField = true;

    private UserEncryptionKey userEncryptionKey;

    public CryptoInfoDialog(boolean showRepeatPasswordField) {
        this.showRepeatPasswordField = showRepeatPasswordField;
        initializeLayoutAndComponents();
    }

    private void initializeLayoutAndComponents() {
        JPanel mainPanel = this.getMainPanel();
        mainPanel.setLayout(new MigLayout());

        // Password label and text
        JLabel labelPassword = new JLabel(
                resourceBundle.getString("crypto.information.dialog.password.label"));
        mainPanel.add(labelPassword);

        passwordField.setColumns(GuiConstants.TEXT_COLUMNS_SIZE);
        mainPanel.add(passwordField, "wrap");

        // Repeat Password label and text
        if (showRepeatPasswordField) {
            JLabel labelRepeatPassword = new JLabel(
                    resourceBundle.getString("crypto.information.dialog.repeat.password.label"));
            mainPanel.add(labelRepeatPassword);

            repeatPasswordField.setColumns(GuiConstants.TEXT_COLUMNS_SIZE);
            mainPanel.add(repeatPasswordField, "wrap");
        }

        // Memorable Word
        JLabel labelMemorableWord = new JLabel(
                resourceBundle.getString("crypto.information.dialog.memorable.word.label"));
        mainPanel.add(labelMemorableWord);

        //memorableWordField.setEchoChar((char) 0); // make password visible
        memorableWordField.setColumns(GuiConstants.TEXT_COLUMNS_SIZE);
        mainPanel.add(memorableWordField, "wrap");

        // Pin
        JLabel labelPin = new JLabel(
                resourceBundle.getString("crypto.information.dialog.pin.label"));
        mainPanel.add(labelPin);

        pinField.setColumns(GuiConstants.TEXT_COLUMNS_SIZE);
        mainPanel.add(pinField, "wrap");

        JLabel capsLockLabel = new JLabel();
        capsLockLabel.setText(
                resourceBundle.getString("crypto.information.dialog.caps.lock.html.label"));
        capsLockLabel.setVisible(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
        mainPanel.add(capsLockLabel);

        Runnable updateCapsLock = () -> capsLockLabel.setVisible(
                Toolkit.getDefaultToolkit()
                        .getLockingKeyState(KeyEvent.VK_CAPS_LOCK));

        // Caps Lock
        mainPanel.registerKeyboardAction((l) -> updateCapsLock.run(),
                KeyStroke.getKeyStroke(KeyEvent.VK_CAPS_LOCK, 0, false),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainPanel.registerKeyboardAction((l) -> updateCapsLock.run(),
                KeyStroke.getKeyStroke(KeyEvent.VK_CAPS_LOCK, 0, true),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    @Override
    protected boolean validateDataOnOk() {
        try {
            Password password;

            if (showRepeatPasswordField) {
                password = Password.of(passwordField.getPassword(), repeatPasswordField.getPassword());
            } else {
                password = Password.of(passwordField.getPassword());
            }

            PinConverter pinConverter = new PinConverter();
            Pin pin = pinConverter.convert(pinField.getPassword());

            MemorableWord memorableWord = MemorableWord.of(memorableWordField.getPassword());

            userEncryptionKey = new UserEncryptionKey(password, pin, memorableWord);

            return true;
        } catch (EmptyPinException emptyPinException) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("message.error.empty.pin"),
                    resourceBundle.getString("message.error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (NonNumericPinValueException nonNumericPin) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("message.error.non.numeric.pin.value"),
                    resourceBundle.getString("message.error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (WeakPasswordException weakPassword) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("crypto.information.dialog.password.strength.error.message"),
                    resourceBundle.getString("crypto.information.dialog.password.strength.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (RepeatedPasswordDontMatchException repeatedPasswordDontMatch) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("crypto.information.dialog.password.mismatch.error.message"),
                    resourceBundle.getString("crypto.information.dialog.password.mismatch.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (ZeroPinException zeroPin) {
            JOptionPane.showMessageDialog(this, zeroPin.getMessage(),
                    resourceBundle.getString("message.error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            ExceptionDialog.of(e);
            return false;
        }
    }

    public UserEncryptionKey getUserEncryptionKey() {
        return userEncryptionKey;
    }
}
