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

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ridgelineapps.wallpaper.WallpaperBase;

// NOTE:
// background should be set in the subclass' constructor since the service draws the background as soon as
// possible to avoid redraws of an out of date canvas when switching between landscape and portrait

public class PatternWallpaper extends WallpaperBase {
    @SuppressWarnings("rawtypes")
    public static final Class[] WALLPAPERS = new Class[] { Targets.class, CirclesInCircles.class, Jacks.class, Lines.class, Dots.class, Rainbows.class,
            ThinCircles.class, Moons.class };

    public Paint background;

    protected ArrayList<Shape> shapes = new ArrayList<Shape>();

    public PatternWallpaper() {
        drawInterval = 500;
        allowsBlackout = true;
    }

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // for (Shape shape : shapes) {
        // if (shape.visible && !shape.drawn && !shape.isBackground) {
        // if (drawnAtLeastOnce) {
        // if (first) {
        // first = false;
        // canvas.clipRect(shape.getBounds());
        // } else {
        // canvas.clipRect(shape.getBounds(), Op.UNION);
        // }
        // }
        // if (!shape.animating) {
        // shape.drawn = true;
        // }
        // }
        // }
        //
        // if (!drawnAtLeastOnce) {
        // drawnAtLeastOnce = true;
        // }

        // TODO: figu;re out why this didn't work
        // if (drawBlackout) {
        // canvas.drawRect(0, 0, width, height, grayoutPaint);
        // drawBlackout = false;
        // drawPaused = true;
        // return;
        // }

        if (!ready || drawPaused) {
            canvas.drawRect(new Rect(0, 0, width, height), getBackground());
            return;
        }

        // if (blackout) {
        // canvas.drawRect(new Rect(0, 0, width, height), engine.background);
        // } else {
        canvas.drawRect(new Rect(0, 0, width, height), getBackground());

        for (Shape shape : shapes) {
            shape.draw(canvas);
        }

        // if (drawBlackout) {
        if (blackout) {
            canvas.drawRect(0, 0, width, height, blackoutPaint);
        }
        // drawBlackout = false;
        // }
        // }

        // if (blackout) {
        // canvas.drawRect(0, 0, width, height, grayoutPaint);
        // }

        // TODO: have "draw once" concept be part of base
        drawPaused = true;
    }

    public Paint getBackground() {
        if (background == null) {
            background = engine.background;
        }

        return background;
    }

    @Override
    public void cleanup() {
        // TODO: clean up resources like paints?
    }
}
