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

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.ridgelineapps.wallpaper.R;

public class Prefs extends PreferenceActivity {
    public static final String NOT_SET = "[not set]";
    public static final String selectedTextStr = "Current wallpaper: " + NOT_SET;
    
//    public static TextView selectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (hasHeaders()) {
//            selectedText = new TextView(this);
//            selectedText.setGravity(Gravity.CENTER);
//            selectedText.setMinLines(5);
//            // selectedText.setBackgroundColor(Color.argb(255, 120, 120, 120));
//            setListFooter(selectedText);
//        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
    }
}