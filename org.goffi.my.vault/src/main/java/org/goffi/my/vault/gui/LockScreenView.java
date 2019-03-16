/*
 * org.goffi.my.vault
 *
 * File Name: LockScreenView.java
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
package org.goffi.my.vault.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Random;

public class LockScreenView extends Pane {

    private final static int MIN_RECTS = 10;
    private final static int MAX_RECTS = 100;
    private final static int PERCENT = 100;
    private final static int MIN_RECT_DIM = 1;
    private final static int MAX_RECT_DIM = 10;

    private class Rect {
        private double x;
        private double y;
        private double w;
        private double h;

        Rect(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        Rect scaledToArea(double w, double h) {
            return new Rect((this.x / PERCENT * w), (this.y / PERCENT) * h,
                    (this.w / PERCENT) * w, (this.h / PERCENT) * h);
        }

        void stroke(GraphicsContext gc) {
            gc.strokeRect(x, y, w, h);
        }
    }

    private final Canvas canvas;
    private final Random random = new Random();
    private final Rect[] rects;

    public LockScreenView() {
        canvas = new Canvas(getWidth(), getHeight());
        getChildren().add(canvas);
        widthProperty().addListener(e -> canvas.setWidth(getWidth()));
        heightProperty().addListener(e -> canvas.setHeight(getHeight()));

        int numberOfRects = random.nextInt(
                MAX_RECTS - MIN_RECTS) + MIN_RECTS + 1;
        rects = new Rect[numberOfRects];
        for (int i = 0; i < numberOfRects; ++i) {
            int dim = random.nextInt(
                    MAX_RECT_DIM - MIN_RECT_DIM) + MIN_RECT_DIM + 1;

            Rect r = new Rect(random.nextInt(PERCENT), random.nextInt(PERCENT),
                    dim, dim);
            rects[i] = r;
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setStroke(Color.BLACK);

        for (Rect rect : rects) {
            rect.scaledToArea(getWidth(), getHeight()).stroke(gc);
        }
    }
}
