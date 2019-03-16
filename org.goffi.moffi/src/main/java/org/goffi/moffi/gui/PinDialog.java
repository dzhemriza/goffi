/*
 * org.goffi.moffi
 *
 * File Name: PinDialog.java
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
import org.goffi.core.domainmodel.Pin;
import org.goffi.moffi.gui.convertor.impl.PinConverter;
import org.goffi.moffi.gui.convertor.impl.exceptions.EmptyPinException;
import org.goffi.moffi.gui.convertor.impl.exceptions.NonNumericPinValueException;

import javax.swing.*;

/**
 * Represents a PIN question dialog
 */
public class PinDialog extends AbstractMoffiDialog {

    private final JPasswordField pinField = new JPasswordField();
    private Pin pin;

    public PinDialog() {
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel mainPanel = this.getMainPanel();
        mainPanel.setLayout(new MigLayout());

        // Pin label and text
        JLabel labelPin = new JLabel(
                resourceBundle.getString("pin.dialog.pin.label"));
        mainPanel.add(labelPin, "wrap");

        pinField.setColumns(GuiConstants.TEXT_COLUMNS_SIZE);
        mainPanel.add(pinField, "wrap");
    }

    @Override
    protected boolean validateDataOnOk() {
        try {
            PinConverter pinConverter = new PinConverter();
            this.pin = pinConverter.convert(pinField.getPassword());

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
        } catch (Exception e) {
            ExceptionDialog.of(e);
            return false;
        }
    }

    public Pin getPin() {
        return this.pin;
    }
}
