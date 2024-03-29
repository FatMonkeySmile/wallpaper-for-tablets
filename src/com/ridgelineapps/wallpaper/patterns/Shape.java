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
import android.graphics.Rect;

//NOTE:
//bounds should be set in subclass' constructor

public class Shape {
    public Rect bounds;
    
    boolean visible = true;
//    public boolean drawn = false;
    
    public boolean isBackground = false;

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void draw(Canvas canvas) {
        
    }
    
    public void tweak() {
        
    }
    
    public boolean overlaps(Shape single) {
        return false;
    }
    
    public Rect getBounds() {
        return bounds;
    }
}
