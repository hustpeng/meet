package org.jivesoftware.smackx.db;

import org.jivesoftware.smackx.favoritedme.FavoritedMeDBStoreProvider;
import org.jivesoftware.smackx.message.MessageDBStoreProvider;
import org.jivesoftware.smackx.visitor.VisitorMeReadFlagDBStoreProvider;
import org.jivesoftware.smackx.visitor.VisitorRecordDBStoreProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.agmbat.android.db.SqlArguments;

public class DataContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.swan.datas";
    public static final String URI_PARAMETER_NOTIFY = "notify";

    private static final String DATABASE_NAME = "swan.db";
    private static final int DATABASE_VERSION = 1;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FavoritedMeDBStoreProvider.getCreateTableStr());
            db.execSQL(VisitorMeReadFlagDBStoreProvider.getCreateTableStr());
            db.execSQL(VisitorRecordDBStoreProvider.getCreateTableStr());
            db.execSQL(MessageDBStoreProvider.getCreateTableStr());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    private void sendNotify(final Uri uri) {
        String notify = uri.getQueryParameter(URI_PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, values);
        if (rowId <= 0) {
            return null;
        }
        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);
        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments args = new SqlArguments(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int numValues = values.length;
            for (int i = 0; i < numValues; i++) {
                if (db.insert(args.table, null, values[i]) <= 0) {
                    return 0;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        sendNotify(uri);
        return values.length;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) sendNotify(uri);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0) sendNotify(uri);
        return count;
    }


}
