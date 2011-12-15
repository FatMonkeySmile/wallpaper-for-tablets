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

import com.ridgelineapps.wallpaper.Rand;
import com.ridgelineapps.wallpaper.Utils;

public class Moons extends PatternWallpaper {
    int numMoons = 24;
    long scale;

    public Paint[] fore;
    public Paint[] border;

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        ready = false;
        
//        background = Utils.createPaint(255, 85, 85, 85);
        background = Utils.createPaint(255, 45, 35, 25);
        
        int alpha = 200;
        int levelsOfShade = 3;
        int dec = alpha / levelsOfShade;
        fore = new Paint[levelsOfShade];
        border = new Paint[levelsOfShade];
        for(int i=0; i < levelsOfShade; i++) {
            fore[i] = Utils.createPaint(alpha, 55, 55, 245);
            border[i] = Utils.createPaint(alpha + 55, 0, 0, 200);
            border[i].setStyle(Paint.Style.STROKE);
            border[i].setStrokeWidth(3);
            alpha -= dec;
        }
        
        scale = longSide / 6;
        int radius = (int) (scale / 2);

        // TODO: cleaner way than that many tries...
        int initialTries = numMoons * 30;
        int tries = initialTries;
        int i = 0;

        while (tries-- > 0 && i < numMoons) {
            int x = Rand.i(width);
            int y = Rand.i(height);
            // TODO: optimize, don't compute entire pic...

            float factor = (float) tries / initialTries; 
            if(factor < 0.25) {
                break;
            }
            
            int r = (int) (factor * radius);
            Moon moon = new Moon(r, x, y, Rand.i(levelsOfShade));
            boolean add = true;

            for (Shape check : shapes) {
                if (check.overlaps(moon)) {
                    add = false;
                    break;
                }
            }
            
//            int BORDER = longSide / 10;
//            if(x < BORDER || x > width - BORDER) {
//                add = false;
//            }
//            if(y < BORDER || y > height - BORDER) {
//                add = false;
//            }

            if (add) {
                i++;
                //TODO: put back here
                moon.build();
                shapes.add(moon);
            }
        }

        ready = true;
    }

    public class Moon extends Shape {
        public static final double INNER_RATIO = 0.955;

        int x;
        int y;
        int r;
        
        int x2;
        int y2;
        int r2;
        
        int paintIndex;

        public Moon(int r, int x, int y, int paintIndex) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.paintIndex = paintIndex;
        }

        public void build() {
            
            //TODO: expand this in a different pattern
            // *************
//            double innerR = r * INNER_RATIO;
//            double angle;
//            
//            if(Utils.randBoolean()) {
//                angle = Math.PI + Utils.randPlusOrMinus(Math.PI / 15);
//            }
//            else {
//                angle = 2 * Math.PI + Utils.randPlusOrMinus(Math.PI / 15);
//            }
//            double offsetX = Math.cos(angle) * innerR * 0.03; 
//            double offsetY = Math.sin(angle) * innerR * 0.03; 
//            
//            x2 = (int) (offsetX + x);
//            y2 = (int) (offsetY + y);
//            r2 = (int) (innerR * 1.015);
            // *************
          double innerR = r * INNER_RATIO;
          double angle;
          
          if(Rand.b()) {
              angle = Math.PI + Rand.plusOrMinusD(Math.PI / 15);
          }
          else {
              angle = 2 * Math.PI + Rand.plusOrMinusD(Math.PI / 15);
          }
          
          angle = Rand.d(Math.PI * 2);
          double offsetX = Math.cos(angle) * innerR * 0.13; 
          double offsetY = Math.sin(angle) * innerR * 0.13; 
          
          x2 = (int) (offsetX + x);
          y2 = (int) (offsetY + y);
          r2 = (int) (innerR * 1.015);
        }

        @Override
        public boolean overlaps(Shape shape) {
            //TODO: Use shapes calls
            Moon moon = (Moon) shape;

            int xDiff = moon.x - x;
            int yDiff = moon.y - y;
            float rDiff = r + moon.r;

            rDiff *= 1.3;

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

            Paint p = fore[paintIndex];
            Paint b = border[paintIndex];
            
            canvas.drawCircle(x, y, r, p);
            canvas.drawCircle(x, y, r, b);
            
            canvas.drawCircle(x2, y2, r2, background);
        }
    }
}
