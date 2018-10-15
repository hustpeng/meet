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
package com.agmbat.android.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现一个provider 基于数据库操作
 */
public abstract class DatabaseProvider extends ContentProvider {

    protected static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private String mAuthorities;
    private DataHelper mDataHelper;
    private int mMatchId = 1;
    private Map<String, ProviderFactory> mProviderFactoryMap;
    private Map<String, ProviderFactory> mProviderIdFactoryMap;

    private static String getAuthority(Context context,
                                       Class<? extends ContentProvider> providerClass) {
        String authority = null;
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            String className = providerClass.getName();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
            ProviderInfo[] provides = packageInfo.providers;
            ProviderInfo providerInfo = null;
            for (ProviderInfo provider : provides) {
                String name = provider.name;
                if (className.equals(name) || className.equals(combine(packageName, name))) {
                    providerInfo = provider;
                    break;
                }
            }
            if (providerInfo != null) {
                authority = providerInfo.authority;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return authority;
    }

    private static String combine(String packageName, String name) {
        if (name.startsWith(".")) {
            return packageName + name;
        } else {
            return packageName + "." + name;
        }
    }

    /**
     * 创建数据库
     *
     * @param db
     */
    protected abstract void onCreateDatabase(SQLiteDatabase db);

    /**
     * 更新数据库
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    protected abstract void onUpgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion);

    @Override
    public boolean onCreate() {
        mAuthorities = getAuthority(getContext(), getClass());
        mProviderFactoryMap = new HashMap<String, ProviderFactory>();
        mProviderIdFactoryMap = new HashMap<String, ProviderFactory>();
        mDataHelper = new DataHelper(getContext(), getDatabaseName(), null, getDatabaseVersion());
        initUri();
        return true;
    }

    public Uri initUri(String path, ProviderFactory factory) {
        int pathId = nextMatchId();
        addURI(path, pathId);
        mProviderFactoryMap.put(String.valueOf(pathId), factory);
        int pathNumberId = nextMatchId();
        addURI(path + "/#", pathNumberId);
        mProviderIdFactoryMap.put(String.valueOf(pathNumberId), factory);
        return createUri(path);
    }

    protected String getAuthorities() {
        return mAuthorities;
    }

    protected abstract void initUri();

    public abstract String getDatabaseName();

    public abstract int getDatabaseVersion();

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Provider helper = createUriHelper(uri);
        Cursor cursor = helper.query(mDataHelper.getReadableDatabase(), projection, selection,
                selectionArgs, sortOrder);
        if (null != cursor) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Provider helper = createUriHelper(uri);
        long rowID = helper.insert(mDataHelper.getWritableDatabase(), values);
        Uri notifyUri = null;
        if (rowID > 0) {
            notifyUri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(notifyUri, null);
        }
        return notifyUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Provider helper = createUriHelper(uri);
        int count = helper.delete(mDataHelper.getWritableDatabase(), selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Provider helper = createUriHelper(uri);
        int updateCount = helper.update(mDataHelper.getWritableDatabase(), values, selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

    /**
     * 通过不同的Uri创建不同的UriHelper
     *
     * @param uri
     * @return
     */
    private Provider createUriHelper(Uri uri) {
        Provider helper = getProvider(uri);
        if (helper == null) {
            throw new IllegalArgumentException("Unknown URL:" + uri.toString());
        }
        return helper;
    }

    protected Provider getProvider(Uri uri) {
        int match = URI_MATCHER.match(uri);
        String key = String.valueOf(match);
        ProviderFactory providerFactory = mProviderFactoryMap.get(key);
        if (providerFactory != null) {
            return providerFactory.createProvider();
        }
        ProviderFactory providerIdFactory = mProviderIdFactoryMap.get(key);
        if (providerIdFactory != null) {
            long id = ContentUris.parseId(uri);
            return providerIdFactory.createUriHelper(id);
        }
        return null;
    }

    protected void addURI(String path, int code) {
        URI_MATCHER.addURI(getAuthorities(), path, code);
    }

    protected Uri createUri(String path) {
        return Uri.parse(String.format("content://%s/%s", getAuthorities(), path));
    }

    private int nextMatchId() {
        return mMatchId++;
    }

    public interface ProviderFactory {
        public Provider createProvider();

        public Provider createUriHelper(long id);
    }

    private final class DataHelper extends SQLiteOpenHelper {

        public DataHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            onCreateDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgradeDatabase(db, oldVersion, newVersion);
        }
    }
}
