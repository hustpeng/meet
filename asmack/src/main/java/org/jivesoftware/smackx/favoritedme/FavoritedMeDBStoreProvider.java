package org.jivesoftware.smackx.favoritedme;

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

public class FavoritedMeDBStoreProvider {

    static String getTableName() {
        return "favoritedme_read_flag";
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
        public static final String FAVORITEDME_READ_JID = "jid";
        public static final String FAVORITEDME_READ_DATE = "date";
    }

    static public String getCreateTableStr() {
        TableSqlBuilder builder = new TableSqlBuilder(getTableName());
        builder.addColumn(Columns._ID, DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
        builder.addColumn(Columns.FAVORITEDME_READ_JID, DataType.TEXT);
        builder.addColumn(Columns.FAVORITEDME_READ_DATE, DataType.INTEGER);
        return builder.buildSql();
    }

    public static ContentValues onAddToDatabase(FavoritedMeReadFlagObject obj, ContentValues values) {
        if (obj == null || values == null) {
            return null;
        }
        values.put(Columns.FAVORITEDME_READ_JID, obj.getJid());
        values.put(Columns.FAVORITEDME_READ_DATE, obj.getCreate_date());
        return values;
    }

    private Context mContext;

    public FavoritedMeDBStoreProvider(Context context) {
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

    public ArrayList<FavoritedMeReadFlagObject> query(String[] projection, String selection,
                                                      String[] selectionArgs, String sortOrder) {
        ArrayList<FavoritedMeReadFlagObject> array = new ArrayList<FavoritedMeReadFlagObject>();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getContentUri(), projection, selection, selectionArgs, sortOrder);

            final int jidIndex = cursor.getColumnIndex(Columns.FAVORITEDME_READ_JID);
            final int dateIndex = cursor.getColumnIndex(Columns.FAVORITEDME_READ_DATE);

            while (cursor.moveToNext()) {
                FavoritedMeReadFlagObject obj = new FavoritedMeReadFlagObject();
                obj.setJid(cursor.getString(jidIndex));
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

    public Uri insert(FavoritedMeReadFlagObject t) {
        Uri ret = null;
        try {
            ret = mContext.getContentResolver().insert(getContentUri(), FavoritedMeDBStoreProvider.onAddToDatabase(t, new ContentValues()));
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }

    public int update(FavoritedMeReadFlagObject t, String selection, String[] selectionArgs) {
        int ret = -1;
        try {
            ret = mContext.getContentResolver().update(getContentUri(), FavoritedMeDBStoreProvider.onAddToDatabase(t, new ContentValues()),
                    selection, selectionArgs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }
}
