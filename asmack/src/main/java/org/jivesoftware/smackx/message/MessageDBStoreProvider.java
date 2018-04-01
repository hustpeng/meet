package org.jivesoftware.smackx.message;

import org.jivesoftware.smack.packet.Message.SubType;
import org.jivesoftware.smackx.db.DataContentProvider;
import org.jivesoftware.smackx.favoritedme.FavoritedMeDBStoreProvider;
import org.jivesoftware.smackx.message.MessageObject.Msg_Status;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.agmbat.sql.DataType;
import com.agmbat.sql.Param;
import com.agmbat.sql.TableSqlBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageDBStoreProvider {

    static String getTableName() {
        return "messages_data";
    }

    public static final Uri getContentUri() {
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
        public static final String MSG_SENDER_JID = "sender_jid";
        public static final String MSG_RECEIVER_JID = "receiver_jid";
        public static final String MSG_SENDER_NAME = "sender_name";
        public static final String MSG_BODY = "body";
        public static final String MSG_HTML = "html";
        public static final String MSG_IS_OUTGOING = "outgoing";
        public static final String MSG_ID = "msg_id";
        public static final String MSG_TYPE = "msg_type";
        public static final String MSG_STATUS = "msg_status";
        public static final String MSG_DATE = "msg_date";
    }

    static public String getCreateTableStr() {
        TableSqlBuilder builder = new TableSqlBuilder(getTableName());
        builder.addColumn(Columns._ID, DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
        builder.addColumn(Columns.MSG_SENDER_JID, DataType.TEXT);
        builder.addColumn(Columns.MSG_RECEIVER_JID, DataType.TEXT);
        builder.addColumn(Columns.MSG_SENDER_NAME, DataType.TEXT);
        builder.addColumn(Columns.MSG_BODY, DataType.TEXT);
        builder.addColumn(Columns.MSG_HTML, DataType.TEXT);
        builder.addColumn(Columns.MSG_IS_OUTGOING, DataType.BOOLEAN);
        builder.addColumn(Columns.MSG_ID, DataType.TEXT);
        builder.addColumn(Columns.MSG_TYPE, DataType.INTEGER);
        builder.addColumn(Columns.MSG_STATUS, DataType.INTEGER);
        builder.addColumn(Columns.MSG_DATE, DataType.INTEGER);
        return builder.buildSql();
    }

    public static ContentValues onAddToDatabase(MessageObject obj, ContentValues values) {
        if (obj == null || values == null) {
            return null;
        }
        values.put(Columns.MSG_SENDER_JID, obj.getSenderJid());
        values.put(Columns.MSG_RECEIVER_JID, obj.getReceiverJid());
        values.put(Columns.MSG_SENDER_NAME, obj.getSenderNickName());
        values.put(Columns.MSG_BODY, obj.getBody());
        values.put(Columns.MSG_HTML, obj.getHtml());
        values.put(Columns.MSG_IS_OUTGOING, obj.isOutgoing());
        values.put(Columns.MSG_ID, obj.getMsg_id());
        values.put(Columns.MSG_TYPE, obj.getMsg_type().ordinal());
        values.put(Columns.MSG_STATUS, obj.getMsg_status().ordinal());
        values.put(Columns.MSG_DATE, obj.getDate());
        return values;
    }

    private Context mContext;

    public MessageDBStoreProvider(Context context) {
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

    public ArrayList<MessageObject> query(String selection, String[] selectionArgs, String sortOrder) {
        ArrayList<MessageObject> array = new ArrayList<MessageObject>();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getContentUri(), null, selection, selectionArgs, sortOrder);

            final int senderJidIndex = cursor.getColumnIndex(Columns.MSG_SENDER_JID);
            final int receiverJidIndex = cursor.getColumnIndex(Columns.MSG_RECEIVER_JID);
            final int senderNameIndex = cursor.getColumnIndex(Columns.MSG_SENDER_NAME);
            final int bodyIndex = cursor.getColumnIndex(Columns.MSG_BODY);
            final int htmlIndex = cursor.getColumnIndex(Columns.MSG_HTML);
            final int outgoingIndex = cursor.getColumnIndex(Columns.MSG_IS_OUTGOING);
            final int msgIdIndex = cursor.getColumnIndex(Columns.MSG_ID);
            final int msgTypeIndex = cursor.getColumnIndex(Columns.MSG_TYPE);
            final int msgStatusIndex = cursor.getColumnIndex(Columns.MSG_STATUS);
            final int dateIndex = cursor.getColumnIndex(Columns.MSG_DATE);

            while (cursor.moveToNext()) {
                MessageObject obj = new MessageObject();
                obj.setSenderJid(cursor.getString(senderJidIndex));
                obj.setReceiverJid(cursor.getString(receiverJidIndex));
                obj.setSenderNickName(cursor.getString(senderNameIndex));
                obj.setBody(cursor.getString(bodyIndex));
                obj.setHtml(cursor.getString(htmlIndex));
                if (cursor.getInt(outgoingIndex) != 0) {
                    obj.setOutgoing(true);
                } else {
                    obj.setOutgoing(false);
                }
                obj.setMsg_id(cursor.getString(msgIdIndex));
                obj.setMsg_type(SubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(Msg_Status.values()[cursor.getInt(msgStatusIndex)]);
                obj.setDate(cursor.getLong(dateIndex));
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

    public Uri insert(MessageObject t) {
        Uri ret = null;
        try {
            ContentValues values = MessageDBStoreProvider.onAddToDatabase(t, new ContentValues());
            ret = mContext.getContentResolver().insert(getContentUri(), values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int update(MessageObject t, String selection, String[] selectionArgs) {
        int ret = -1;
        try {
            ret = mContext.getContentResolver().update(getContentUri(), MessageDBStoreProvider.onAddToDatabase(t, new ContentValues()),
                    selection, selectionArgs);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return ret;
    }

    public int updateStatus(int newStatus, String selection, String[] selectionArgs) {
        int ret = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(Columns.MSG_STATUS, newStatus);
            ret = mContext.getContentResolver().update(getContentUri(), values, selection, selectionArgs);
        } catch (Exception e) {

        }

        return ret;
    }
}
