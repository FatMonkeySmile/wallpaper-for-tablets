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

package com.ridgelineapps.wallpaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.ridgelineapps.wallpaper.preferences.Prefs;

public class WallpaperForTabletsActivity extends Activity {
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      if (Build.VERSION.SDK_INT > 15) {
         WallpaperManager wm = WallpaperManager.getInstance(this);
         WallpaperInfo wi = wm.getWallpaperInfo();
         if(wi != null && !wi.getPackageName().equals("com.ridgelineapps.wallpaper")) {
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
            myAlertDialog.setTitle("Wallpaper for Tablets");
            myAlertDialog.setMessage("Would you like to set Wallpaper for Tablets as the current wallpaper?");
            myAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface di, int x) {
                  Intent i = new Intent();
                  i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                  String p = DelegatingWallpaperService.class.getPackage().getName();
                  String c = DelegatingWallpaperService.class.getCanonicalName();
                  i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(p, c));
                  startActivity(i);
               }
            });
            myAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface di, int x) {
               }
            });
            myAlertDialog.show();
         }

         Intent i = new Intent(this, Prefs.class);
         startActivity(i);
      } else {
         Toast toast = Toast.makeText(this, "Choose \"Wallpaper for Tablets\" from the list to start the Live Wallpaper.", Toast.LENGTH_LONG);
         toast.show();
         Intent i = new Intent();
         i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
         startActivityForResult(i, 0);
      }
   }

}
