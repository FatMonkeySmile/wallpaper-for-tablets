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

package com.ridgelineapps.wallpaper.preferences;

import com.ridgelineapps.wallpaper.R;


public class PrefsPatterns extends PrefsFragmentBase {
    
    @Override
    public String getWallpaperName() {
        return "Patterns";
    }

    @Override
    public String getSetWallpaperKey() {
        return "set_wallpaper_patterns";
    }

    @Override
    public int getResourceId() {
        return R.xml.preferences_wp_patterns;
    }

    @Override
    public String[] getKeysToUpdate() {
        return new String[]{ };
    }
    
}