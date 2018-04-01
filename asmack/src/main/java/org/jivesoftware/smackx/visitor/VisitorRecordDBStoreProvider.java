package org.jivesoftware.smackx.visitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.agmbat.sql.DataType;
import com.agmbat.sql.Param;
import com.agmbat.sql.TableSqlBuilder;

import org.jivesoftware.smackx.db.DataContentProvider;

import java.util.ArrayList;

public class VisitorRecordDBStoreProvider {

    static String getTableName() {
        return "visitor_record";
    }

    static final Uri getContentUri() {
        return Uri.parse("content://" + DataContentProvider.AUTHORITY + "/" + getTableName()
                + "?" + DataContentProvider.URI_PARAMETER_NOTIFY + "=true");
    }

    static final Uri getContentUriNoNotify() {
        return Uri.parse("content://" + DataContentProvider.AUTHORITY + "/" + getTableName()
                + "?" + DataContentProvider.URI_PARAMETER_NOTIFY + "=false");
    }

    static Uri getContentUri(final long id, final boolean notify) {
        return Uri.parse("content://" + DataContentProvider.AUTHORITY + "/" + getTableName()
                + "/" + id + "?" + DataContentProvider.URI_PARAMETER_NOTIFY + "=" + notify);
    }

    public interface Columns extends BaseColumns {
        public static final String WHO_VISITOR_JID = "who_visitor_jid";
        public static final String VISITOR_WHO_JID = "visitor_who_jid";
        public static final String VISITOR_ENTRANCE = "visitor_entrance";
        public static final String VISITOR_TIME = "visitor_time";
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

    private Context mContext;

    public VisitorRecordDBStoreProvider(Context context) {
        mContext = context;
    }

    public int delete(String selection, String[] selectionArgs) {
        int ret = -1;
        try {
            ret = mContext.getContentResolver().delete(getContentUri(), selection, selectionArgs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }

    public ArrayList<VisitorRecordObject> query(String[] projection, String selection,
                                                String[] selectionArgs, String sortOrder) {
        ArrayList<VisitorRecordObject> array = new ArrayList<VisitorRecordObject>();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getContentUri(), projection, selection, selectionArgs, sortOrder);

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

    public Uri insert(VisitorRecordObject t) {
        Uri ret = null;
        try {
            ret = mContext.getContentResolver().insert(getContentUri(), VisitorRecordDBStoreProvider.onAddToDatabase(t, new ContentValues()));
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }

    public int update(VisitorRecordObject t, String selection, String[] selectionArgs) {
        int ret = -1;
        try {
            ret = mContext.getContentResolver().update(getContentUri(), VisitorRecordDBStoreProvider.onAddToDatabase(t, new ContentValues()),
                    selection, selectionArgs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }
}
