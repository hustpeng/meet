
package com.agmbat.android.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public final class DBManager {

    private Context mContext;

    private static DBManager sInstance;

    private DBManager(Context context) {
        mContext = context;
    }

    public static DBManager getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Please call init first.");
        }
        return sInstance;
    }

    public static DBManager init(Context context) {
        if (sInstance == null) {
            sInstance = new DBManager(context);
        }
        return sInstance;
    }

    /**
     * 插入数据库操作
     * 
     * @param resolver
     * @param uri
     * @param values
     * @return
     */
    public static long insert(ContentResolver resolver, Uri uri, ContentValues values) {
        Uri retUri = resolver.insert(uri, values);
        return ContentUris.parseId(retUri);
    }

    private ContentResolver getContentResolver() {
        return mContext.getContentResolver();
    }
}
