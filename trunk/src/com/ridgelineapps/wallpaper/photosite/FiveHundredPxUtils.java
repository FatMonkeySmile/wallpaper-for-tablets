/*
 * Copyright (C) 2012 Wallpaper for Tablets (http://code.google.com/p/wallpaper-for-tablets)
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
/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ridgelineapps.wallpaper.photosite;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;

import com.ridgelineapps.wallpaper.Rand;

public class FiveHundredPxUtils {
   public static final String API_REST_HOST = "api.500px.com";
   public static final String API_REST_URL = "/v1/";
   
   // IMPORTANT: Replace this 500px API consumer key with your own
   static final String CONSUMER_KEY = "HUXXnccG7AbleR2oTz4Kx6GxNl2jOakpPy4wMRfV";
   
   public static final int IO_BUFFER_SIZE = 4 * 1024;
   public static final FiveHundredPxUtils sInstance = new FiveHundredPxUtils();
   public HttpClient mClient;

   private FiveHundredPxUtils() {
      final HttpParams params = new BasicHttpParams();
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      HttpProtocolParams.setContentCharset(params, "UTF-8");

      final SchemeRegistry registry = new SchemeRegistry();
      registry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));

      final ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);

      mClient = new DefaultHttpClient(manager, params);
   }

   public String getRandomPhoto() {
      HttpEntity entity1 = null;
      HttpEntity entity2 = null;
      try {
         final int rpp = 20;
         Uri.Builder uri = buildGetMethod("photos");
         uri.appendQueryParameter("feature", "popular");
         uri.appendQueryParameter("rpp", "" + rpp); // "results per page" default is 20, but we set it here just to make sure
         uri.appendQueryParameter("page", "1");
         uri.appendQueryParameter("exclude", "Nude");
   
         HttpGet get = new HttpGet(uri.build().toString());
         HttpHost host = new HttpHost(API_REST_HOST, 443, "https");
         HttpResponse response = mClient.execute(host, get);
         if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            entity1 = response.getEntity();
            InputStream in = entity1.getContent();
            int totalPages = 1;

            JSONObject json = new JSONObject(in);
            if (json != null && json.has("photos")) {

               if (json.has("total_items")) {
                  int totalImages = json.getInt("total_items");
                  totalPages = totalImages / rpp;
               }

               int page = Rand.i(totalPages) + 1;

               uri = buildGetMethod("photos");
               uri.appendQueryParameter("feature", "popular");
               uri.appendQueryParameter("rpp", "" + rpp);
               uri.appendQueryParameter("page", "" + page);
               uri.appendQueryParameter("exclude", "Nude");
               get = new HttpGet(uri.build().toString());
               host = new HttpHost(API_REST_HOST, 443, "https");
               response = mClient.execute(host, get);
               
               if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                  entity2 = response.getEntity();
                  in = entity2.getContent();
                  JSONObject json2 = new JSONObject(in);
                  if (json2 != null && json2.has("photos")) {

                     JSONArray photos = (JSONArray) json.get("photos");
                     if (photos == null || photos.length() == 0) {
                        return "fail";
                     } else {
                        JSONObject photo = photos.getJSONObject(Rand.i(photos.length()));
                        String url = photo.getString("image_url");
                        url = url.replace("2.jpg", "4.jpg");
                        return url;
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (entity1 != null) {
               entity1.consumeContent();
            }
         }
         catch(Exception e) {
            // Ignore...
         }
         try {
            if (entity2 != null) {
               entity2.consumeContent();
            }
         }
         catch(Exception e) {
            // Ignore...
         }
      }
      
      return null;
   }

   private static Uri.Builder buildGetMethod(String resource) {
      final Uri.Builder builder = new Uri.Builder();
      builder.path(API_REST_URL + resource).appendQueryParameter("consumer_key", CONSUMER_KEY);
      return builder;
   }
}
