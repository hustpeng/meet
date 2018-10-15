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

public class VisitorMeReadFlagDBStoreProvider {

    private Context mContext;

    public VisitorMeReadFlagDBStoreProvider(Context context) {
        mContext = context;
    }

    static String getTableName() {
        return "visitorme_read_flag";
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

    static public String getCreateTableStr() {
        TableSqlBuilder builder = new TableSqlBuilder(getTableName());
        builder.addColumn(Columns._ID, DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
        builder.addColumn(Columns.VISITORME_READ_VISITOR_JID, DataType.TEXT);
        builder.addColumn(Columns.VISITORME_READ_MY_JID, DataType.TEXT);
        builder.addColumn(Columns.VISITORME_READ_ENTRANCE, DataType.TEXT);
        builder.addColumn(Columns.VISITORME_READ_DATE, DataType.INTEGER);
        return builder.buildSql();
    }

    public static ContentValues onAddToDatabase(VisitorMeReadFlagObject obj, ContentValues values) {
        if (obj == null || values == null) return null;
        values.put(Columns.VISITORME_READ_VISITOR_JID, obj.getVisitorJid());
        values.put(Columns.VISITORME_READ_MY_JID, obj.getMyJid());
        values.put(Columns.VISITORME_READ_ENTRANCE, obj.getEntrance());
        values.put(Columns.VISITORME_READ_DATE, obj.getCreate_date());
        return values;
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

    public ArrayList<VisitorMeReadFlagObject> query(String[] projection, String selection,
                                                    String[] selectionArgs, String sortOrder) {
        ArrayList<VisitorMeReadFlagObject> array = new ArrayList<VisitorMeReadFlagObject>();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getContentUri(), projection, selection, selectionArgs, sortOrder);

            final int visitorJidIndex = cursor.getColumnIndex(Columns.VISITORME_READ_VISITOR_JID);
            final int myJidIndex = cursor.getColumnIndex(Columns.VISITORME_READ_MY_JID);
            final int entranceIndex = cursor.getColumnIndex(Columns.VISITORME_READ_ENTRANCE);
            final int dateIndex = cursor.getColumnIndex(Columns.VISITORME_READ_DATE);

            while (cursor.moveToNext()) {
                VisitorMeReadFlagObject obj = new VisitorMeReadFlagObject();
                obj.setVisitorJid(cursor.getString(visitorJidIndex));
                obj.setMyJid(cursor.getString(myJidIndex));
                obj.setEntrance(cursor.getString(entranceIndex));
                obj.setCreate_date(cursor.getLong(dateIndex));
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

    public Uri insert(VisitorMeReadFlagObject t) {
        Uri ret = null;
        try {
            ret = mContext.getContentResolver().insert(getContentUri(), VisitorMeReadFlagDBStoreProvider.onAddToDatabase(t, new ContentValues()));
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }

    public int update(VisitorMeReadFlagObject t, String selection, String[] selectionArgs) {
        int ret = -1;
        try {
            ret = mContext.getContentResolver().update(getContentUri(), VisitorMeReadFlagDBStoreProvider.onAddToDatabase(t, new ContentValues()),
                    selection, selectionArgs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }

    public interface Columns extends BaseColumns {
        public static final String VISITORME_READ_VISITOR_JID = "jid";
        public static final String VISITORME_READ_MY_JID = "my_jid";
        public static final String VISITORME_READ_ENTRANCE = "entrance";
        public static final String VISITORME_READ_DATE = "date";
    }
}
