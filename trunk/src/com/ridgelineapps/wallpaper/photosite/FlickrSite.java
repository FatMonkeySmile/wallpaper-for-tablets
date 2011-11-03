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

import com.ridgelineapps.wallpaper.Rand;

public class FlickrSite implements Site {

    public String getKeyName() {
        return "site_flickr";
    }
    
    public String getDisplayName() {
        return "Flickr";
    }
    
    public String loadImage(PhotoSiteWallpaper.ImageLoader imageLoader, boolean random) {
        String img = null;
        
        try {
            FlickrUtils.PhotoList list = FlickrUtils.get().interestingness(500, 1);
            if(list != null && list.mTotal > 0) {
                FlickrUtils.Photo photo = list.get(Rand.i(list.mTotal));
                if(photo != null) {
                    //TODO: put owner and title on photo for a sec?
                    img = photo.getUrl(FlickrUtils.PhotoSize.LARGE);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        //TODO: user Flickr api to load Bitmap?
        return img;
    }
}
