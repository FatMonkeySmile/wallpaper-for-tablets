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

package com.ridgelineapps.wallpaper.photosite;

import android.graphics.Bitmap;

import com.ridgelineapps.wallpaper.Rand;
import com.ridgelineapps.wallpaper.Utils;

public class NationalGeographicWallSite implements Site {
    static final String[] MONTHS = new String[]{ "", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

    public String getKeyName() {
        return "site_national_geographic_wall";
    }
    
    public String getDisplayName() {
        return "National Geographic Wallpapers";
    }
    
	public boolean isRandom() {
		return true;
	}
    
    public String loadImage(PhotoSiteWallpaper.ImageLoader imageLoader, boolean random) {
        String year = "11";
        
        int rand = Rand.i(3);
        if(rand == 0) {
            year = "09";
        }
        else if(rand == 1) {
            year = "10";
        }
        else if(rand == 2) {
            year = "11";
        }
        String baseUrl = "http://ngm.nationalgeographic.com/wallpaper/img/20" + year + "/"; 
        
        int attempts = 0;
        boolean success = false;
        String imageUrl = null;
        
        //TODO: return downloaded image rather than downloading and throwing away...
        while(!success)
        {
            try {
                //TODO: Don't hardcode 25
                int imgCount = Rand.rangeI(1, 25);
                int month = Rand.rangeI(1, 12);
                String monthStr = padNumberString("" + month, 2);
                
                if(++attempts > 15) {
                    break;
                }
                imageUrl = baseUrl + monthStr + "/" + MONTHS[month] + year + "wallpaper-" + imgCount + "_1600.jpg";
                
                System.out.println("requesting:" + imageUrl);
                Bitmap bmp = Utils.downloadBitmap(imageUrl);
                if(bmp != null) {
                    bmp.recycle();
                    success = true;
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        if(!success) {
            return null;
        }
        
        return imageUrl;
    }
    
    public static String padNumberString(String str, int len)
    {
        while(str.length() < len)
        {
            str = "0" + str;
        }
        
        return str;
    }
}
