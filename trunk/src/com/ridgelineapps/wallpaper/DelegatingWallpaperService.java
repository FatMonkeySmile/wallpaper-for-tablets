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

import java.util.ArrayList;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.ridgelineapps.wallpaper.imagefile.ImageFileWallpaper;
import com.ridgelineapps.wallpaper.patterns.CirclesInCircles;
import com.ridgelineapps.wallpaper.patterns.Dots;
import com.ridgelineapps.wallpaper.patterns.Jacks;
import com.ridgelineapps.wallpaper.patterns.Lines;
import com.ridgelineapps.wallpaper.patterns.Moons;
import com.ridgelineapps.wallpaper.patterns.NoPattern;
import com.ridgelineapps.wallpaper.patterns.Rainbows;
import com.ridgelineapps.wallpaper.patterns.Targets;
import com.ridgelineapps.wallpaper.patterns.ThinCircles;
import com.ridgelineapps.wallpaper.photosite.PhotoSiteWallpaper;
import com.ridgelineapps.wallpaper.singlecolor.SingleColorWallpaper;

public class DelegatingWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new SimpleWallpaperEngine();
    }

    public class SimpleWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };

        static final int DOUBLE_TAP_THRESH = 600;
        
        public long lastTap;

        boolean doubleTapRefresh = true;

        WallpaperBase wallpaper;
        WallpaperBase oldWallpaper;

        int longSide;
        int shortSide;

        int width;
        int height;

        private boolean visible = true;
        private boolean touchEnabled = true;

        boolean firstDraw = true;

        public Paint background;

        GoogleAnalyticsTracker tracker;
        

        public SimpleWallpaperEngine() {
            background = Utils.createPaint(0, 0, 0);
            // TODO: anti-alias?

            SharedPreferences prefs = getPrefs();
            prefs.registerOnSharedPreferenceChangeListener(this);
            
            try {
                tracker = GoogleAnalyticsTracker.getInstance();
                tracker.startNewSession("UA-28005805-1", 60 * 60, getBaseContext()); // Dispatch interval is once an hour
            }
            catch(Exception e) {
            	e.printStackTrace();
            }
        }

        public Context getBaseContext() {
            return DelegatingWallpaperService.this.getBaseContext();
        }
        
        public void track(String s) {
            if(tracker != null) {
            	try {
            		if(!s.startsWith("/")) {
            			s = "/" + s;
            		}
            		tracker.trackPageView(s);
            	} catch(Exception e) {
            		e.printStackTrace();
            	}
            }
        }

        public synchronized void onSharedPreferenceChanged(SharedPreferences shared, String key) {
            //TODO: causes wallpaper to be re-inited() as preferences are tweaked in code, do another way that doesn't require ignore hack here...
            if(key.equals("photosite_last_refresh_attempt") ||
                    key.equals("photosite_last_successful_refresh")) {
                return;
            }
            
            SharedPreferences prefs = getPrefs();
            doubleTapRefresh = prefs.getBoolean("double_tap_refresh", true);

            if(wallpaper != null) {
                wallpaper.prefsChanged();
            }
            
            // TODO: better way to do, since this probably causes all of the start logic in the first draw(?)
            cleanupWallpaper();
            // refreshWallpaper(true);
        }

        public SharedPreferences getPrefs() {
            return PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {

            if(wallpaper != null) {
                if (doubleTapRefresh && action.equals(WallpaperManager.COMMAND_TAP)) {
                    long now = System.currentTimeMillis();
                    if(now - lastTap < DOUBLE_TAP_THRESH) {
                        if(wallpaper.doubleTap()) {
                            refreshWallpaper(true);
                        }
                        lastTap = 0;
                    } else {
                        lastTap = now;
                    }
                }
            }

            return super.onCommand(action, x, y, z, extras, resultRequested);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        public void cleanupWallpaper() {
            if (wallpaper != null) {
                try {
                    oldWallpaper = wallpaper;
                    wallpaper = null;
                    oldWallpaper.cleanup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @SuppressWarnings("rawtypes")
        public synchronized void refreshWallpaper(boolean reload) {
            // TODO: how often to get these? is there way to only set them when changed easily?
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String wallpaperType = prefs.getString("wp_type", "SingleColor");
            
            if(wallpaperType.equals("Bugs")) {
                wallpaperType = "SingleColor";
            }
            
            track(wallpaperType);

            if (!reload && wallpaper != null) {
                if(wallpaperType.equals("ImageFile") || wallpaperType.equals("PhotoSite")) {
                    wallpaper.init(width, height, longSide, shortSide, false);
                    wallpaper.drawPaused = false;
                    drawAsap();
                    return;
                }
            }
            
            cleanupWallpaper();

            if (visible) {
                Class c = null;

                if (wallpaperType.equals("Patterns")) {
                    // TODO: put this logic down in the patterns wallpaper

                    boolean targets = prefs.getBoolean("patterns_targets", false);
                    boolean circlesInCircles = prefs.getBoolean("patterns_circlesincircles", false);
                    boolean jacks = prefs.getBoolean("patterns_jacks", false);
                    boolean rainbows = prefs.getBoolean("patterns_rainbows", false);
                    boolean dots = prefs.getBoolean("patterns_dots", false);
                    boolean lines = prefs.getBoolean("patterns_lines", false);
                    boolean thinCircles = prefs.getBoolean("patterns_thincircles", false);
                    boolean moons = prefs.getBoolean("patterns_moons", false);

                    ArrayList<Class> choices = new ArrayList<Class>();
                    if (targets) {
                        choices.add(Targets.class);
                        track("Patterns/targets");
                    }
                    if (jacks) {
                        choices.add(Jacks.class);
                        track("Patterns/jacks");
                    }
                    if (circlesInCircles) {
                        choices.add(CirclesInCircles.class);
                        track("Patterns/circlesInCircles");
                    }
                    if (rainbows) {
                        choices.add(Rainbows.class);
                        track("Patterns/rainbows");
                    }
                    if (dots) {
                        choices.add(Dots.class);
                        track("Patterns/dots");
                    }
                    if (lines) {
                        choices.add(Lines.class);
                        track("Patterns/lines");
                    }
                    if (thinCircles) {
                        choices.add(ThinCircles.class);
                        track("Patterns/thinCircles");
                    }
                    if (moons) {
                        choices.add(Moons.class);
                        track("Patterns/moons");
                    }

                    if (oldWallpaper != null && reload && choices.size() > 1) {
                        choices.remove(oldWallpaper.getClass());
                    }

                    if (oldWallpaper != null && !reload && choices.contains(oldWallpaper.getClass())) {
                        c = oldWallpaper.getClass();
                    } else if (choices.isEmpty()) {
                        c = NoPattern.class;
                    } else {
                        c = choices.get(Rand.i(choices.size()));
                    }
                } else if (wallpaperType.equals("ImageFile")) {
                    c = ImageFileWallpaper.class;
                } else if (wallpaperType.equals("PhotoSite")) {
                    c = PhotoSiteWallpaper.class;
                } else {
                    c = SingleColorWallpaper.class;
                }
                try {
                    wallpaper = (WallpaperBase) c.newInstance();
                    wallpaper.engine = this;
                    System.out.println("calling init for:" + wallpaper);
                    wallpaper.init(width, height, longSide, shortSide, true);

                    // TODO: only randomize if needed
                    wallpaper.randomize();

                    try
                    {
                        track(wallpaperType + "/darken_" + wallpaper.blackout);
                        track(wallpaperType + "/doubleTap_" + doubleTapRefresh);
                    	
	                    if (wallpaper instanceof PhotoSiteWallpaper) {
	                    	PhotoSiteWallpaper w = (PhotoSiteWallpaper) wallpaper;
	                    	if(w.site != null) {
	                    		track("PhotoSite/" + w.site.getKeyName());
		                   		track("PhotoSite/rate_" + w.refreshInterval);
		                   		track("PhotoSite/cache_" + w.cacheImage);
	                    	}
	                    } else if(wallpaper instanceof ImageFileWallpaper) {
	                    	ImageFileWallpaper w = (ImageFileWallpaper) wallpaper;
	                   		track("ImageFile/portrait_" + w.portraitDifferent);
	                   		track("ImageFile/rotate_" + w.rotate);
	                   		track("ImageFile/fill_" + w.fill);
	                    } else if(wallpaper instanceof SingleColorWallpaper) {
	                    	SingleColorWallpaper w = (SingleColorWallpaper) wallpaper;
	                   		track("SingleColor/" + w.color);
	                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: what to do?
                }

                drawAsap();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            if (width > 0 && height > 0 && width != this.width || height != this.height) {
                this.width = width;
                this.height = height;
                longSide = Math.max(width, height);
                shortSide = Math.min(width, height);

                firstDraw = true;
                // TODO: don't refresh, just update
                refreshWallpaper(false);
            }
        }

        public void drawAsap() {
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.post(drawRunner);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            if (touchEnabled) {
                if (wallpaper != null) {
                    wallpaper.touched(event);
                }
            }
        }

        private void draw() {
            // if (wallpaper == null && width != 0 && height != 0) {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                // if (firstDraw) {
                // firstDraw = false;
                // try {
                // canvas = holder.lockCanvas();
                // if (canvas != null) {
                // canvas.drawRect(0, 0, width, height, background);
                // }
                // } finally {
                // try {
                // if (canvas != null) {
                // holder.unlockCanvasAndPost(canvas);
                // }
                // } finally {
                // canvas = null;
                // }
                // }
                // }

                // TODO: check for "isReady" since we possibly could call the wallpaper before it is inited
                if (wallpaper == null) {
                    refreshWallpaper(false);
                }

                if (wallpaper != null) {
                    if (wallpaper.preDraw()) {
                        
                        //TODO: simple profile to make sure draw is not being called when it shouldn't be on wp that are 'static'
                        canvas = holder.lockCanvas();
                        if (canvas != null) {
                            wallpaper.draw(canvas);
                        }

                        // wallpaper.touched(null);
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            // }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                if (wallpaper != null && wallpaper.drawInterval >= 0) {
                    handler.postDelayed(drawRunner, wallpaper.drawInterval);
                }
                // else {
                // handler.postDelayed(drawRunner, 1000);
                // }
            }
        }
    }
}
