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
import android.view.MotionEvent;

import com.ridgelineapps.wallpaper.Rand;
import com.ridgelineapps.wallpaper.Utils;


public class Dots extends PatternWallpaper {
    // TODO: tweak numbers for performance
    int scale;
    int radius;
    
    static final boolean test = false;

    public Paint highlight;
    public Paint[] fore;

    public Dots() {
        background = Utils.createPaint(250, 250, 150);
    }
    
    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);

        highlight = Utils.createPaint(150, 150, 150, 150);
        
        fore = new Paint[10];
        fore[0] = Utils.createPaint(247, 229, 110);
        fore[1] = Utils.createPaint(249, 197, 77);
        fore[2] = Utils.createPaint(225, 200, 80);
        fore[3] = Utils.createPaint(250, 200, 210);
        fore[4] = Utils.createPaint(235, 150, 60);
        fore[5] = Utils.createPaint(215, 200, 190);
        fore[6] = Utils.createPaint(190, 95, 35);
        fore[7] = Utils.createPaint(196, 213, 47);
        fore[8] = Utils.createPaint(150, 175, 200);
        fore[9] = Utils.createPaint(125, 95, 80);

//        scale = longSide / 100;
//        radius = (int) (scale / 3);

        scale = longSide / 25;
        if(test) {
            scale = longSide / 75;
        }
        radius = (int) (scale * 0.41);
        
        int yIndex = 0;
        int y = Rand.i(radius);

        while (y < height + radius) {
            int x = 0;
            if (yIndex % 2 == 1) {
                //x += radius;
            }
            
            int skipCount = 0;
            while (x < width + radius) {
                
                //TODO: util method to give boolean on this check
                if(skipCount == 0 && Rand.i(55) < 1) {
//                    skipCount = 1;
                }
                
                if(skipCount == 0 && Rand.i(55) < 1) {
//                    skipCount = Utils.randRange(3, 4);
                }
                
                if(skipCount > 0) {
                    skipCount--;
                    x += scale;
                }
                else
                {
                    int xToUse = x;
                    int yToUse = y;
                    if(test) {
                        xToUse = x + Rand.plusOrMinusI((int)(y * y * 0.00002));
                        yToUse = y + Rand.plusOrMinusI((int)(y * y * 0.00002));
//                        yToUse = y + Rand.i((int)(x * x * 0.00002));
                    }
                    Dot dot = new Dot(fore, radius, xToUse, yToUse);
                    shapes.add(dot);
                    x += scale;
                }
            }
            yIndex++;
            y += scale;
        }

        randomize();
    }

    @Override
    public void touched(MotionEvent event) {
        super.touched(event);

        if (event != null) {
        }
    }

    public class Dot extends Shape {

        int x;
        int y;
        int r;
        Paint p;
        int dir;

        public Dot(Paint[] fore, int r, int x, int y) {
            p = fore[Rand.i(fore.length)];

            this.x = x;
            this.y = y;
            this.r = r;
            
            bounds = new Rect(x - r, y - r, x + r, y + r);
        }

        @Override
        public void tweak() {
        }

        @Override
        public boolean overlaps(Shape single) {

            return false;
        }

        @Override
        public void draw(Canvas canvas) {
            if (!visible) {
                return;
            }

            canvas.drawCircle(x, y, r + 1, highlight);
            canvas.drawCircle(x, y, r, p);
        }
    }
}
