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


public class Rectangle extends Shape {
    Paint p;
    int x;
    int y;
    int width;
    int height;
    
    public Rectangle(Paint p, int x, int y, int width, int height) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        bounds = new Rect(Math.min(x, x + width), Math.min(y, y + height), Math.max(x, x + width), Math.max(y, y + height)); 
    }

    @Override
    public void tweak() {
        int fudge = 3;
        x += Rand.i(fudge) - fudge / 2;
        y += Rand.i(fudge) - fudge / 2;
        width += Rand.i(fudge) - fudge / 2;
        height += Rand.i(fudge) - fudge / 2;
    }
    
    @Override
    public void draw(Canvas canvas) {
        if(!visible) {
            return;
        }

        canvas.drawRect(x, y, x + width, y + height, p);
    }
}
