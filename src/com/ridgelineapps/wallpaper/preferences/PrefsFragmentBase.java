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

package com.ridgelineapps.wallpaper.preferences;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

//TODO make so unchecking set as wallpaper sets the wallpaper to [not-set] and it doesn't draw anything? (or don't allow unchecking somehow)
public abstract class PrefsFragmentBase extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    //TODO: make non-static 
//    public static HashMap<String, Preference> setWallpaperPrefs = new HashMap<String, Preference>();
    
    private ArrayList<Preference> prefs = new ArrayList<Preference>();
    private HashMap<String, String> origSummaries = new HashMap<String, String>();
//    CheckBoxPreference thisSetWallpaperPref;

    public String[] getKeysToUpdate() {
        return null;
    }
    
    public abstract int getResourceId();
    
    public String getWallpaperName() {
        return null;
    }

    public String getSetWallpaperKey() {
        return null;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(getResourceId());

        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences(); // PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        
//        String setWallpaperKey = getSetWallpaperKey();
//        if(setWallpaperKey != null) {
//            Preference pref = getPreferenceScreen().findPreference(setWallpaperKey); // getPreferenceScreen().findPreference(prefKey);
//            
//            if (pref != null) {
//                thisSetWallpaperPref = (CheckBoxPreference) pref;
//                setWallpaperPrefs.put(setWallpaperKey, pref);
//                String currentWallpaper = sharedPrefs.getString("wp_type", "");
//                if(currentWallpaper.equals(getWallpaperName())) {
//                    ((CheckBoxPreference) pref).setChecked(true);
//                }
//            }
//        }
        
        String[] keysToUpdate = getKeysToUpdate();
        if(keysToUpdate != null) {
            for (String prefKey : keysToUpdate) {
                Preference pref = getPreferenceScreen().findPreference(prefKey); // getPreferenceScreen().findPreference(prefKey);
    
                if (pref != null) {
                    prefs.add(pref);
                    String summary;
                    if (pref.getSummary() == null) {
                        summary = "";
                    } else {
                        summary = pref.getSummary().toString();
                    }
                    origSummaries.put(pref.getKey(), summary);
                    onSharedPreferenceChanged(sharedPrefs, prefKey);
                }
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences shared, String key) {
//        if(thisSetWallpaperPref != null) {
//            if(key.equals("wp_type")) {
//                String value = shared.getString(key, "");
//                if(!value.equals("")) {
//                    if(value.equals(getWallpaperName()) && !thisSetWallpaperPref.isChecked()) {
//                        thisSetWallpaperPref.setChecked(true);
//                    }
//                }
//                
//                for (Preference pref : setWallpaperPrefs.values()) {
//                    if(!pref.getKey().equals(key)) {
//                        CheckBoxPreference cb = (CheckBoxPreference) pref;
//                        cb.setChecked(false);
//                    }
//                }
//            }
//
//            if(key.equals(thisSetWallpaperPref.getKey())) {
//                String currentWallpaper = shared.getString("wp_type", "");
//                if(thisSetWallpaperPref.isChecked()) {
//                    if(!currentWallpaper.equals(getWallpaperName())) {
//                        shared.edit().putString("wp_type", getWallpaperName()).commit();
//                    }
//                }
//                else {
//                    if(currentWallpaper.equals(getWallpaperName())) {
//                        thisSetWallpaperPref.setChecked(true);
//                    }
//                }
//            }
//        }
            
        for (Preference pref : prefs) {
            if (pref.getKey().equals(key)) {
                    String summary = origSummaries.get(key);
                    if (summary != null) {
                        String value = shared.getString(key, Prefs.NOT_SET);
                        if(pref instanceof ListPreference) {
                            ListPreference listPref = (ListPreference) pref;
                            if(listPref != null && listPref.getEntry() != null) {
                                value = listPref.getEntry().toString();
                            }
                        }
                        String newSummary = summary.replace(Prefs.NOT_SET, value);
                        if(key.endsWith("uri")) {
                            newSummary = newSummary.replace(PrefsImageFile.FILE_URI_PREFIX, "");
                        }
                        pref.setSummary(newSummary);
    //                    if (key.equals("wp_type")) {
    //                        Prefs.selectedText.setText(Prefs.selectedTextStr.replace(Prefs.NOT_SET, entry));
    //                    }
                    }
//                }
            }
        }
    }
}
