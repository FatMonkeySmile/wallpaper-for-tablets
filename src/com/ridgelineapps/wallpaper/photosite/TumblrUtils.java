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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Xml;
import android.view.InflateException;

import com.ridgelineapps.wallpaper.photosite.FlickrUtils.PhotoSize;

public class TumblrUtils {
    static final String LOG_TAG = "TumblrUtils";

    public static final int IO_BUFFER_SIZE = 4 * 1024;

    public static final TumblrUtils sInstance = new TumblrUtils();

    public HttpClient mClient;


    public static class PhotoList {
        private ArrayList<Photo> mPhotos;
        private int mStart;
        public int mTotal;

        private void add(Photo photo) {
            mPhotos.add(photo);
        }

        public Photo get(int index) {
            return mPhotos.get(index);
        }

        public int getCount() {
            return mPhotos.size();
        }

        public int getStart() {
            return mStart;
        }
        
        public int getTotal() {
            return mTotal;
        }
    }
    
    public static class Photo {
        private String mId;
        int mWidth;
        int mHeight;
//        private String mTitle;
//        private String mDate;
//        private String mOwner;
        
        private String mUrl;
        
        PhotoList list;
        int pageAt;
        int imageAt;
        int totalImages;

        private Photo() {
        }
        
        public void setList(PhotoList list, int pageAt, int imageAt, int totalImages) {
            this.list = list;
            this.pageAt = pageAt;
            this.imageAt = imageAt;
            this.totalImages = totalImages;
        }

//        public String getOwner() {
//            return mOwner;
//        }

        public String getId() {
            return mId;
        }
        
//        public String getTitle() {
//            return mTitle;
//        }

        public String getUrl() {
            return mUrl;
        }
        
        public Bitmap loadPhotoBitmap(PhotoSize size) {
            Bitmap bitmap = null;
            InputStream in = null;
            BufferedOutputStream out = null;

            try {
                String urlStr = getUrl();
                
                in = new BufferedInputStream(new URL(urlStr).openStream(),
                        IO_BUFFER_SIZE);

//                    bitmap = BitmapFactory.decodeStream(in);
                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
                copy(in, out);
                out.flush();

                final byte[] data = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (IOException e) {
                android.util.Log.e(FlickrUtils.LOG_TAG, "Could not load photo: " + this, e);
            } finally {
                closeStream(in);
                closeStream(out);
            }

            return bitmap;
        }
    }


    public static TumblrUtils get() {
        return sInstance;
    }    

    private TumblrUtils() {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");

        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        final ThreadSafeClientConnManager manager =
                new ThreadSafeClientConnManager(params, registry);

        mClient = new DefaultHttpClient(manager, params);
    }

    public PhotoList getPhotos(String acct, int perPage, int start) {
        String url = acct + ".tumblr.com"; 
        final Uri.Builder uri = new Uri.Builder();
        uri.path("/api/read");
        uri.appendQueryParameter("num", "" + perPage);
        uri.appendQueryParameter("start", "" + start);
        uri.appendQueryParameter("type", "photo");

        final HttpGet get = new HttpGet(uri.build().toString());
        final PhotoList photos = new PhotoList();
        
        try {
            executeRequest(url, get, new ResponseHandler() {
                public void handleResponse(InputStream in) throws IOException {
                    parseResponse(in, new ResponseParser() {
                        public void parseResponse(XmlPullParser parser)
                                throws XmlPullParserException, IOException {
                            parsePhotos(parser, photos);
                        }
                    });
                }
            });
        } catch (IOException e) {
            android.util.Log.e(LOG_TAG, "Could not find photos for : " + acct);
        }

        return photos;
    }
    
    void downloadPhoto(Photo photo, OutputStream destination) throws IOException {
        final BufferedOutputStream out = new BufferedOutputStream(destination, IO_BUFFER_SIZE);
        final String url = photo.getUrl();
        final HttpGet get = new HttpGet(url);

        HttpEntity entity = null;
        try {
            final HttpResponse response = mClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                entity = response.getEntity();
                entity.writeTo(out);
                out.flush();
            }
        } finally {
            if (entity != null) {
                entity.consumeContent();
            }
        }
    }

