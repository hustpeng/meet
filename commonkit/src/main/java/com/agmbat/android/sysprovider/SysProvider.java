/*
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com>
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
package com.agmbat.android.sysprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.view.View;

import com.agmbat.android.SystemManager;
import com.agmbat.android.image.BitmapUtils;
import com.agmbat.android.utils.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SysProvider {

    public static final int CAMERA_BUCKET_ID = getBucketId(Environment.getExternalStorageDirectory().toString() + "/"
            + BucketNames.CAMERA);
    private static final String TAG = SysProvider.class.getSimpleName();
    private static final boolean LOAD_ALL_FOLDERS = true;
    private static final String CAMERA = "DCIM/Camera";

    /**
     * And to convert the image URI to the direct file system path of the image file
     *
     * @param uri
     * @return
     */
    public static String getImagePathFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        String path = null;
        final String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(index);
            }
            cursor.close();
        }
        return path;
    }

    public static Bitmap getThumbnailOfLastPhoto() {
        final ContentResolver resolver = SystemManager.getContentResolver();
        Cursor cursor =
                MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                                MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_TAKEN}, null,
                        null,
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC LIMIT 1");
        Bitmap thumb = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                thumb =
                        MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
            }
            cursor.close();
        }
        return thumb;
    }

    public static Bitmap getBitmap(Uri url) {
        try {
            return MediaStore.Images.Media.getBitmap(SystemManager.getContentResolver(), url);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPhoneNumber(Uri uri) {
        long id = ContentUris.parseId(uri);
        Cursor cursor =
                query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
        List<String> numbers = new ArrayList<String>();
        if (cursor != null) {
            int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                numbers.add(cursor.getString(numberColumn));
            }
            cursor.close();
        }
        // 多个电话号码时选择第一个
        return numbers.size() > 0 ? numbers.get(0) : null;
    }

    public static List<LocalImage> loadImage() {
        List<LocalImage> list = new ArrayList<LocalImage>();
        String selection;
        String[] selectionArgs;
        if (LOAD_ALL_FOLDERS) {
            selection = null;
            selectionArgs = null;
        } else {
            selection = ImageColumns.BUCKET_DISPLAY_NAME + " = ?";
            selectionArgs = new String[]{"Camera"};
        }
        Cursor cursor =
                query(Images.Media.EXTERNAL_CONTENT_URI, LocalImage.PROJECTION, selection, selectionArgs,
                        ImageColumns.DATE_TAKEN + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocalImage image = new LocalImage(cursor);
                list.add(image);
            }
            cursor.close();
        }
        return list;
    }

    public static Cursor queryScreenshot() {
        String selection = ImageColumns.BUCKET_DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{"Screenshots"};
        Cursor cursor =
                query(Images.Media.EXTERNAL_CONTENT_URI, LocalImage.PROJECTION, selection, selectionArgs,
                        ImageColumns.DATE_TAKEN + " DESC");
        return cursor;
    }

    public static LocalImage loadImage(Uri uri) {
        Cursor cursor = query(uri, LocalImage.PROJECTION, null, null, null);
        LocalImage image = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                image = new LocalImage(cursor);
            }
            cursor.close();
        }
        return image;
    }

    public static List<LocalVideo> loadVideo2() {
        return loadVideo2(null);
    }

    public static List<LocalVideo> loadVideo2(OnLoadVideoCallBack callBack) {
        List<LocalVideo> list = new ArrayList<LocalVideo>();
        Cursor cursor = queryVideo();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocalVideo item = new LocalVideo(cursor);
                list.add(item);
                if (callBack != null) {
                    callBack.onLoad(item);
                }
            }
            cursor.close();
        }
        return list;
    }

    public static List<LocalVideo> loadVideo() {
        String whereClause = VideoColumns.BUCKET_ID + " = ?";
        String orderClause = VideoColumns.DATE_TAKEN + " DESC, " + VideoColumns._ID + " DESC";
        Uri uri = Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = LocalVideo.PROJECTION;
        return loadVideo(uri, projection, whereClause, orderClause);
    }

    public static List<LocalVideo> loadVideo(int start, int count) {
        String whereClause = VideoColumns.BUCKET_ID + " = ?";
        String orderClause = VideoColumns.DATE_TAKEN + " DESC, " + VideoColumns._ID + " DESC";
        Uri baseUri = Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = LocalVideo.PROJECTION;
        Uri uri = baseUri.buildUpon().appendQueryParameter("limit", start + "," + count).build();
        return loadVideo(uri, projection, whereClause, orderClause);
    }

    public static List<LocalVideo> loadVideo(Uri uri, String[] projection, String whereClause, String orderClause) {
        List<LocalVideo> list = new ArrayList<LocalVideo>();
        Cursor cursor =
                query(uri, projection, whereClause, new String[]{String.valueOf(CAMERA_BUCKET_ID)}, orderClause);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocalVideo item = new LocalVideo(cursor);
                list.add(item);
            }
            cursor.close();
        }
        return list;
    }

    private static Cursor queryVideo() {
        // Uri uri = baseUri.buildUpon().appendQueryParameter("limit", start +
        // "," + count).build();
        String whereClause = VideoColumns.BUCKET_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(CAMERA_BUCKET_ID)
        };
        String orderClause = VideoColumns.DATE_TAKEN + " DESC, " + VideoColumns._ID + " DESC";
        return query(Video.Media.EXTERNAL_CONTENT_URI, LocalVideo.PROJECTION, whereClause,
                selectionArgs, orderClause);

    }

    public static Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                               String sortOrder) {
        return SystemManager.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    static int getBucketId(String path) {
        return path.toLowerCase().hashCode();
    }

    static Cursor getItemCursor(Uri uri, String[] projection, int id) {
        return query(uri, projection, "_id=?", new String[]{String.valueOf(id)}, null);
    }

    /**
     * Saves the current chart state with the given name to the given path on the sdcard leaving the path empty "" will
     * put the saved file directly on the SD card chart is saved as a PNG image, example: saveToPath("myfilename",
     * "foldername1/foldername2");
     *
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     * @return returns true on success, false on error
     */
    public static boolean saveToPath(View v, String title, String pathOnSD) {
        Bitmap bitmap = BitmapUtils.getBitmapFromView(v);
        String path = Environment.getExternalStorageDirectory().getPath() + pathOnSD + "/" + title + ".png";
        BitmapUtils.compressToFile(bitmap, CompressFormat.PNG, 40, path);
        return true;
    }

    /**
     * Saves the current state of the chart to the gallery as a JPEG image. The filename and compression can be set. 0
     * == maximum compression, 100 = low compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     *
     * @param fileName e.g. "my_image"
     * @param quality  e.g. 50, min = 0, max = 100
     * @return returns true if saving was successfull, false if not
     */
    public static boolean saveToGallery(View v, String fileName, int quality) {
        // restrain quality
        if (quality < 0 || quality > 100) {
            quality = 50;
        }
        long currentTime = System.currentTimeMillis();
        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }
        String filePath = file.getAbsolutePath() + "/" + fileName;
        Bitmap b = BitmapUtils.getBitmapFromView(v);
        BitmapUtils.compressToFile(b, quality, filePath);
        long size = new File(filePath).length();
        ContentValues values = new ContentValues(8);
        // store the details
        values.put(Images.Media.TITLE, fileName);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.DATE_ADDED, currentTime);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DESCRIPTION, "MPAndroidChart-Library Save");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);
        return v.getContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values) == null ? false
                : true;
    }

    public static void printVideoInfo(LocalVideo video) {
        Log.d(TAG, "id:" + video.id);
        Log.d(TAG, "caption:" + video.caption);
        Log.d(TAG, "mimeType:" + video.mimeType);
        Log.d(TAG, "fileSize:" + video.fileSize);
        Log.d(TAG, "latitude:" + video.latitude);
        Log.d(TAG, "longitude:" + video.longitude);
        Log.d(TAG, "dateTakenInMs:" + video.dateTakenInMs);
        Log.d(TAG, "dateAddedInSec:" + video.dateAddedInSec);
        Log.d(TAG, "dateModifiedInSec:" + video.dateModifiedInSec);
        Log.d(TAG, "filePath:" + video.filePath);
        Log.d(TAG, "bucketId:" + video.bucketId);
        Log.d(TAG, "width:" + video.width);
        Log.d(TAG, "height:" + video.height);
        Log.d(TAG, "durationInSec:" + video.durationInSec);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(video.getFilePath());

        String trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
        String composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        String date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String num_tracks = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
        String writer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
        String mimetype = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        String albumartist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        String disc_number = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
        String compilation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
        String has_audio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        String has_video = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
        String video_width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String video_height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String timed_text_languages =
                retriever.extractMetadata(21);// MediaMetadataRetriever.METADATA_KEY_TIMED_TEXT_LANGUAGES
        String is_drm = retriever.extractMetadata(22);// MediaMetadataRetriever.METADATA_KEY_IS_DRM
        String location = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

        Log.d(TAG, "[MediaMetadataRetriever]trackNumber:" + trackNumber);
        Log.d(TAG, "[MediaMetadataRetriever]album:" + album);
        Log.d(TAG, "[MediaMetadataRetriever]artist:" + artist);
        Log.d(TAG, "[MediaMetadataRetriever]composer:" + composer);
        Log.d(TAG, "[MediaMetadataRetriever]date:" + date);
        Log.d(TAG, "[MediaMetadataRetriever]genre:" + genre);
        Log.d(TAG, "[MediaMetadataRetriever]title:" + title);
        Log.d(TAG, "[MediaMetadataRetriever]year:" + year);
        Log.d(TAG, "[MediaMetadataRetriever]durationInSec:" + duration);
        Log.d(TAG, "[MediaMetadataRetriever]num_tracks:" + num_tracks);
        Log.d(TAG, "[MediaMetadataRetriever]writer:" + writer);
        Log.d(TAG, "[MediaMetadataRetriever]mimetype:" + mimetype);
        Log.d(TAG, "[MediaMetadataRetriever]albumartist:" + albumartist);
        Log.d(TAG, "[MediaMetadataRetriever]disc_number:" + disc_number);
        Log.d(TAG, "[MediaMetadataRetriever]compilation:" + compilation);
        Log.d(TAG, "[MediaMetadataRetriever]has_audio:" + has_audio);
        Log.d(TAG, "[MediaMetadataRetriever]has_video:" + has_video);
        Log.d(TAG, "[MediaMetadataRetriever]video_width:" + video_width);
        Log.d(TAG, "[MediaMetadataRetriever]video_height:" + video_height);
        Log.d(TAG, "[MediaMetadataRetriever]bitrate:" + bitrate);
        Log.d(TAG, "[MediaMetadataRetriever]timed_text_languages:" + timed_text_languages);
        Log.d(TAG, "[MediaMetadataRetriever]is_drm:" + is_drm);
        Log.d(TAG, "[MediaMetadataRetriever]location:" + location);
        Log.d(TAG, "[MediaMetadataRetriever]rotation:" + rotation);
        retriever.release();
    }

    public static void loadMediaItem(OnLoadMediaItemCallBack callback) {
        Cursor imageCursor = queryImage();
        Cursor videoCursor = queryVideo();

        LocalImage image = null;
        LocalVideo video = null;

        boolean imageFinish = (imageCursor == null);
        boolean videoFinish = (videoCursor == null);

        while (!(imageFinish && videoFinish)) {
            if (image == null && !imageFinish) {
                if (imageCursor.moveToNext()) {
                    image = new LocalImage(imageCursor);
                } else {
                    imageFinish = true;
                    imageCursor.close();
                }
            }

            if (video == null && !videoFinish) {
                if (videoCursor.moveToNext()) {
                    video = new LocalVideo(videoCursor);
                } else {
                    videoFinish = true;
                    videoCursor.close();
                }
            }

            if (imageFinish && videoFinish) {
                return;
            }
            if (!imageFinish && !videoFinish) {
                if (image.dateAddedInSec > video.dateAddedInSec) {
                    callback.onLoad(image);
                    image = null;
                } else {
                    callback.onLoad(video);
                    video = null;
                }
            } else if (imageFinish) {
                callback.onLoad(video);
                video = null;
            } else {
                callback.onLoad(image);
                image = null;
            }
        }
    }

    public static List<LocalImage> loadImages(OnLoadImageCallBack callBack) {
        List<LocalImage> list = new ArrayList<LocalImage>();
        Cursor cursor = queryImage();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocalImage image = new LocalImage(cursor);
                list.add(image);
                if (callBack != null) {
                    callBack.onLoad(image);
                }
            }
            cursor.close();
        }
        return list;
    }

    public static LocalImage queryImage(Uri uri) {
        Cursor cursor = query(uri, LocalImage.PROJECTION, null, null, null);
        LocalImage image = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                image = new LocalImage(cursor);
            }
            cursor.close();
        }
        return image;
    }

    private static Cursor queryImage() {
        String selection;
        String[] selectionArgs;
        if (LOAD_ALL_FOLDERS) {
            selection = null;
            selectionArgs = null;
        } else if (AppUtils.debuggable()) {
            selection = ImageColumns.BUCKET_DISPLAY_NAME + " = ? OR "
                    + ImageColumns.BUCKET_DISPLAY_NAME + " = ?";
            selectionArgs = new String[]{
                    "Camera", "testphotos"
            };
        } else {
            selection = ImageColumns.BUCKET_DISPLAY_NAME + " = ?";
            selectionArgs = new String[]{
                    "Camera"
            };
        }
        return query(Images.Media.EXTERNAL_CONTENT_URI, LocalImage.PROJECTION, selection,
                selectionArgs, ImageColumns.DATE_TAKEN + " DESC");
    }

    /**
     * 让系统扫描媒体文件, 可以将此文件加载到系统数据库中
     */
    public static void scanMediaFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    public interface OnLoadImageCallBack {
        public void onLoad(LocalImage image);
    }

    public interface OnLoadVideoCallBack {
        public void onLoad(LocalVideo video);
    }

    public interface OnLoadMediaItemCallBack extends OnLoadImageCallBack, OnLoadVideoCallBack {
    }

}
