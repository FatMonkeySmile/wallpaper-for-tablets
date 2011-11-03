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
 *
 * Portions copyright http://code.google.com/p/dodge-android/
 */

package com.ridgelineapps.wallpaper.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ridgelineapps.wallpaper.R;
import com.ridgelineapps.wallpaper.Utils;

public class SelectPortraitImagePreference extends Preference {
    ImageView imageView;

    public SelectPortraitImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        imageView = (ImageView) view.findViewById(R.id.prefs_image_view);
        updateBackgroundImage();
    }

    //TODO: don't do any of this when image screensaver is not the screensaver...
    void updateBackgroundImage() {
        if (imageView != null) {
            Bitmap bitmap = null;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String imageURIString = prefs.getString("full_image_uri_portrait", null);
            if (imageURIString != null) {
                Uri imageURI = Uri.parse(imageURIString);
                try {
                    bitmap = Utils.loadBitmap(getContext(), imageURI, Math.max(128, imageView.getWidth()), Math.max(128, imageView.getHeight()), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            imageView.setImageBitmap(bitmap);
        }
    }
}
