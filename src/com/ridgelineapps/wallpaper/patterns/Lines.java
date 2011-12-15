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


public class Lines extends PatternWallpaper {
    public Paint[] fore;
    public static final int alpha = 150;
//    public static final int hAlpha = 50;
    
    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);

        fore = new Paint[10];
        
        background = Utils.createPaint(40, 40, 140);
        
        fore[0] = Utils.createPaint(64, 64, 140);
        fore[1] = Utils.createPaint(alpha, 110, 80, 35);
        fore[2] = Utils.createPaint(alpha, 80, 40, 40);
        fore[3] = Utils.createPaint(alpha, 210, 100, 110);
        fore[4] = Utils.createPaint(alpha, 200, 110, 160);
        fore[5] = Utils.createPaint(alpha, 160, 170, 70);
        fore[6] = Utils.createPaint(alpha, 250, 130, 30);
        fore[7] = Utils.createPaint(alpha, 203, 60, 90);
        fore[8] = Utils.createPaint(alpha, 160, 200, 210);
        // 250, 200, 20
        fore[9] = Utils.createPaint(125, 95, 80);

        int i = Rand.i(5);
//        while(i < longSide * 2) {
//            Line line = new Line(fore[Rand.i(fore.length)], i, -20, -20, i, Utils.randRange(25, 40));
//            line.tweak((int) (i * 0.03 + 10));
//            shapes.add(line);
//            i += line.lineWidth / 5;
//        }
        while(i < longSide * 2) {
            Line line = new Line(fore[Rand.i(fore.length)], i, -70, -70, i, Rand.rangeI(40, 55));
            line.tweak(i * 0.06f);
            shapes.add(line);
            i += line.lineWidth / 5;
        }
    }

    @Override
    public void touched(MotionEvent event) {
        super.touched(event);

        if (event != null) {
        }
    }

    public class Line extends Shape {
        Paint p;
//        Paint h;
        int x1;
        int y1;
        int x2;
        int y2;
        int lineWidth;

        public Line(Paint p, int x1, int y1, int x2, int y2, int lineWidth) {
            this.p = p;
//            this.h = Utils.copy(p);
//            h.setAlpha(hAlpha);
            p.setStrokeWidth(lineWidth);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.lineWidth = lineWidth;

            bounds = new Rect(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
        }

        public void tweak(float fudgeF) {
            int fudge = (int) fudgeF;
            x1 += Rand.plusOrMinusI(fudge);
            y1 += Rand.plusOrMinusI(fudge);
            x2 += Rand.plusOrMinusI(fudge);
            y2 += Rand.plusOrMinusI(fudge);
//            x1 += Utils.randPlusOrMinus(fudge);
//            y1 -= Math.abs(Rand.i(fudge));
//            x2 -= Math.abs(Rand.i(fudge));
//            y2 += Utils.randPlusOrMinus(fudge);
        }
        
        @Override
        public void draw(Canvas canvas) {
            if (!visible) {
                return;
            }

            canvas.drawLine(x1, y1, x2, y2, p);
//            int off = 3;
//            canvas.drawLine(x1 - off, y1 - off, x2 - off, y2 - off, h);
//            canvas.drawLine(x1 + off, y1 + off, x2 + off, y2 + off, h);
//            canvas.drawLine(x1 - off, y1 + off, x2 - off, y2 + off, h);
//            canvas.drawLine(x1 + off, y1 - off, x2 + off, y2 - off, h);
        }
    }
}
