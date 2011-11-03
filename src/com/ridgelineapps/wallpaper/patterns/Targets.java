/*
 * Copyright (C) 2011 Wallpaper for Tablets Open Source Project
 * 
 * This file is part of Wallpaper for Tablets
 * 
 * Wallpaper for Tablets is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Wallpaper for Tablets.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ridgelineapps.wallpaper.patterns;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ridgelineapps.wallpaper.Rand;
import com.ridgelineapps.wallpaper.Utils;

public class Targets extends PatternWallpaper {
    public static final int SHAPES_PER_LONG_SIDE = 12;
    public static final int NUM_CIRCLES = 6;

    public Paint paint;
    public int radius;
    public int targetBump;
    public int lineWidth;
    
    public Targets() {
        background = Utils.createPaint(255, 208, 197, 169);
    }

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        paint = Utils.createPaint(150, 25, 25);

        int scale = longSide / (SHAPES_PER_LONG_SIDE - 1);

        radius = (int) (scale / 2);
        targetBump = (int) (radius * 0.2f);
        lineWidth = (int) (radius / NUM_CIRCLES);

        int xIncrement = (int) scale;
        int yIncrement = (int) (radius * 0.6);


        int yIndex = 0;
        int y = yIncrement;

        while (y < height + radius) {
            int x = 0;
            if (yIndex % 2 == 1) {
                x += radius;
            }
            while (x < width + radius) {
                Circle shape = new Circle(x, y);
                shapes.add(shape);
                x += xIncrement;
            }
            yIndex++;
            y += yIncrement;
        }
    }

    public class Circle extends Shape {
        int x;
        int y;

        public Circle(int x, int y) {
            this.x = x;
            this.y = y;

            bounds = new Rect(x - radius, y - radius, x + radius, y + radius);
        }

        @Override
        public void tweak() {
            x += Rand.plusOrMinusI(2);
            y += Rand.plusOrMinusI(2);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (!visible) {
                return;
            }
            int r = radius;
            Paint p;
            for (int i = 0; i < NUM_CIRCLES; i++) {
                if (i % 2 == 0) {
                    p = paint;
                } else {
                    p = background;
                }
                canvas.drawCircle(x, y, r, p);
                r -= lineWidth;
            }
        }
    }
}
