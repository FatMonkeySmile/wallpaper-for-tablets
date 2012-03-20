/*
/*
 * Copyright (C) 2012 Wallpaper for Tablets (http://code.google.com/p/wallpaper-for-tablets)
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

package com.ridgelineapps.wallpaper.photosite;


public class FiveHundredPxSite implements Site {
   
    public String getKeyName() {
        return "site_500px";
    }
    
    public String getDisplayName() {
        return "500px";
    }
    
	public boolean isRandom() {
		return true;
	}
    
    public String loadImage(PhotoSiteWallpaper.ImageLoader imageLoader, boolean random) {
       return FiveHundredPxUtils.sInstance.getRandomPhoto();
    }
}