    private void parsePhotos(XmlPullParser parser, PhotoList photos)
            throws XmlPullParserException, IOException {
        int type;
        String name;
//        SimpleDateFormat parseFormat = null;
//        SimpleDateFormat outputFormat = null;

        final int depth = parser.getDepth();

        Photo photo = null;
        int width = 0;
        String url = null;
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            
            if (name.equals("posts")) {
                photos.mStart = Integer.parseInt(parser.getAttributeValue(null, "start"));
                photos.mTotal = Integer.parseInt(parser.getAttributeValue(null, "total"));
                photos.mPhotos = new ArrayList<Photo>();
            } else if (name.equals("post")) {
                if(photo != null && url != null) {
                    addPhoto(photos, photo, url);
                }
                photo = new Photo();
                url = null;
                width = 0;
                photo.mId = parser.getAttributeValue(null, "id");
                try
                {
                    photo.mWidth = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    photo.mHeight = Integer.parseInt(parser.getAttributeValue(null, "height"));
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
//                photo.mTitle = parser.getAttributeValue(null, RESPONSE_ATTR_TITLE);
//                photo.mDate = parser.getAttributeValue(null, RESPONSE_ATTR_DATE_TAKEN);
//                photo.mUrlSq = parser.getAttributeValue(null, RESPONSE_ATTR_URL_SQ);
//                photo.mUrlS = parser.getAttributeValue(null, RESPONSE_ATTR_URL_S);
//                photo.mUrlM = parser.getAttributeValue(null, RESPONSE_ATTR_URL_M);
//                photo.mUrlL = parser.getAttributeValue(null, RESPONSE_ATTR_URL_L);
//                photo.mUrlO = parser.getAttributeValue(null, RESPONSE_ATTR_URL_O);
//                photo.mOwner = parser.getAttributeValue(null, RESPONSE_ATTR_OWNER);
//
//                if (parseFormat == null) {
//                    parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    outputFormat = new SimpleDateFormat("MMMM d, yyyy");
//                }
//                try {
//                    photo.mDate = outputFormat.format(parseFormat.parse(photo.mDate));
//                } catch (ParseException e) {
//                    android.util.Log.w(LOG_TAG, "Could not parse photo date", e);
//                }
            } else if(name.equals("photo-url")) {
                try {
                    String maxWidthStr = parser.getAttributeValue(null, "max-width");
                    int maxWidth = Integer.parseInt(maxWidthStr);
                    if(maxWidth > width) {
                        width = maxWidth;
                        parser.next();
                        url = parser.getText();
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
//            } else if(name.equals("photo-link-url")) {
//                width = Integer.MAX_VALUE;
//                parser.next();
//                url = parser.getText();
            }
        }
        
        if(photo != null && url != null) {
            addPhoto(photos, photo, url);
        }
    }
    
    private void addPhoto(PhotoList photos, Photo photo, String url) {
        if(photo.mWidth != 0 && photo.mWidth < TumblrSite.MIN_WIDTH) {
            return;
        }
        if(photo.mHeight != 0 && photo.mHeight < TumblrSite.MIN_HEIGHT) {
            return;
        }
        photo.mUrl = url;
        photos.add(photo);
    }

    private void parseResponse(InputStream in, ResponseParser responseParser) throws IOException {
        final XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new InputStreamReader(in));

            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
                // Empty
            }

            if (type != XmlPullParser.START_TAG) {
                throw new InflateException(parser.getPositionDescription()
                        + ": No start tag found!");
            }

//            String name = parser.getName();
//            if (RESPONSE_TAG_RSP.equals(name)) {
//                final String value = parser.getAttributeValue(null, RESPONSE_ATTR_STAT);
//                if (!RESPONSE_STATUS_OK.equals(value)) {
//                    throw new IOException("Wrong status: " + value);
//                }
//            }

            responseParser.parseResponse(parser);

        } catch (XmlPullParserException e) {
            final IOException ioe = new IOException("Could not parse the response");
            ioe.initCause(e);
            throw ioe;
        }
    }

    private void executeRequest(String url, HttpGet get, ResponseHandler handler) throws IOException {
        HttpEntity entity = null;
        HttpHost host = new HttpHost(url, 80, "http");
        try {
            final HttpResponse response = mClient.execute(host, get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                entity = response.getEntity();
                final InputStream in = entity.getContent();
                handler.handleResponse(in);
            }
        } finally {
            if (entity != null) {
                entity.consumeContent();
            }
        }
    }

    static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(TumblrUtils.LOG_TAG, "Could not close stream", e);
            }
        }
    }

    private static interface ResponseHandler {
        public void handleResponse(InputStream in) throws IOException;
    }

    private static interface ResponseParser {
        public void parseResponse(XmlPullParser parser) throws XmlPullParserException, IOException;
    }
}
