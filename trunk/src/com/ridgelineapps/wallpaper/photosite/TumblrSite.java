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

import com.ridgelineapps.wallpaper.Rand;

public class TumblrSite implements Site {

    // TODO: cleaner...
    public static String acctName = "android";
    
    public static final int MIN_WIDTH = 1280 * 2 / 3;
    public static final int MIN_HEIGHT = 800 * 2 / 3;
    
    public String getKeyName() {
        return "site_tumblr";
    }

    public String getDisplayName() {
        return "Tumblr";
    }
    
	public boolean isRandom() {
		return true;
	}

    public String loadImage(PhotoSiteWallpaper.ImageLoader imageLoader, boolean random) {
        //TODO: fix that multiple double-taps causes multiple image loads at the same time and that double tap doesn't always blank out image immediately
        String img = null;

        try {

            if (acctName != null && !acctName.equals("")) {
                TumblrUtils.PhotoList list = TumblrUtils.get().getPhotos(acctName, 50, 0);

                //TODO: cleaner...
                if(list.mTotal > 50) {
                    int page = Rand.i(list.mTotal / 50); 
                    list = TumblrUtils.get().getPhotos(acctName, 50, page * 50);
                }
                
                if (list != null && list.getCount() > 0) {
                    TumblrUtils.Photo photo = list.get(Rand.i(list.getCount()));
                    System.out.println("photo width:" + photo.mWidth + ", height:" + photo.mHeight);
                    if (photo != null) {
                        // TODO: put owner and title on photo for a sec?
                        img = photo.getUrl();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return img;
    }
}
