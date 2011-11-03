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

package com.ridgelineapps.wallpaper.preferences;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;

import com.ridgelineapps.wallpaper.R;
import com.ridgelineapps.wallpaper.photosite.PhotoSiteWallpaper;
import com.ridgelineapps.wallpaper.photosite.Site;

public class PrefsPhotoSite extends PrefsFragmentBase {
    ArrayList<CheckBoxPreference> sitePrefs = new ArrayList<CheckBoxPreference>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (Site site : PhotoSiteWallpaper.sites) {
            Preference pref = getPreferenceScreen().findPreference(site.getKeyName()); // getPreferenceScreen().findPreference(prefKey);
            boolean foundOneChecked = false;

            if (pref != null) {
                CheckBoxPreference cb = (CheckBoxPreference) pref;
                sitePrefs.add(cb);
                if (cb.isChecked()) {
                    if (!foundOneChecked) {
                        foundOneChecked = true;
                    } else {
                        cb.setChecked(false);
                    }
                }
            }
        }

        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences shared, String key) {
        super.onSharedPreferenceChanged(shared, key);

        boolean siteOn = false;
        boolean allSitesOff = true;

        // TODO: do more effeciently somehow?
        for (CheckBoxPreference pref : sitePrefs) {
            if(pref.isChecked()) {
                allSitesOff = false;
            }
            if (pref.getKey().equals(key) && pref.isChecked()) {
                siteOn = true;
            }
        }
        
        if(allSitesOff) {
            for (CheckBoxPreference pref : sitePrefs) {
                if (pref.getKey().equals(key)) {
                     pref.setChecked(true);
                }
            }
        } else if(siteOn) {
            for (CheckBoxPreference pref : sitePrefs) {
                if (pref.getKey().equals(key)) {
                     pref.setChecked(true);
                } else {
                    if (pref.isChecked()) {
                        pref.setChecked(false);
                    }
                }
            }
        }
    }


    @Override
    public String getWallpaperName() {
        return "PhotoSite";
    }

    @Override
    public String getSetWallpaperKey() {
        return "set_wallpaper_photosite";
    }

    @Override
    public int getResourceId() {
        return R.xml.preferences_wp_photosite;
    }

    @Override
    public String[] getKeysToUpdate() {
        return new String[] { "photosite_refresh_interval", "flickr_user_value", "tumblr_name" };
    }

}
