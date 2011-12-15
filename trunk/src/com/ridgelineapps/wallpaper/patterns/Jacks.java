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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ridgelineapps.wallpaper.Rand;
import com.ridgelineapps.wallpaper.Utils;

//TODO: put back to old look...
public class Jacks extends PatternWallpaper {
    public int numJacks;
    long scale;
    int at = 0;

    public Paint[] forePaints;
    public Paint[][] glowPaints;
    static final int glowCount = 3;

    public int overlapBuffer;;

    public Jacks() {
        background = Utils.createPaint(255, 230, 230, 215);
    }

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        ready = false;
        
        numJacks = longSide / 85;

        int alpha = 255;
        forePaints = new Paint[3];
        forePaints[0] = Utils.createPaint(alpha, 37, 37, 37);
        forePaints[1] = Utils.createPaint(alpha, 75, 75, 75);
        forePaints[2] = Utils.createPaint(alpha, 50, 50, 50);

        glowPaints = new Paint[forePaints.length][];
        for (int i = 0; i < forePaints.length; i++) {
            glowPaints[i] = new Paint[glowCount];
            Paint last = forePaints[i];
            for (int j = 0; j < glowCount; j++) {
                int diff = 25;
                glowPaints[i][j] = Utils.createPaint(last.getAlpha() - diff, Color.red(last.getColor()) + diff, Color.green(last.getColor()) + diff,
                    Color.blue(last.getColor()) + diff);
                last = glowPaints[i][j];
            }
        }
        
//        for(int i=0; i < 10; i++) {
//            shapes.add(new Jack(50, 300 + i * 10, 300));
//        }
//
//        ready = true;
//        drawPaused = false;
//        
//        if(true) return;
        
        int lineLength = longSide / 9;
        overlapBuffer = lineLength / 3;
        int initialTries = numJacks * 20;
        int tries = initialTries;
        int i = 0;

        while (tries-- > 0 && i < numJacks) {
            int x = Rand.i(width);
            int y = Rand.i(height);
            // TODO: optimize, don't compute entire pic...

            double sizeFactor = 1;
            boolean tweak = true;
            
//            sizeFactor = Utils.randRange(0.4f, 1f);

            if (tries < initialTries * 2 / 3) {
                sizeFactor = 0.3;
                tweak = false;
            }

            if (tries < initialTries * 1 / 2) {
                sizeFactor = 0.6;
            }

//            if (tries < initialTries * 1 / 3) {
//                smaller = lineLength * 4 / 5;
//            }

            Jack jack = new Jack((int) (lineLength * sizeFactor), x, y, tweak);
            boolean add = true;

            for (Shape check : shapes) {
                if (check.overlaps(jack)) {
                    add = false;
                    break;
                }
            }

            if (add) {
                i++;
                jack.finishCreation();
                shapes.add(jack);
            }
        }
        
        ready = true;
        drawPaused = false;
    }


    // TODO: make jacks smaller and sparser (like when bug gave it that look, but less extreme)
    public class Jack extends Shape {
        int x;
        int y;
        int lineLength;

        int[] xs;
        int[] ys;
//        float[] ang;
//        int[] dist;
        int[] rs;
        int numCircles;
        Paint p;
        // TODO: don't store these (or paint even) here
        Paint[] glow;

        public Jack(int lineLength, int x, int y, boolean tweak) {
            if(tweak) {
                int lineLengthChange = (int) (lineLength * 0.5);
                lineLength += Rand.plusOrMinusI(lineLengthChange);
            }

            this.lineLength = lineLength;
            this.x = x;
            this.y = y;

            // TODO: include circle, not a hack...
            bounds = new Rect(x - lineLength - 10, y - lineLength - 10, x + lineLength + 10, y + lineLength + 10);
        }

        public void finishCreation() {
            int colorIndex = Rand.i(forePaints.length);
            p = Utils.createPaint(0,  0, 0); //forePaints[colorIndex];
            glow = glowPaints[colorIndex];

            // TODO: one per pic, not per Jack
            // touchColor = new Paint();
            // touchColor.setAntiAlias(true);
            // touchColor.setARGB(255, 255, 0, 0);

            this.numCircles = Rand.rangeI(9, 11);

            xs = new int[numCircles];
            ys = new int[numCircles];
//            dist = new int[numCircles];
//            ang = new float[numCircles];
            rs = new int[numCircles];

            int ballBaseRadius = lineLength / 10;
            double ballRadiusVary = (int) (ballBaseRadius * 0.8);

            double angleInc = 2 * Math.PI / numCircles;
            double angleVary = angleInc / 6;
            double angle = Math.random() * Math.PI;

            for (int i = 0; i < numCircles; i++) {
                double angleToUse = angle;
                angleToUse += Utils.rand.nextDouble() * angleVary - (angleVary / 2);

//                ang[i] = (float) angleToUse;
                angle += angleInc;

                // TODO: check for efficiency everywhere (moving out calculations, not creating objects ( or
                // even primitives)
                rs[i] = (int) (ballBaseRadius + Utils.rand.nextDouble() * ballRadiusVary - ballRadiusVary / 2);
                
                int dist = Rand.rangeI(lineLength / 2, lineLength);

                xs[i] = x + (int) (Math.cos(angleToUse) * dist);
                ys[i] = y + (int) (Math.sin(angleToUse) * dist);
            }
        }

        @Override
        public boolean overlaps(Shape shape) {
            Jack jack = (Jack) shape;

            int xDiff = jack.x - x;
            int yDiff = jack.y - y;
            float radDiff = jack.lineLength + lineLength + overlapBuffer;

            // radDiff *= 0.8;

            if (xDiff * xDiff + yDiff * yDiff < radDiff * radDiff) {
                return true;
            }

            return false;
        }

        @Override
        public void draw(Canvas canvas) {
            // Paint p2 = Utils.createPaint(255, 255, 0);
            // canvas.drawCircle(x, y, 200, p2);
            for (int i = 0; i < numCircles; i++) {
                if (xs[i] != 0) {
                    for (int j = glow.length - 1; j >= 0; j--) {
                        canvas.drawLine(x + j, y + j, xs[i], ys[i], glow[j]);
                        canvas.drawLine(x - j, y + j, xs[i], ys[i], glow[j]);
                        canvas.drawLine(x + j, y - j, xs[i], ys[i], glow[j]);
                        canvas.drawLine(x - j, y - j, xs[i], ys[i], glow[j]);
                    }

                    for (int j = glow.length - 1; j >= 0; j--) {
                        canvas.drawCircle(xs[i], ys[i], rs[i] + j, glow[j]);
                    }
                }
            }

            for (int i = 0; i < numCircles; i++) {
                if (xs[i] != 0) {
                    canvas.drawLine(x, y, xs[i], ys[i], p);
                    canvas.drawCircle(xs[i], ys[i], rs[i], p);
                }
            }
        }

    }
}
