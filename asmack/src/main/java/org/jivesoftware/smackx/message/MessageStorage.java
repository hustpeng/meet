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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageSubType;
import org.jivesoftware.smackx.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageStorage {

    ////
    private static MessageIndex sMessageIndex;
    private DatabaseHelper mOpenHelper;


    public MessageStorage() {
        mOpenHelper = new DatabaseHelper(AppResources.getAppContext());
    }

    private static String getTableName() {
        return "messages_data";
    }

    static public String getCreateTableStr() {
        TableSqlBuilder builder = new TableSqlBuilder(getTableName());
        builder.addColumn(Columns._ID, DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
        builder.addColumn(Columns.MSG_FROM_JID, DataType.TEXT);
        builder.addColumn(Columns.MSG_TO_JID, DataType.TEXT);
        builder.addColumn(Columns.MSG_SENDER_JID, DataType.TEXT);
        builder.addColumn(Columns.MSG_SENDER_NAME, DataType.TEXT);
        builder.addColumn(Columns.MSG_SENDER_AVATAR, DataType.TEXT);
        builder.addColumn(Columns.MSG_CHAT_TYPE, DataType.TEXT);
        builder.addColumn(Columns.MSG_BODY, DataType.TEXT);
        builder.addColumn(Columns.MSG_HTML, DataType.TEXT);
        builder.addColumn(Columns.MSG_IS_OUTGOING, DataType.BOOLEAN);
        builder.addColumn(Columns.MSG_ID, DataType.TEXT);
        builder.addColumn(Columns.MSG_TYPE, DataType.INTEGER);
        builder.addColumn(Columns.MSG_STATUS, DataType.INTEGER);
        builder.addColumn(Columns.MSG_DATE, DataType.INTEGER);
        builder.addColumn(Columns.MSG_ACCOUNT, DataType.TEXT);
        return builder.buildSql();
    }

    /**
     * 将相关信息添加的ContentValues中
     *
     * @param obj
     * @param values
     */
    public static void onAddToDatabase(MessageObject obj, ContentValues values) {
        if (obj == null || values == null) {
            return;
        }
        values.put(Columns.MSG_FROM_JID, obj.getFromJid());
        values.put(Columns.MSG_TO_JID, obj.getToJid());
        values.put(Columns.MSG_SENDER_JID, obj.getSenderJid());
        values.put(Columns.MSG_SENDER_NAME, obj.getSenderNickName());
        values.put(Columns.MSG_SENDER_AVATAR, obj.getSenderAvatar());
        if (null != obj.getChatType()) {
            values.put(Columns.MSG_CHAT_TYPE, obj.getChatType().name());
        }
        values.put(Columns.MSG_BODY, obj.getBody());
        values.put(Columns.MSG_HTML, obj.getHtml());
        values.put(Columns.MSG_IS_OUTGOING, obj.isOutgoing());
        values.put(Columns.MSG_ID, obj.getMsgId());
        if (null != obj.getMsgType()) {
            values.put(Columns.MSG_TYPE, obj.getMsgType().ordinal());
        }
        values.put(Columns.MSG_STATUS, obj.getMsgStatus().ordinal());
        values.put(Columns.MSG_DATE, obj.getDate());
        values.put(Columns.MSG_ACCOUNT, obj.getAccount());
    }

    /**
     * 合并消息
     *
     * @param senderArray
     * @param receiverArray
     * @return
     */
    private static List<MessageObject> mergeMessage(List<MessageObject> senderArray,
                                                    List<MessageObject> receiverArray) {
        List<MessageObject> resultArray = new ArrayList<>();
        if (senderArray.size() > 0 && receiverArray.size() > 0) {
            for (MessageObject senderMessageObject : senderArray) {
                boolean found = false;
                for (MessageObject receiverMessageObject : receiverArray) {
                    if (senderMessageObject.getToJid().equals(receiverMessageObject.getFromJid())) {
                        if (senderMessageObject.getDate() > receiverMessageObject.getDate()) {
                            resultArray.add(senderMessageObject);
                        } else {
                            resultArray.add(receiverMessageObject);
                        }
                        found = true;
                        receiverArray.remove(receiverMessageObject);
                        break;
                    }
                }
                if (!found) {
                    resultArray.add(senderMessageObject);
                }
            }
            resultArray.addAll(receiverArray);
        } else if (senderArray.size() > 0) {
            resultArray.addAll(senderArray);
        } else if (receiverArray.size() > 0) {
            resultArray.addAll(receiverArray);
        }
        return resultArray;
    }

    private static MessageObject cursorToMessage(Cursor cursor) {
        if (sMessageIndex == null) {
            sMessageIndex = new MessageIndex(cursor);
        }
        MessageObject obj = new MessageObject();
        obj.setFromJid(cursor.getString(sMessageIndex.fromJidIndex));
        obj.setToJid(cursor.getString(sMessageIndex.toJidIndex));
        obj.setSenderJid(cursor.getString(sMessageIndex.senderJidIndex));
        obj.setSenderAvatar(cursor.getString(sMessageIndex.senderAvatarIndex));
        obj.setSenderNickName(cursor.getString(sMessageIndex.senderNameIndex));
        String chatType = cursor.getString(sMessageIndex.chatTypeIndex);
        if (!TextUtils.isEmpty(chatType)) {
            obj.setChatType(Message.Type.fromString(chatType));
        }
        obj.setBody(cursor.getString(sMessageIndex.bodyIndex));
        obj.setMsgId(cursor.getString(sMessageIndex.msgIdIndex));
        obj.setMsgType(MessageSubType.values()[cursor.getInt(sMessageIndex.msgTypeIndex)]);
        obj.setMsgStatus(MessageObjectStatus.values()[cursor.getInt(sMessageIndex.msgStatusIndex)]);
        obj.setDate(cursor.getLong(sMessageIndex.dateIndex));
        obj.setHtml(cursor.getString(sMessageIndex.htmlIndex));
        if (cursor.getInt(sMessageIndex.outgoingIndex) != 0) {
            obj.setOutgoing(true);
        } else {
            obj.setOutgoing(false);
        }
        obj.setAccount(cursor.getString(sMessageIndex.accountIndex));
        return obj;
    }

    public int update(MessageObject t, String selection, String[] selectionArgs) {
        ContentValues values = new ContentValues();
        onAddToDatabase(t, values);
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

    /**
     * 插入数据消息内容
     *
     * @param msg
     * @return
     */
    public void insertMsg(MessageObject msg) {
        if (msg != null) {
            ContentValues values = new ContentValues();
            onAddToDatabase(msg, values);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.insert(getTableName(), null, values);
        }
    }

    public void updateMsg(MessageObject newMsg, String myJid) {
        if (newMsg != null) {
            update(newMsg, Columns.MSG_ID + "=? And " + Columns.MSG_ACCOUNT + "=?",
                    new String[]{
                            newMsg.getMsgId(),
                            myJid

                    });
        }
    }

    public void deleteMsg(String msgId, String myJid) {
        delete(Columns.MSG_ID + "=? And " + Columns.MSG_ACCOUNT + "=?", new String[]{
                msgId, myJid
        });
    }

    public MessageObject getMsg(String msgId, String myJid) {
        if (TextUtils.isEmpty(msgId)) {
            return null;
        }
        ArrayList<MessageObject> arrayList = query(
                Columns.MSG_ID + "=? And " + Columns.MSG_ACCOUNT + "=?", new String[]{
                        msgId, myJid
                }, null);
        if (arrayList != null && arrayList.size() > 0) {
            return arrayList.get(0);
        } else {
            return null;
        }
    }

    private ArrayList<MessageObject> query(String selection, String[] selectionArgs, String sortOrder) {
        ArrayList<MessageObject> array = new ArrayList<MessageObject>();
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableName());
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();
            cursor = qb.query(db, null, selection, selectionArgs, null, null, sortOrder);
            while (cursor.moveToNext()) {
                MessageObject obj = cursorToMessage(cursor);
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

    public List<MessageObject> search(String myJid, String chatJid, String keyword) {
        List<MessageObject> array = new ArrayList<MessageObject>();
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableName());
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();
            String where = Columns.MSG_ACCOUNT + "=? AND (("
                    + Columns.MSG_FROM_JID + "=? AND " + Columns.MSG_TO_JID + "=?) OR ("
                    + Columns.MSG_FROM_JID + "=? AND " + Columns.MSG_TO_JID + "=?)) AND ("
                    + Columns.MSG_BODY + " LIKE ? OR " + Columns.MSG_SENDER_NAME + " LIKE ?)";
            String likeArgs = "%" + keyword + "%";
            String[] args = new String[]{myJid, myJid, chatJid, chatJid, myJid, likeArgs, likeArgs};
            cursor = qb.query(db, null, where, args, null, null, Columns.MSG_DATE + " DESC");
            while (cursor.moveToNext()) {
                MessageObject obj = cursorToMessage(cursor);
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
     * 删除两个人的聊天消息
     *
     * @param msg
     */
    public void deleteChatMessage(MessageObject msg) {
        deleteChatMessage(msg.getFromJid(), msg.getToJid(), msg.getAccount());
    }

    /**
     * 删除两个人的聊天记录
     *
     * @param aJid
     * @param bJid
     */
    public void deleteChatMessage(String aJid, String bJid, String account) {
        StringBuilder builder = new StringBuilder();
        builder.append("((");
        builder.append(Columns.MSG_FROM_JID);
        builder.append("=?");
        builder.append(" AND ");
        builder.append(Columns.MSG_TO_JID);
        builder.append("=?");
        builder.append(")");
        builder.append(" OR ");
        builder.append("(");
        builder.append(Columns.MSG_FROM_JID);
        builder.append("=?");
        builder.append(" AND ");
        builder.append(Columns.MSG_TO_JID);
        builder.append("=?");
        builder.append("))");
        builder.append(" AND ");
        builder.append(Columns.MSG_ACCOUNT);
        builder.append("=?");

        String where = builder.toString();
        String[] selectionArgs = new String[]{
                aJid, bJid, bJid, aJid, account
        };
        delete(where, selectionArgs);
    }

    // MessageFragment data
    public List<MessageObject> getAllMessage(String myJid) {
        if (null == myJid) {
            myJid = "";
        }
        List<MessageObject> senderArray = getSenderMessageObjects(myJid);
        List<MessageObject> receiverArray = getReceiverMessageObjects(myJid);
        List<MessageObject> resultArray = mergeMessage(senderArray, receiverArray);
        MessageObject.sortByDate(resultArray, false);
        return resultArray;
    }

    private List<MessageObject> getReceiverMessageObjects(String myJid) {
        List<MessageObject> messages = new ArrayList<MessageObject>();
        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), null,
                "(" + Columns.MSG_TO_JID + "=? And " + Columns.MSG_IS_OUTGOING + "=0 And " + Columns.MSG_ACCOUNT + "=?)"
                , new String[]{
                        myJid, myJid
                }, Columns.MSG_FROM_JID, null, MessageStorage.Columns.MSG_DATE + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                MessageObject obj = cursorToMessage(cursor);
                obj.setOutgoing(false);
                messages.add(obj);
            }
            cursor.close();
        }
        return messages;
    }

    /**
     * 获取已发送的信息
     *
     * @param myJid
     * @return
     */
    private List<MessageObject> getSenderMessageObjects(String myJid) {
        List<MessageObject> messages = new ArrayList<MessageObject>();
        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), null,
                Columns.MSG_FROM_JID + "=? And " + Columns.MSG_IS_OUTGOING + "=1 And " + Columns.MSG_ACCOUNT + "=?"
                , new String[]{
                        myJid, myJid
                }, Columns.MSG_TO_JID, null, Columns.MSG_DATE + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                MessageObject obj = cursorToMessage(cursor);
                obj.setOutgoing(true);
                messages.add(obj);
            }
            cursor.close();
        }
        return messages;
    }

    /**
     * 查询两个人的消息记录
     *
     * @param myJid
     * @param chatJid
     * @return
     */
    public List<MessageObject> getMessages(String myJid, String chatJid, long since, int limit, boolean dateAsc) {
        if (TextUtils.isEmpty(myJid) || TextUtils.isEmpty(chatJid)) {
            return new ArrayList<MessageObject>();
        }
        List<MessageObject> resultArray = new ArrayList<MessageObject>();
        String orderBy = null;
        if (dateAsc) {
            orderBy = Columns.MSG_DATE + " ASC";
        } else {
            orderBy = Columns.MSG_DATE + " DESC";
        }
        String where = "((" + Columns.MSG_FROM_JID + "=? And " + Columns.MSG_TO_JID + "=?) Or ("
                + Columns.MSG_FROM_JID + "=? And " + Columns.MSG_TO_JID + "=?)) And "
                + Columns.MSG_ACCOUNT + "=?";

        String[] args = null;
        if (since != -1) {
            where += " And " + Columns.MSG_DATE + "<?";
            args = new String[]{myJid, chatJid, chatJid, myJid, myJid, String.valueOf(since)};
        } else {
            args = new String[]{myJid, chatJid, chatJid, myJid, myJid};
        }

        String limitArgs = null;
        if (limit != -1) {
            limitArgs = String.valueOf(limit);
        }
        Cursor cursor = mOpenHelper.getReadableDatabase().query(getTableName(), null, where,
                args, null, null, orderBy, limitArgs);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MessageObject obj = cursorToMessage(cursor);
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
                }, Columns.MSG_TO_JID + "=? and " + MessageStorage.Columns.MSG_STATUS + "=? and "
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

    public interface Columns extends BaseColumns {

        public static final String MSG_FROM_JID = "from_jid";
        public static final String MSG_TO_JID = "to_jid";
        public static final String MSG_SENDER_JID = "sender_jid";
        public static final String MSG_SENDER_NAME = "sender_name";
        public static final String MSG_SENDER_AVATAR = "sender_avatar";
        public static final String MSG_CHAT_TYPE = "chat_type";
        public static final String MSG_BODY = "body";
        public static final String MSG_HTML = "html";
        public static final String MSG_IS_OUTGOING = "outgoing";
        public static final String MSG_ID = "msg_id";
        public static final String MSG_TYPE = "msg_type";
        public static final String MSG_STATUS = "msg_status";
        public static final String MSG_DATE = "msg_date";
        public static final String MSG_ACCOUNT = "account";
    }

    private static class MessageIndex {

        private final int fromJidIndex;
        private final int toJidIndex;
        private final int senderJidIndex;
        private final int senderAvatarIndex;
        private final int senderNameIndex;
        private final int chatTypeIndex;
        private final int bodyIndex;
        private final int msgIdIndex;
        private final int msgTypeIndex;
        private final int msgStatusIndex;
        private final int dateIndex;
        private final int htmlIndex;
        private final int outgoingIndex;
        private final int accountIndex;

        public MessageIndex(Cursor cursor) {
            fromJidIndex = cursor.getColumnIndex(Columns.MSG_FROM_JID);
            toJidIndex = cursor.getColumnIndex(Columns.MSG_TO_JID);
            senderJidIndex = cursor.getColumnIndex(Columns.MSG_SENDER_JID);
            senderAvatarIndex = cursor.getColumnIndex(Columns.MSG_SENDER_AVATAR);
            senderNameIndex = cursor.getColumnIndex(Columns.MSG_SENDER_NAME);
            chatTypeIndex = cursor.getColumnIndex(Columns.MSG_CHAT_TYPE);
            bodyIndex = cursor.getColumnIndex(Columns.MSG_BODY);
            htmlIndex = cursor.getColumnIndex(Columns.MSG_HTML);
            msgIdIndex = cursor.getColumnIndex(Columns.MSG_ID);
            msgTypeIndex = cursor.getColumnIndex(Columns.MSG_TYPE);
            msgStatusIndex = cursor.getColumnIndex(Columns.MSG_STATUS);
            dateIndex = cursor.getColumnIndex(Columns.MSG_DATE);
            outgoingIndex = cursor.getColumnIndex(Columns.MSG_IS_OUTGOING);
            accountIndex = cursor.getColumnIndex(Columns.MSG_ACCOUNT);
        }

    }
}
