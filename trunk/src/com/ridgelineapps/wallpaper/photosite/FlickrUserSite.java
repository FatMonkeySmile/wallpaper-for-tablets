/*
 * Copyright (C) 2011 Wallpaper for Tablets Open Source Project
 * 
 * This file is part of Wallpaper for Tablets
 * 
 * Wallpaper for Tablets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Wallpaper for Tablets. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.ridgelineapps.wallpaper.photosite;

import com.ridgelineapps.wallpaper.Rand;

public class FlickrUserSite implements Site {

    // TODO: cleaner...
    public static String userName;
    
    public String getKeyName() {
        return "site_flickr_user";
    }

    public String getDisplayName() {
        return "Flickr";
    }
    
	public boolean isRandom() {
		return true;
	}

    public String loadImage(PhotoSiteWallpaper.ImageLoader imageLoader, boolean random) {
        String img = null;

        try {
            
            if(userName != null && !userName.equals("")) {
                FlickrUtils.User user = FlickrUtils.get().findByUserName(userName);
                if(user != null) {
                    FlickrUtils.PhotoList list = FlickrUtils.get().getPublicPhotos(user, 500, 1);
                    if (list != null && list.getCount() > 0) {
                        FlickrUtils.Photo photo = list.get(Rand.i(list.getCount()));
                        if (photo != null) {
                            // TODO: put owner and title on photo for a sec?
                            img = photo.getUrl(FlickrUtils.PhotoSize.LARGE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: use Flickr api to load Bitmap?
        return img;
    }
}
