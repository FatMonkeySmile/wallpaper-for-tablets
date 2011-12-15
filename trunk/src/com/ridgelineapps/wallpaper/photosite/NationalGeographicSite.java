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

package com.ridgelineapps.wallpaper.photosite;

import com.ridgelineapps.wallpaper.Utils;

public class NationalGeographicSite implements Site {

    public String getKeyName() {
        return "site_national_geographic";
    }
    
    public String getDisplayName() {
        return "National Geographic Photo of the Day";
    }
    
	public boolean isRandom() {
		return false;
	}
    
    public String loadImage(PhotoSiteWallpaper.ImageLoader imageLoader, boolean random) {
        String img = null;
        
        String str = "http://photography.nationalgeographic.com/photography/photo-of-the-day/";

        String page = Utils.getPage(str);
        System.out.println("page loaded:" + str);

        if (page != null && !imageLoader.kill) {
            String pref = "<div class=\"download_link\"><a href=\"";
            String suffix = "\"";

            img = Utils.findValue(page, pref, suffix);
            System.out.println("findValue:" + img);

            if (img == null && !imageLoader.kill) {
                pref = "div class=\"primary_photo\"";
                int at = Utils.findValueIndexAfterText(page, pref);
                if (at != -1) {
                    img = Utils.findImgAfter(page, at);
                    System.out.println("findImgAfter:" + img);
                }
            }
        }
        return img;
    }
}
