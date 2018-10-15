package org.jivesoftware.smackx.visitor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import com.agmbat.android.AppResources;
import com.agmbat.sql.DataType;
import com.agmbat.sql.Param;
import com.agmbat.sql.TableSqlBuilder;

import org.jivesoftware.smackx.db.DatabaseHelper;

import java.util.ArrayList;

public class VisitorRecordDBStoreProvider {

    private DatabaseHelper mOpenHelper;


    public VisitorRecordDBStoreProvider() {
        mOpenHelper = new DatabaseHelper(AppResources.getAppContext());
    }

    static String getTableName() {
        return "visitor_record";
    }

    static public String getCreateTableStr() {
        TableSqlBuilder builder = new TableSqlBuilder(getTableName());
        builder.addColumn(Columns._ID, DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
        builder.addColumn(Columns.WHO_VISITOR_JID, DataType.TEXT);
        builder.addColumn(Columns.VISITOR_WHO_JID, DataType.TEXT);
        builder.addColumn(Columns.VISITOR_ENTRANCE, DataType.TEXT);
        builder.addColumn(Columns.VISITOR_TIME, DataType.INTEGER);
        return builder.buildSql();
    }

    public static ContentValues onAddToDatabase(VisitorRecordObject obj, ContentValues values) {
        if (obj == null || values == null) return null;

        values.put(Columns.WHO_VISITOR_JID, obj.getWhoVisitorJid());
        values.put(Columns.VISITOR_WHO_JID, obj.getVisitorWhoJid());
        values.put(Columns.VISITOR_ENTRANCE, obj.getEntrance());
        values.put(Columns.VISITOR_TIME, obj.getVisitorTime());
        return values;
    }

    public int delete(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(getTableName(), selection, selectionArgs);
    }

    public ArrayList<VisitorRecordObject> query(String[] projection, String selection,
                                                String[] selectionArgs, String sortOrder) {

        ArrayList<VisitorRecordObject> array = new ArrayList<VisitorRecordObject>();

        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableName());
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();

            cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            final int whoVisitorJidIndex = cursor.getColumnIndex(Columns.WHO_VISITOR_JID);
            final int visitorWhoJidIndex = cursor.getColumnIndex(Columns.VISITOR_WHO_JID);
            final int entranceIndex = cursor.getColumnIndex(Columns.VISITOR_ENTRANCE);
            final int visitorTimeIndex = cursor.getColumnIndex(Columns.VISITOR_TIME);

            while (cursor.moveToNext()) {
                VisitorRecordObject obj = new VisitorRecordObject();
                obj.setWhoVisitorJid(cursor.getString(whoVisitorJidIndex));
                obj.setVisitorWhoJid(cursor.getString(visitorWhoJidIndex));
                obj.setEntrance(cursor.getString(entranceIndex));
                obj.setVisitorTime(cursor.getLong(visitorTimeIndex));
                array.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return array;
    }

    public void insert(VisitorRecordObject t) {
        ContentValues values = new ContentValues();
        onAddToDatabase(t, values);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.insert(getTableName(), null, values);
    }

    public int update(VisitorRecordObject t, String selection, String[] selectionArgs) {
        ContentValues values = VisitorRecordDBStoreProvider.onAddToDatabase(t, new ContentValues());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(getTableName(), values, selection, selectionArgs);
        return count;
    }

    public interface Columns extends BaseColumns {
        public static final String WHO_VISITOR_JID = "who_visitor_jid";
        public static final String VISITOR_WHO_JID = "visitor_who_jid";
        public static final String VISITOR_ENTRANCE = "visitor_entrance";
        public static final String VISITOR_TIME = "visitor_time";
    }
}
