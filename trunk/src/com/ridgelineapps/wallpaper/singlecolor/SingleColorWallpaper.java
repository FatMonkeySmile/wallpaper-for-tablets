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

package com.ridgelineapps.wallpaper.singlecolor;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.ridgelineapps.wallpaper.Utils;
import com.ridgelineapps.wallpaper.WallpaperBase;

public class SingleColorWallpaper extends WallpaperBase {

    public Paint background;
    public String color;

    @Override
    public boolean doubleTap() {
        return false;
    }
    
    @Override
    public void draw(Canvas canvas) {
        try {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), background);
        }
        finally {
            // Should only have to draw it once...
            drawPaused = true;
        }
    }

    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        
        SharedPreferences prefs = engine.getPrefs();
        color = prefs.getString("single_color", "Black");
        int[] rgb = Utils.getCommonColorRgb(color);

        background = Utils.createPaint(rgb[0], rgb[1], rgb[2]);
        //TODO: anti alias on?
        background.setAntiAlias(false);
    }

    @Override
    public void cleanup() {
        // TODO: clean up resources like paints?
    }
}
