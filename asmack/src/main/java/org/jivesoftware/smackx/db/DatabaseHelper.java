package org.jivesoftware.smackx.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jivesoftware.smackx.favoritedme.FavoritedMeDBStoreProvider;
import org.jivesoftware.smackx.message.MessageStorage;
import org.jivesoftware.smackx.visitor.VisitorMeReadFlagDBStoreProvider;
import org.jivesoftware.smackx.visitor.VisitorRecordDBStoreProvider;

/**
 * TODO 用户数据库, 需要将登陆用户隔离, 一个用户是一个数据文件夹
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "swan.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FavoritedMeDBStoreProvider.getCreateTableStr());
        db.execSQL(VisitorMeReadFlagDBStoreProvider.getCreateTableStr());
        db.execSQL(VisitorRecordDBStoreProvider.getCreateTableStr());
        db.execSQL(MessageStorage.getCreateTableStr());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}