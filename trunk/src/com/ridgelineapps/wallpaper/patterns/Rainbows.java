/*
 * Copyright (C) 2011 Wallpaper for Tablets (http://code.google.com/p/wallpaper-for-tablets)
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

public class Rainbows extends PatternWallpaper {
    // TODO: tweak numbers for performance

    int border;
    int numCircles;

    public static final int NUM_INNER_CIRCLES = 4;

    public Paint[] fore;

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        shapes.clear();
        ready = false;
        
        if(longSide == width) {
            numCircles = 25;
        }
        else {
            numCircles = 10;
        }

        int alpha = 255;
//        background = Utils.createPaint(255, 250, 250, 250);
        background = Utils.createPaint(255, 30, 30, 30);

        fore = new Paint[4];
        fore[0] = Utils.createPaint(alpha, 0, 100, 120);
        fore[1] = Utils.createPaint(alpha, 200, 220, 50);
        fore[2] = Utils.createPaint(alpha, 200, 10, 120);
        fore[3] = Utils.createPaint(alpha, 245, 245, 215);
        
        border = width / 17;
        int radius = (int) longSide / 12;

        // TODO: cleaner way than that many tries...
        int initialTries = numCircles * 60;
        int tries = initialTries;
        int i = 0;

        while (tries-- > 0 && i < numCircles) {
            float x = Rand.i(width);
            float y = Rand.i(height);

            // boolean inside = false;
            // for (Shape check : shapes) {
            // if(check.getBounds().contains(x, y)) {
            // inside = true;
            // break;
            // }
            // }
            // if(inside) {
            // continue;
            // }

            float factor;
            int rand = Rand.i(12);
            if (rand < 3) {
                factor = Rand.rangeF(0.9f, 1);
            } else if (rand < 9) {
                factor = Rand.rangeF(0.6f, 0.9f);
            } else {
                factor = Rand.rangeF(0.5f, 6f);
            }

            float r = factor * radius;
//            int r = radius;

            boolean add = true;

//            if (x < width * 0.33 && y < height / 2) {
//                add = false;
//            }
//
//            if (x > width * 0.66 && y < height / 2) {
//                add = false;
//            }
            float topXBorder = 0.35f;
            int xCheck = (int) (width * topXBorder);
            float yBorder = 1f;
//            if (x > width - width * topXBorder && y > (float) x / (width * topXBorder) * height * yBorder) {
//                add = false;
//            }
            if (x > width - width * topXBorder && y > (float) x / (width * topXBorder) * height * yBorder) {
//                add = false;
            }

//            if (x < width * topXBorder && y > (width - x) / (width * topXBorder) * height * yBorder) {
//                add = false;
//            }
            if (y < height / 2 && (x < width / 4 || x > width * .75)) {//width * topXBorder && y > (width - x) / (width * topXBorder) * height * yBorder) {
//                add = false;
            }
            
            if(x < xCheck && y < height *0.8- x) {
                add = false;
            }
            
            if(x > width - xCheck && y < height * 0.8 - (width - x)) {
                add = false;
            }
            
//            if(y - r < height / 6) {
//                add = false;
//            }
            if(y - r < height - shortSide * 1.3) {
                add = false;
            }
//            if (x - r < border || x + r > width - border || y - r < 0 || y + r > height - border
            // || y + r < height / 2
                if (x - r - border < 0 || x + r + border > width || y - r - border < 0 || y + r > height // - border
            ) {
                add = false;
            }
            // else {

            if (add) {
                Rainbow bow = new Rainbow((int) r, (int) x, (int) y);
                for (Shape check : shapes) {
                    if (check.overlaps(bow)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    i++;
                    shapes.add(bow);
                }
            }
        }

        randomize();
        ready = true;
    }

    public class Rainbow extends Shape {
        Paint[] p;
        Paint[] line;

        int r;
        int x;
        int y;

        int lineWidth;

        int rightLeft = 1;

        public Rainbow(int r, int x, int y) {
            // animating = true;

            // radius_ += Utils.randPlusOrMinus(radius_ / 3);

            this.r = r;
            this.x = x;
            this.y = y;

            if (Rand.b()) {
                rightLeft = -1;
            }

            lineWidth = r / NUM_INNER_CIRCLES + 1;
            bounds = new Rect(x - r, y - r, x + r, y + r);

            this.p = new Paint[NUM_INNER_CIRCLES];

            int lastColor = -1;

            for (int i = 0; i < NUM_INNER_CIRCLES; i++) {
                boolean found = false;
                while (!found) {
                    int color = Rand.i(fore.length);
                    p[i] = fore[color];
                    if (color == lastColor) {
                        found = false;
                    } else {
                        lastColor = color;
                        found = true;
                    }
                }
            }

            line = new Paint[NUM_INNER_CIRCLES];
            for (int i = 0; i < NUM_INNER_CIRCLES; i++) {
                line[i] = Utils.copy(p[i]);
                line[i].setStrokeWidth(lineWidth);
            }
        }

        @Override
        public boolean overlaps(Shape shape) {
            // TODO: Use shapes calls
            Rainbow bow = (Rainbow) shape;

            int xDiff = bow.x - x;
            int yDiff = bow.y - y;
            float rDiff = bow.r + r;

            if (xDiff * xDiff + yDiff * yDiff < rDiff * rDiff) {
                return true;
            }

            return false;
        }

        @Override
        public void draw(Canvas canvas) {
            if (!visible) {
                return;
            }

            float currR = r;
            for (int i = 0; i < NUM_INNER_CIRCLES; i++) {
                if (i < NUM_INNER_CIRCLES - 1) {
                    float x1 = currR; //(float) lineWidth * (NUM_INNER_CIRCLES - i);
                    if (rightLeft > 0) {
                        x1 -= lineWidth; // ?
                    } else {
                    }
                    x1 *= rightLeft;
                    x1 += x;
                    
                    canvas.drawRect(x1, y, x1 + lineWidth, height, line[i]);
                }

                canvas.drawCircle(x, y, currR, p[i]);
                currR -= lineWidth;

            }
        }
    }
}
