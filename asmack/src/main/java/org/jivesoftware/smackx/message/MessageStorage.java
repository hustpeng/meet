/**
 * $RCSfile$
 * $Revision$
 * $Date$
 * <p>
 * Copyright 2003-2007 Jive Software.
 * <p>
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.sql.DataType;
import com.agmbat.sql.Param;
import com.agmbat.sql.TableSqlBuilder;

import org.jivesoftware.smack.packet.MessageSubType;
import org.jivesoftware.smackx.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageStorage {

    private DatabaseHelper mOpenHelper;

    public MessageStorage() {
        mOpenHelper = new DatabaseHelper(AppResources.getAppContext());
    }

    /**
     * 插入数据消息内容
     *
     * @param t
     * @return
     */
    public long insert(MessageObject t) {
        ContentValues values = onAddToDatabase(t, new ContentValues());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert(getTableName(), null, values);
        return rowId;
    }

    public int update(MessageObject t, String selection, String[] selectionArgs) {
        ContentValues values = onAddToDatabase(t, new ContentValues());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(getTableName(), values, selection, selectionArgs);
        return count;
    }

    public int delete(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(getTableName(), selection, selectionArgs);
    }

    public void deleteAllMsg() {
        delete(null, null);
    }

    public int updateStatus(int newStatus, String selection, String[] selectionArgs) {
        ContentValues values = new ContentValues();
        values.put(Columns.MSG_STATUS, newStatus);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(getTableName(), values, selection, selectionArgs);
    }

    public void insertMsg(MessageObject newMsg) {
        if (newMsg != null) {
            insert(newMsg);
        }
    }

    public void updateMsg(MessageObject newMsg) {
        if (newMsg != null) {
            update(newMsg, Columns.MSG_ID + "=?",
                    new String[]{
                            newMsg.getMsg_id()
                    });
        }
    }

    public void deleteMsg(String msgId) {
        delete(Columns.MSG_ID + "=?", new String[]{
                msgId
        });
    }

    public MessageObject getMsg(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return null;
        }
        ArrayList<MessageObject> arrayList = query(
                Columns.MSG_ID + "=?", new String[]{
                        msgId
                }, null);
        if (arrayList != null && arrayList.size() > 0) {
            return arrayList.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<MessageObject> query(String selection, String[] selectionArgs, String sortOrder) {
        ArrayList<MessageObject> array = new ArrayList<MessageObject>();

        Cursor cursor = null;
        try {


            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableName());
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();

            cursor = qb.query(db, null, selection, selectionArgs, null, null, sortOrder);

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
                obj.setMsgType(MessageSubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(MessageObjectStatus.values()[cursor.getInt(msgStatusIndex)]);
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


    public void deleteMsg(String[] msgIds) {
        if (msgIds == null || msgIds.length == 0) {
            return;
        }
        int size = msgIds.length;
        int i = 0;
        StringBuilder builder = new StringBuilder(Columns.MSG_ID + "=?");
        while (i < size - 1) {
            builder.append(" OR " + Columns.MSG_ID + "=?");
            i++;
        }
        delete(builder.toString(), msgIds);
    }

    public void correctMessagesStatus() {
        updateStatus(
                MessageObjectStatus.FAILED.ordinal(),
                Columns.MSG_STATUS + "=? Or "
                        + Columns.MSG_STATUS + "=? Or "
                        + Columns.MSG_STATUS + "=?",
                new String[]{
                        String.valueOf(MessageObjectStatus.SENDING.ordinal()),
                        String.valueOf(MessageObjectStatus.UPLOADING.ordinal()),
                        String.valueOf(MessageObjectStatus.LOCATING.ordinal())
                });
    }

    /**
     * 删除聊天消息
     *
     * @param msg
     */
    public void deleteChatMessage(MessageObject msg) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(Columns.MSG_SENDER_JID);
        builder.append("=?");
        builder.append(" AND ");
        builder.append(Columns.MSG_RECEIVER_JID);
        builder.append("=?");
        builder.append(")");
        builder.append(" OR ");
        builder.append("(");
        builder.append(Columns.MSG_SENDER_JID);
        builder.append("=?");
        builder.append(" AND ");
        builder.append(Columns.MSG_RECEIVER_JID);
        builder.append("=?");
        builder.append(")");

        String where = builder.toString();
        String[] selectionArgs = new String[]{
                msg.getSenderJid(), msg.getReceiverJid(), msg.getReceiverJid(), msg.getSenderJid()
        };
        delete(where, selectionArgs);
    }

    static String getTableName() {
        return "messages_data";
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
        values.put(Columns.MSG_TYPE, obj.getMsgType().ordinal());
        values.put(Columns.MSG_STATUS, obj.getMsg_status().ordinal());
        values.put(Columns.MSG_DATE, obj.getDate());
        return values;
    }


    ///
    //////
    // MessageFragment data
    public List<MessageObject> getAllMessage(String myJid) {
        List<MessageObject> sender_array = getSenderMessageObjects(myJid);
        List<MessageObject> receiver_array = getReceiverMessageObjects(myJid);
        List<MessageObject> resultArray = mergeMessage(sender_array, receiver_array);
        return resultArray;
    }

    private List<MessageObject> getReceiverMessageObjects(String myJid) {
        List<MessageObject> messages = new ArrayList<MessageObject>();
        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), null,
                Columns.MSG_RECEIVER_JID + "=? And " + Columns.MSG_IS_OUTGOING + "=0)"
                        + " group by " + " ( " + Columns.MSG_SENDER_JID, new String[]{
                        myJid
                }, null, null, MessageStorage.Columns.MSG_DATE + " DESC");

        if (cursor != null) {
            int senderJidIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_SENDER_JID);
            int receiverJidIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_RECEIVER_JID);
            int senderNameIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_SENDER_NAME);
            int bodyIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_BODY);
            int msgIdIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_ID);
            int msgTypeIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_TYPE);
            int msgStatusIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_STATUS);
            int dateIndex = cursor.getColumnIndex(MessageStorage.Columns.MSG_DATE);

            while (cursor.moveToNext()) {
                MessageObject obj = new MessageObject();
                obj.setSenderJid(cursor.getString(senderJidIndex));
                obj.setReceiverJid(cursor.getString(receiverJidIndex));
                obj.setSenderNickName(cursor.getString(senderNameIndex));
                obj.setBody(cursor.getString(bodyIndex));
                obj.setMsg_id(cursor.getString(msgIdIndex));
                obj.setMsgType(MessageSubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(MessageObjectStatus.values()[cursor.getInt(msgStatusIndex)]);
                obj.setDate(cursor.getLong(dateIndex));
                obj.setOutgoing(false);
                messages.add(obj);
            }
            cursor.close();
        }
        return messages;
    }

    private List<MessageObject> getSenderMessageObjects(String myJid) {
        List<MessageObject> messages = new ArrayList<MessageObject>();
        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), null,
                Columns.MSG_SENDER_JID + "=? And " + Columns.MSG_IS_OUTGOING + "=1)" + " group by "
                        + " ( " + Columns.MSG_RECEIVER_JID, new String[]{
                        myJid
                }, null, null, Columns.MSG_DATE + " DESC");

        if (cursor != null) {
            int senderJidIndex = cursor.getColumnIndex(Columns.MSG_SENDER_JID);
            int receiverJidIndex = cursor.getColumnIndex(Columns.MSG_RECEIVER_JID);
            int senderNameIndex = cursor.getColumnIndex(Columns.MSG_SENDER_NAME);
            int bodyIndex = cursor.getColumnIndex(Columns.MSG_BODY);
            int msgIdIndex = cursor.getColumnIndex(Columns.MSG_ID);
            int msgTypeIndex = cursor.getColumnIndex(Columns.MSG_TYPE);
            int msgStatusIndex = cursor.getColumnIndex(Columns.MSG_STATUS);
            int dateIndex = cursor.getColumnIndex(Columns.MSG_DATE);

            while (cursor.moveToNext()) {
                MessageObject obj = new MessageObject();
                obj.setSenderJid(cursor.getString(senderJidIndex));
                obj.setReceiverJid(cursor.getString(receiverJidIndex));
                obj.setSenderNickName(cursor.getString(senderNameIndex));
                obj.setBody(cursor.getString(bodyIndex));
                obj.setMsg_id(cursor.getString(msgIdIndex));
                obj.setMsgType(MessageSubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(MessageObjectStatus.values()[cursor.getInt(msgStatusIndex)]);
                obj.setDate(cursor.getLong(dateIndex));
                obj.setOutgoing(true);
                messages.add(obj);
            }
            cursor.close();
        }
        return messages;
    }

    private static List<MessageObject> mergeMessage(List<MessageObject> sender_array,
                                                    List<MessageObject> receiver_array) {
        List<MessageObject> resultArray = null;
        boolean duplicateFlag = false;
        if (sender_array.size() > 0 && receiver_array.size() > 0) {
            for (MessageObject senderMessageObject : sender_array) {
                for (MessageObject receiverMessageObject : receiver_array) {
                    if (senderMessageObject.getSenderJid().equals(
                            receiverMessageObject.getReceiverJid())) {
                        if (senderMessageObject.getDate() > receiverMessageObject.getDate()) {
                            receiver_array.remove(receiverMessageObject);
                            receiver_array.add(senderMessageObject);
                        }
                        duplicateFlag = true;
                        break;
                    }
                }

                if (!duplicateFlag) {
                    receiver_array.add(senderMessageObject);
                }
            }
            resultArray = receiver_array;
        } else if (sender_array.size() > 0) {
            resultArray = sender_array;
        } else {
            resultArray = receiver_array;
        }
        return resultArray;
    }

    /**
     * 查询两个人的消息记录
     *
     * @param myJid
     * @param chatJid
     * @return
     */
    public List<MessageObject> getMessages(String myJid, String chatJid) {
        List<MessageObject> resultArray = new ArrayList<MessageObject>();

        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), null, "("
                        + Columns.MSG_SENDER_JID + "=? And " + Columns.MSG_RECEIVER_JID + "=?) Or ("
                        + Columns.MSG_SENDER_JID + "=? And " + Columns.MSG_RECEIVER_JID + "=?)",
                new String[]{
                        myJid, chatJid,
                        chatJid, myJid
                }, null, null, Columns.MSG_DATE + " ASC");
        if (cursor != null) {
            int senderJidIndex = cursor.getColumnIndex(Columns.MSG_SENDER_JID);
            int receiverJidIndex = cursor.getColumnIndex(Columns.MSG_RECEIVER_JID);
            int senderNameIndex = cursor.getColumnIndex(Columns.MSG_SENDER_NAME);
            int bodyIndex = cursor.getColumnIndex(Columns.MSG_BODY);
            int htmlIndex = cursor.getColumnIndex(Columns.MSG_HTML);
            int msgIdIndex = cursor.getColumnIndex(Columns.MSG_ID);
            int msgTypeIndex = cursor.getColumnIndex(Columns.MSG_TYPE);
            int msgStatusIndex = cursor.getColumnIndex(Columns.MSG_STATUS);
            int outgoingIndex = cursor.getColumnIndex(Columns.MSG_IS_OUTGOING);
            int dateIndex = cursor.getColumnIndex(Columns.MSG_DATE);

            while (cursor.moveToNext()) {
                MessageObject obj = new MessageObject();
                obj.setSenderJid(cursor.getString(senderJidIndex));
                obj.setReceiverJid(cursor.getString(receiverJidIndex));
                obj.setSenderNickName(cursor.getString(senderNameIndex));
                obj.setHtml(cursor.getString(htmlIndex));
                obj.setBody(cursor.getString(bodyIndex));
                obj.setMsg_id(cursor.getString(msgIdIndex));
                obj.setMsgType(MessageSubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(MessageObjectStatus.values()[cursor.getInt(msgStatusIndex)]);
                obj.setDate(cursor.getLong(dateIndex));
                if (cursor.getInt(outgoingIndex) != 0) {
                    obj.setOutgoing(true);
                } else {
                    obj.setOutgoing(false);
                }
                resultArray.add(obj);
            }
            cursor.close();
        }
        return resultArray;
    }

    public int getUnReadMsgCount(String jid) {
        int count = 0;
//        if (!XMPPManager.getInstance().isLogin()) {
//            return 0;
//        }
//        String jid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        if (TextUtils.isEmpty(jid)) {
            return 0;
        }
        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), new String[]{
                        MessageStorage.Columns.MSG_STATUS
                }, MessageStorage.Columns.MSG_RECEIVER_JID + "=? and " + MessageStorage.Columns.MSG_STATUS + "=? and "
                        + MessageStorage.Columns.MSG_IS_OUTGOING + "=?",
                new String[]{
                        jid, String.valueOf(MessageObjectStatus.UNREAD.ordinal()), String.valueOf(0)
                }, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }
    ////
}
