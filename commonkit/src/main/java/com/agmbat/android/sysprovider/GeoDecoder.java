package com.agmbat.android.sysprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.task.AsyncTaskUtils.Priority;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.log.Log;
import com.agmbat.net.HttpUtils;
import com.agmbat.sql.DataType;
import com.agmbat.sql.Param;
import com.agmbat.sql.SqlUtils;
import com.agmbat.sql.TableSqlBuilder;
import com.agmbat.text.JsonUtils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class GeoDecoder {

    private static final String TAG = GeoDecoder.class.getSimpleName();
    private static final String API_URL_QUERY_NEARBY =
            "http://restapi.amap.com/rgeocode/simple?resType=json&encode=UTF-8&range=3000&roadnum=10&crossnum=3"
                    + "&poinum=10&retvalue=10&sid=7001&rid=170562&region=%f,"
                    + "%f&ia=1&key=cb39f62d5ff2ec36194532c8d93a7866";
    private static final String API_URL_DECODE =
            "http://restapi.amap.com/v3/geocode/regeo?location=%f,%f&radius=100&key=cb39f62d5ff2ec36194532c8d93a7866";
    private static final String API_URL_SEARCH_KEYWORD =
            "http://restapi.amap.com/v3/place/text?keywords=%s&key=cb39f62d5ff2ec36194532c8d93a7866&offset=5&page=1&s"
                    + "=rsv3";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ORIGINAL_ADDRESS = "oaddr";
    private static final String COLUMN_MODIFIED_ADDRESS = "maddr";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final double CACHE_DISTANCE = 100;
    private static final String TABLE_CACHE = "cache";
    private ArrayList<CacheItem> mCacheItems;
    private DatabaseHelper mDatabaseHelper;
    private Handler mHandler;

    private GeoDecoder() {
        mDatabaseHelper = new DatabaseHelper(AppResources.getAppContext());
        HandlerThread handlerThread = new HandlerThread("GeoDecoderThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        loadCache();
    }

    public static GeoDecoder getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static CacheItem createCacheItemFromCursor(Cursor cursor) {
        CacheItem cacheItem = new CacheItem();
        cacheItem.id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        cacheItem.location = location;
        cacheItem.originalAddress = cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_ADDRESS));
        cacheItem.modifiedAddress = cursor.getString(cursor.getColumnIndex(COLUMN_MODIFIED_ADDRESS));
        return cacheItem;
    }

    private void loadCache() {
        mCacheItems = new ArrayList<CacheItem>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CACHE, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mCacheItems.add(createCacheItemFromCursor(cursor));
            }
            cursor.close();
        }
    }

    private void addToCache(String address, Location location) {
        final CacheItem cacheItem = new CacheItem();
        cacheItem.location = new Location(location);
        cacheItem.originalAddress = address;
        mCacheItems.add(cacheItem);
        AsyncTaskUtils.executeRunnableAsync(new Runnable() {
            public void run() {
                try {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_LATITUDE, cacheItem.location.getLatitude());
                    values.put(COLUMN_LONGITUDE, cacheItem.location.getLongitude());
                    values.put(COLUMN_ORIGINAL_ADDRESS, cacheItem.originalAddress);
                    values.put(COLUMN_MODIFIED_ADDRESS, cacheItem.modifiedAddress);
                    SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
                    cacheItem.id = database.insert(TABLE_CACHE, COLUMN_ORIGINAL_ADDRESS, values);
                } catch (Exception e) {
                    Log.w(TAG, e);
                }
            }
        }, Priority.LOW);
    }

    private CacheItem getCacheItem(Location location) {
        for (CacheItem item : mCacheItems) {
            if (item.location.distanceTo(location) < CACHE_DISTANCE) {
                return item;
            }
        }
        return null;
    }

    public void modifyAddress(String newName, Location location) {
        final CacheItem cacheItem = getCacheItem(location);
        if (cacheItem != null) {
            cacheItem.modifiedAddress = newName;
            AsyncTaskUtils.executeRunnableAsync(new Runnable() {
                public void run() {
                    try {
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_MODIFIED_ADDRESS, cacheItem.modifiedAddress);
                        mDatabaseHelper.getWritableDatabase().update(TABLE_CACHE, values,
                                COLUMN_ID + " = " + cacheItem.id, null);
                    } catch (Exception e) {
                        Log.w(TAG, e);
                    }
                }
            }, Priority.LOW);
        }
    }

    private String getAddressCache(Location location) {
        CacheItem cacheItem = getCacheItem(location);
        if (cacheItem != null) {
            if (!TextUtils.isEmpty(cacheItem.modifiedAddress)) {
                return cacheItem.modifiedAddress;
            }
            return cacheItem.originalAddress;
        }
        return null;
    }

    public String decode(Location location) {
        if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
            return null;
        }
        String address = getAddressCache(location);
        if (!TextUtils.isEmpty(address)) {
            return address;
        }
        String url = String.format(Locale.US, API_URL_DECODE, location.getLongitude(), location.getLatitude());
        try {
            String text = HttpUtils.getUrlAsString(url);
            JSONObject jsonObject = JsonUtils.asJsonObject(text);
            JSONObject addressObject = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent");
            address = jsonObject.getJSONObject("regeocode").getString("formatted_address");
            address = address.replaceAll("^(\\w+省)?", "");
            addToCache(address, location);
            return address;
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return null;
    }

    public void decodeAsync(final Location location, final DecodeCallback callback) {
        mHandler.post(new Runnable() {
            public void run() {
                final String address = decode(location);
                UiUtils.runOnUIThread(new Runnable() {
                    public void run() {
                        if (callback != null) {
                            callback.onDecodeFinished(location, address);
                        }
                    }
                });
            }
        });
    }

    public JSONObject queryNearbyPlace(Location location) {
        String url = String.format(Locale.US, API_URL_QUERY_NEARBY, location.getLongitude(), location.getLatitude());
        try {
            String text = HttpUtils.getUrlAsString(url);
            int index = text.indexOf('{');
            if (index >= 0) {
                text = text.substring(index);
                return new JSONObject(text);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return null;
    }

    public JSONObject queryKeyword(String keyword) {
        try {
            String url = String.format(Locale.US, API_URL_SEARCH_KEYWORD, URLEncoder.encode(keyword, "utf-8"));
            String text = HttpUtils.getUrlAsString(url);
            int index = text.indexOf('{');
            if (index >= 0) {
                text = text.substring(index);
                return new JSONObject(text);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return null;
    }

    public static interface DecodeCallback {
        void onDecodeFinished(Location location, String address);
    }

    private static class SingletonHolder {
        private static final GeoDecoder INSTANCE = new GeoDecoder();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "geo_address_cache.db";
        private static final int DATABASE_VERSION = 1;

        /**
         * Initiate a new instance of {@link DatabaseHelper}.
         *
         * @param context
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            TableSqlBuilder builder = new TableSqlBuilder(TABLE_CACHE);
            builder.addColumn(COLUMN_ID, DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
            builder.addColumn(COLUMN_ORIGINAL_ADDRESS, DataType.TEXT);
            builder.addColumn(COLUMN_MODIFIED_ADDRESS, DataType.TEXT);
            builder.addColumn(COLUMN_LATITUDE, DataType.DOUBLE);
            builder.addColumn(COLUMN_LONGITUDE, DataType.DOUBLE);
            String sql = builder.buildSql();
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SqlUtils.dropTableSql(TABLE_CACHE));
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }

    private static class CacheItem {
        long id;
        String originalAddress;
        String modifiedAddress;
        Location location;
    }

}
