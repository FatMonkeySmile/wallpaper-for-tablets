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

public class ThinCircles extends PatternWallpaper {
    int numCircles = 1000;
    long scale;

    public Paint[] fore;
//    public Paint[] border;

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        ready = false;
        
        background = Utils.createPaint(255, 35, 35, 35);
        
        int alpha = 200;
        int levelsOfShade = 4;
        int dec = alpha / levelsOfShade;
        fore = new Paint[levelsOfShade];
        for(int i=0; i < levelsOfShade; i++) {
            fore[i] = Utils.createPaint(alpha, 230, 55, 55);
            fore[i].setStyle(Paint.Style.STROKE);
            fore[i].setStrokeWidth(1);
            alpha -= dec;
        }

        scale = longSide / 6;
        int radius = (int) (scale / 2);

        int i = 0;
        int border = (int) (longSide * 0.08);
        while (i < numCircles) {
            int x = Rand.rangeI(border, width - border);
            int y = Rand.rangeI(border, height - border); 

            float factor = (float) Utils.rand.nextDouble();
            float step = 10f;
            factor = ((int) (factor * step)) / step;
            
            if(factor != 0) {
                int r = (int) (factor * radius);
                
                shapes.add(new ThinCircle(r, x, y, Rand.i(levelsOfShade)));
                i++;
            }
        }

        ready = true;
    }

    public class ThinCircle extends Shape {
        int x;
        int y;
        int r;
        
        int paintIndex;

        public ThinCircle(int r, int x, int y, int paintIndex) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.paintIndex = paintIndex;
        }

        public void build() {
        }

        @Override
        public boolean overlaps(Shape shape) {
            //TODO: Use shapes calls
            ThinCircle t = (ThinCircle) shape;

            int xDiff = t.x - x;
            int yDiff = t.y - y;
            float rDiff = r + t.r;

            rDiff *= 1.5;

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

            canvas.drawCircle(x, y, r, fore[paintIndex]);
        }
    }
}
