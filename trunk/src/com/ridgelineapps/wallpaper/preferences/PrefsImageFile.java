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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import com.ridgelineapps.wallpaper.R;

public class PrefsImageFile extends PrefsFragmentBase {

    static final int SELECT_IMAGE = 1;
    static final int SELECT_PORTRAIT_IMAGE = 2;
    static final String FILE_URI_PREFIX = "file:///";
    
    private String selectedImagePath;
    private String filemanagerstring;
    
    //TODO: reset button -- changes images to [not set]
//    Preference selectImagePref;
//    Preference selectPortraitImagePref;
    SelectImagePreference selectImagePref;
    SelectPortraitImagePreference selectPortraitImagePref;

    @Override
    public String getWallpaperName() {
        return "ImageFile";
    }

    @Override
    public String getSetWallpaperKey() {
        return "set_wallpaper_image";
    }

    @Override
    public int getResourceId() {
        return R.xml.preferences_wp_image;
    }

    @Override
    public String[] getKeysToUpdate() {
       return new String[]{ "full_image_uri", "portrait_full_image_uri" };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: is it the wrong type of preference?  preference screen?
//        selectImagePref = findPreference("full_image_uri");
//        selectImagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                portrait = false;
//                selectBackgroundImage();
//                return true;
//            }
//        });
//        
//        selectPortraitImagePref = findPreference("portrait_full_image_uri");
//        selectPortraitImagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                portrait = false;
//                //TODO: rename all backgrounds to (?)
//                selectPortraitBackgroundImage();
//                return true;
//            }
//        });
        
        selectImagePref = (SelectImagePreference) findPreference("image_file");
        selectImagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                selectBackgroundImage();
                return true;
            }
        });
        
        selectPortraitImagePref = (SelectPortraitImagePreference) findPreference("image_file_portrait");
        selectPortraitImagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                selectPortraitBackgroundImage();
                return true;
            }
        });
    }

    void selectBackgroundImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
        // Intent i = new Intent(
        // Intent.ACTION_PICK,
        // android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    void selectPortraitBackgroundImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_PORTRAIT_IMAGE);
        // Intent i = new Intent(
        // Intent.ACTION_PICK,
        // android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences shared, String key) {
    	super.onSharedPreferenceChanged(shared, key);
    	if(key.equals("portrait_image_set")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("portrait_full_image_uri", "");
            editor.commit();
    	}
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE || requestCode == SELECT_PORTRAIT_IMAGE) {
                Uri selectedImageUri = data.getData();

                filemanagerstring = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);

                String finalUri = (selectedImagePath != null) ? selectedImagePath : filemanagerstring;
                finalUri = FILE_URI_PREFIX + finalUri;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                SharedPreferences.Editor editor = prefs.edit();
                String key;
                if(requestCode == SELECT_IMAGE) {
                    key = "full_image_uri";
                }
                else {
                    key = "portrait_full_image_uri";
                }
                editor.putString(key, finalUri);
                editor.commit();
                // CheckBoxPreference useImagePref = (CheckBoxPreference)
                // findPreference("useBackgroundImage");
                // useImagePref.setChecked(true);
                if(requestCode == SELECT_PORTRAIT_IMAGE) {
                    selectPortraitImagePref.updateBackgroundImage(null);
                } else {
                    selectImagePref.updateBackgroundImage(null);
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
