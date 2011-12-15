/*
 * Copyright (C) 2011 Wallpaper for Tablets (http://code.google.com/p/wallpaper-for-tablets)
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.ridgelineapps.wallpaper.Utils;
import com.ridgelineapps.wallpaper.WallpaperBase;

public class PhotoSiteWallpaper extends WallpaperBase {

    // static final String fileUri;
    Bitmap image; 
    // boolean fill;
    ImageLoader loader;
    boolean clearedImage = false;
    Site site;
    Paint bitmapPaint;
    boolean fill;
    public static final Site[] sites = new Site[] { new NationalGeographicSite(), new NationalGeographicWallSite(), /* new FiveHundredPxSite(), */ new FlickrSite(), new FlickrUserSite(), new TumblrSite() };

    public static final String CACHE_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WallpaperForTablets/cached_image.png";

    // boolean imageDrawn;

    // boolean random;

    long lastChange = -1;
    long lastAttempt = -1;
    public static final long ONE_MIN = 1000L * 60; // one minute in milliseconds constant
    long refreshInterval = 1440 * ONE_MIN; // default is one day
    long retryInterval = 60 * ONE_MIN; // default is one hour

    boolean cacheImage = true;
    boolean cacheChecked;

    public PhotoSiteWallpaper() {
        allowsBlackout = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (image == null) { //!clearedImage) {
            canvas.drawRect(0, 0, width, height, engine.background);
        }

        Bitmap bmp = image;
        if (bmp != null) {
            System.out.println("image:" + image);
            try {
                // canvas.drawBitmap(bmp, 0, 0, engine.background);
                float scaleWidth = (float) width / bmp.getWidth();
                float scaleHeight = (float) height / bmp.getHeight();

                float scale;

                if (fill) {
                    scale = Math.max(scaleWidth, scaleHeight);
                } else {
                    scale = Math.min(scaleWidth, scaleHeight);
                }
                
                int destWidth = (int) (bmp.getWidth() * scale);
                int destHeight = (int) (bmp.getHeight() * scale);

                int x = 0;
                int y = 0;

                // if (!fill) {
                x = (width - destWidth) / 2;
                y = (height - destHeight) / 2;
                // }

                Rect dest = new Rect(x, y, x + destWidth, y + destHeight); 

                canvas.drawRect(0, 0, width, height, engine.background);
                canvas.drawBitmap(bmp, null, dest, bitmapPaint);
                if (blackout) {
                    canvas.drawRect(0, 0, width, height, blackoutPaint);
                }
            } finally {
                drawPaused = true; // imageDrawn = true;
            }
        }
    }

    @Override
    public boolean preDraw() {
        if (image == null && clearedImage && loader.done) {
            drawPaused = true; // return false;
        }

        if (image == null && !cacheChecked) {
            loadImageFromCache();
        }

        if (loader == null || loader.done) {
            boolean refresh = false;
            if (image == null) {
                if (System.currentTimeMillis() - lastAttempt > retryInterval) {
                    refresh = true;
                }
            }
            
            long mostRecentChangeOrAttempt = Math.max(lastAttempt, lastChange);
            if (!refresh && System.currentTimeMillis() - mostRecentChangeOrAttempt > refreshInterval) {
                refresh = true;
            }

            if (refresh) {
                loadImage(false);
            }
        }

        // if (image != null && imageDrawn) {
        // return false;
        // }

        return super.preDraw();
    }
    
    @Override
    public void prefsChanged() {
        //TODO: not sure if this works in all cases since this is called only on the active wp when its changed
        removeImageFromCache();
    }

    @Override
    public boolean doubleTap() {
    	if(site.isRandom()) {
	        clearImage();
	        loadImage(false);
    	}
        return false;
    }
    
    public void clearImage() {
        removeImageFromCache();
        image = null;
        clearedImage = false;
        drawPaused = false;
    }
    
    // TODO: Check that we are not painting more than once or listeneing to any
    // events...
    @Override
    public void init(int width, int height, int longSide, int shortSide, boolean reload) {
        super.init(width, height, longSide, shortSide, reload);
        clearedImage = false;
        
        bitmapPaint = new Paint();
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);
        
        if(reload) {
            image = null;
        }
        drawPaused = false;

        drawInterval = 250;
        if (reload) {
            SharedPreferences prefs = engine.getPrefs();
            // TODO: figure out int prefs instead of strings...
            try {
                fill = prefs.getBoolean("photosite_fill_screen", true);
                
                // Removed fill preference until we know for sure it generally looks good for the online images...
                fill = false;

                String rate = prefs.getString("photosite_refresh_interval", "1440");
                refreshInterval = Integer.parseInt(rate);
                refreshInterval *= ONE_MIN;
                
                cacheImage = prefs.getBoolean("photosite_cache", true);
                
//                removeImageFromCache();

                // refreshInterval = 10 * 1000L;

                // random = prefs.getBoolean("photosite_random", false);

                Map<String, ?> prefsMap = prefs.getAll();
                for (String prefKey : prefsMap.keySet()) {
                    for (Site siteCheck : sites) {
                        if (prefKey.equals(siteCheck.getKeyName())) {
                            if (prefs.getBoolean(prefKey, false)) {
                                site = siteCheck;
                                break;
                            }
                        }
                    }
                }

                FlickrUserSite.userName = prefs.getString("flickr_user_value", "");
                TumblrSite.acctName = prefs.getString("tumblr_name", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // fill = prefs.getBoolean("fill", true);

            // fileUri = prefs.getString("full_image_uri", "");
            //
            // if (!fileUri.trim().equals("")) {
            // try {
            // // image =
            // Utils.scaledBitmapFromURIWithMinimumSize(engine.getBaseContext(),
            // // Uri.parse(fileUri), width, height, false);
            // image = Utils.loadBitmap(engine.getBaseContext(),
            // Uri.parse(fileUri), width, height, false);
            //
            // } catch (Exception e) {
            // // TODO: don't try to load again...
            // Log.e("ImageFileWallpaper", "0", e);
            // }
            // }
        }

        if (reload) {
            loadImage(true);
        }
    }

    // TODO: single source all of these pref keys
    public long getLastChanged() {
        if (lastChange != -1) {
            return lastChange;
        }

        lastChange = engine.getPrefs().getLong("photosite_last_successful_refresh", 0);
        return lastChange;
    }

    public long getLastAttempt() {
        if (lastAttempt != -1) {
            return lastAttempt;
        }

        lastAttempt = engine.getPrefs().getLong("photosite_last_refresh_attempt", 0);
        return lastAttempt;
    }

    public void updateLastChanged() {
        lastChange = System.currentTimeMillis();
        engine.getPrefs().edit().putLong("photosite_last_successful_refresh", lastChange).commit();
    }

    public void updateLastAttempt() {
        lastAttempt = System.currentTimeMillis();
        engine.getPrefs().edit().putLong("photosite_last_refresh_attempt", lastAttempt).commit();
    }

    public void loadImage(boolean clearOld) {
        if (site == null) {
            clearedImage = false;
            image = null;
            return;
        }

        synchronized (this) {

            updateLastAttempt();

            if (clearOld) {
                image = null;
                clearedImage = false;
                drawPaused = false;
            }

            if (loader != null) {
                loader.kill = true;
                loader = null;
            }

            loader = new ImageLoader(site);

            // for dev instead of threading...
            // ThreadPolicy tp = ThreadPolicy.LAX;
            // StrictMode.setThreadPolicy(tp);
            // loader.run();

            Thread thread = new Thread(loader);
            thread.start();
        }
    }

    public void loadImageFromCache() {
        if (!cacheImage) {
            return;
        }

        synchronized (this) {

            cacheChecked = true;

            try {
                File file = new File(CACHE_FILE_NAME);
                if(file.exists() && image == null) {
                    image = BitmapFactory.decodeFile(CACHE_FILE_NAME);
                }
            } catch (Exception e) {
                Log.e("PhotoSiteWallpaper", "0", e);
            }
        }
    }

    public void removeImageFromCache() {
        synchronized (this) {
            new File(CACHE_FILE_NAME).delete();
        }
    }

    public void saveImageToCache() {
        if (!cacheImage) {
            return;
        }

        synchronized (this) {

            if (image != null) {
                try {
                    File file = new File(CACHE_FILE_NAME);
                    File parent = file.getParentFile();
                    parent.mkdirs();
                    FileOutputStream out = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    Log.e("PhotoSiteWallpaper", "0", e);
                }
            }
        }
    }

    @Override
    public void cleanup() {
        try {
            if (loader != null) {
                loader.kill = true;
                loader = null;
            }

            if (image != null) {
                image.recycle();
            }
            // TODO: clean up resources like paints?
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ImageLoader implements Runnable {
        boolean kill;
        boolean done;
        Site site;

        public ImageLoader(Site site) {
            this.site = site;
        }

        public void run() {
            try {

                // TODO: put light loading icon instead of blanking out
                String img = site.loadImage(this, false);
                if (img != null && !kill) {
                    System.out.println("loading img:" + img);
                    // TODO: image looks bad, check how we scale
                    Bitmap bmp = Utils.downloadBitmap(img);
                    if (bmp != null && !kill) {
                        image = bmp;
                        drawPaused = false;
                        saveImageToCache();
                        // imageDrawn = false;
                    }
                }
            } finally {
                // TODO: should this be used to make the wp more effecient? Have it 
                // not get pinged, but be able to poke service to start up draw check thread?
                done = true;

                if (image != null) {
                    updateLastChanged();
                }
            }
        }
    }
}
