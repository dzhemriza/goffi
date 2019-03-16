/*
 * org.goffi.moffi
 *
 * File Name: ShadowModePanel.java
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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class ShadowModePanel extends JPanel {

    private static final int MIN_NUMBER_OF_RECTANGLES = 10;
    private static final int MAX_NUMBER_OF_RECTANGLES = 100;
    private final static int MIN_RECT_DIM = 1;
    private final static int MAX_RECT_DIM = 10;
    private final static int PERCENT = 100;
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.BOLD, 42);

    private final Rectangle[] rectangles;
    private final String shadowModeText;

    public ShadowModePanel() {
        shadowModeText = ResourceBundle
                .getBundle("org/goffi/moffi/gui/i18n/ApplicationStrings")
                .getString("shadow.mode.text");

        int numberOfRectangles = ThreadLocalRandom.current().nextInt(
                MIN_NUMBER_OF_RECTANGLES, MAX_NUMBER_OF_RECTANGLES + 1);
        rectangles = new Rectangle[numberOfRectangles];
        for (int i = 0; i < rectangles.length; ++i) {
            int dim = ThreadLocalRandom.current().nextInt(MIN_RECT_DIM, MAX_RECT_DIM + 1);

            // These numbers are in % based on the panel width and height
            rectangles[i] = new Rectangle(
                    /*x*/ ThreadLocalRandom.current().nextInt(0, PERCENT + 1),
                    /*y*/ ThreadLocalRandom.current().nextInt(0, PERCENT + 1),
                    /*w*/ dim,
                    /*h*/ dim);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;

            // Draw rectangles
            for (Rectangle rect : rectangles) {
                int x = (int) ((rect.getX() / 100) * this.getWidth());
                int y = (int) ((rect.getY() / 100) * this.getHeight());
                int w = (int) ((rect.getWidth() / 100) * this.getWidth());
                int h = (int) ((rect.getHeight() / 100) * this.getHeight());

                Rectangle r = new Rectangle(x, y, w, h);

                g2.draw(r);
            }

            // Draw the shadow mode
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(DEFAULT_FONT);
            g2.setColor(Color.RED);

            FontMetrics fontMetrics = g2.getFontMetrics();
            Rectangle2D stringRect = fontMetrics.getStringBounds(shadowModeText, g2);

            int x = (int) (this.getWidth() / 2 - stringRect.getWidth() / 2);
            int y = (int) (this.getHeight() / 2 - stringRect.getHeight() / 2);

            g2.drawString(shadowModeText, x, y);

            // Draw 4 small rectangles on each corner
            Rectangle r = new Rectangle(0, 0, 10, 10);
            g2.draw(r);
            r.setBounds(this.getWidth() - 11, 0, 10, 10);
            g2.draw(r);
            r.setBounds(this.getWidth() - 11, this.getHeight() - 11, 10, 10);
            g2.draw(r);
            r.setBounds(0, this.getHeight() - 11, 10, 10);
            g2.draw(r);
        }
    }
}
