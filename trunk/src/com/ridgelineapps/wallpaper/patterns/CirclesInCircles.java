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

public class CirclesInCircles extends PatternWallpaper {
    // TODO: tweak numbers for performance

    public static final int MAX_INNER_CIRCLES = 13;
    public static final int MIN_INNER_CIRCLES = 5;

    int numCircles = 100;
    long scale;
    int smallestRadiusAllowed;

    public Paint[] fore;
    public Paint[] border;

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        ready = false;
        
        smallestRadiusAllowed = longSide / 50;

        int alpha = 200;
        background = Utils.createPaint(255, 85, 58, 20);
        fore = new Paint[4];
        fore[0] = Utils.createPaint(alpha, 85, 58, 20);
        fore[1] = Utils.createPaint(alpha, 119, 180, 180);
        fore[2] = Utils.createPaint(alpha, 185, 220, 74);
        fore[3] = Utils.createPaint(alpha, 243, 251, 228);

        border = new Paint[4];
        for (int i = 0; i < fore.length; i++) {
            if (i == 0) {
                border[i] = Utils.createPaint(fore[i], -30, -140);
            } else {
                border[i] = Utils.createPaint(fore[i], -120, -60);
            }
            border[i].setStyle(Paint.Style.STROKE);
            border[i].setStrokeWidth(3);
        }

        scale = longSide / 6;
        int radius = (int) (scale / 2);

        // TODO: cleaner way than that many tries...
        int initialTries = numCircles * 30;
        int tries = initialTries;
        int i = 0;

        while (tries-- > 0 && i < numCircles) {
            int x = Rand.i(width);
            int y = Rand.i(height);
            // TODO: optimize, don't compute entire pic...

            int smaller = 0;
            
            float factor = (float) tries / initialTries; 
            if(factor < 0.25) {
                break;
            }
            
            int r = (int) (factor * radius);
//            if (tries < initialTries * 1 / 3) {
//                smaller = radius / 3;
//            }
//
//            if (tries < initialTries * 2 / 3) {
//                smaller = radius * 2 / 3;
//            }

//            boolean inside = false;
//            for (Shape check : shapes) {
//                if(check.getBounds().contains(x, y)) {
//                    inside = true;
//                    break;
//                }
//            }
//            if(inside) {
//                continue;
//            }
            
//            Ripple rip = new Ripple(radius - smaller, x, y);
            Ripple rip = new Ripple(r - smaller, x, y);
            boolean add = true;

            for (Shape check : shapes) {
                if (check.overlaps(rip)) {
                    add = false;
                    break;
                }
            }

            if (add) {
                i++;
                //TODO: put back here
                rip.buildInnerCircles();
                shapes.add(rip);
            }
        }

        randomize();
        ready = true;
    }

    public class Ripple extends Shape {
        public static final int OVERLAP_BUFFER = 3;

        int[] x;
        int[] y;
        int[] radius;
        int numCircles;
        Paint[] p;
        Paint[] h;
        
        int radius_;
        int x_;
        int y_;

        public Ripple(int radius_, int x_, int y_) {
            // animating = true;

            radius_ += Rand.plusOrMinusF(radius_ / 3);

            bounds = new Rect(x_ - radius_, y_ - radius_, x_ + radius_, y_ + radius_);

            this.radius_ = radius_;
            this.x_ = x_;
            this.y_ = y_;
            
            this.numCircles = Rand.rangeI(MIN_INNER_CIRCLES, MAX_INNER_CIRCLES);
            this.x = new int[numCircles];
            this.y = new int[numCircles];
            this.radius = new int[numCircles];
            this.p = new Paint[numCircles];
            this.h = new Paint[numCircles];

            x[0] = x_;
            y[0] = y_;
            radius[0] = radius_;
            int color = Rand.i(fore.length);
            // }

            p[0] = fore[color];
            h[0] = border[color];
            
//            buildInnerCircles();
        }

        public void buildInnerCircles() {
            int lastxOffset = 0;
            int lastyOffset = 0;

            for (int i = 1; i < numCircles; i++) {
                int seed = i + 1; // (int) (0.6 * radius_ / numCircles); // i + 1
                seed *= 3;
                int xOffset = Rand.i(seed);
                xOffset -= seed / 2;
                int yOffset = Rand.i(seed);
                yOffset -= seed / 2;
                x_ += xOffset;
                y_ += yOffset;

                // TODO: could be simpler
                double change = Math.pow(lastxOffset, 2) + Math.pow(lastyOffset, 2);
                if (change > 0) {
                    change = Math.sqrt(change);
                    if (change > 0) {
                        radius_ -= change;
                        int bump = (int) (change / 2);
                        if (bump > 0) {
                            radius_ -= Rand.i(bump) + change / 3;
                        }
                    }
                }
                lastxOffset = xOffset;
                lastyOffset = yOffset;

                if (radius_ < smallestRadiusAllowed) {
                    break;
                }
                x[i] = x_;
                y[i] = y_;
                radius[i] = radius_;
                int color;

                // if(i == numCircles - 1) {
                // color = 0;
                // }
                // else {
                color = Rand.i(fore.length);
                // }

                p[i] = fore[color];
                h[i] = border[color];
            }
        }

        @Override
        public boolean overlaps(Shape single) {
            //TODO: Use shapes calls
            Ripple ripple = (Ripple) single;

            int xDiff = ripple.x[0] - x[0];
            int yDiff = ripple.y[0] - y[0];
            float radDiff = ripple.radius[0] + radius[0] + OVERLAP_BUFFER;

            radDiff *= 0.8;

            if (xDiff * xDiff + yDiff * yDiff < radDiff * radDiff) {
                return true;
            }

            return false;
        }

        @Override
        public void draw(Canvas canvas) {
            if (!visible) {
                return;
            }

            for (int i = 0; i < numCircles; i++) {
                if (x[i] != 0) {
//                     p[i].setAlpha(maxAlpha - touchCount);
//                     h[i].setAlpha(Math.max(20, maxHighlightAlpha - touchCount));
                    canvas.drawCircle(x[i], y[i], radius[i], p[i]);
                    canvas.drawCircle(x[i], y[i], radius[i], h[i]);
                }
            }
        }
    }
}
