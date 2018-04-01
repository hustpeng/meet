/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.message;

import android.text.TextUtils;

import com.agmbat.android.AppResources;

import org.jivesoftware.smackx.message.MessageObject.Msg_Status;

import java.util.ArrayList;

public class MessageStorage {
    private MessageDBStoreProvider dbStoreProvider;

    public MessageStorage() {
        dbStoreProvider = new MessageDBStoreProvider(AppResources.getAppContext());
    }

    public void insertMsg(MessageObject newMsg) {
        if (newMsg != null) {
            dbStoreProvider.insert(newMsg);
        }
    }

    public void updateMsg(MessageObject newMsg) {
        if (newMsg != null) {
            dbStoreProvider.update(newMsg, MessageDBStoreProvider.Columns.MSG_ID + "=?",
                    new String[] {
                        newMsg.getMsg_id()
                    });
        }
    }

    public MessageObject getMsg(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return null;
        }

        ArrayList<MessageObject> arrayList = dbStoreProvider.query(
                MessageDBStoreProvider.Columns.MSG_ID + "=?", new String[] {
                    msgId
                }, null);
        if (arrayList != null && arrayList.size() > 0) {
            return arrayList.get(0);
        } else {
            return null;
        }
    }

    public void deleteMsg(String msgId) {
        dbStoreProvider.delete(MessageDBStoreProvider.Columns.MSG_ID + "=?", new String[] {
            msgId
        });
    }

    public void deleteAllMsg() {
        dbStoreProvider.delete(null, null);
    }

    public void deleteMsg(String[] msgIds) {
        if (msgIds == null || msgIds.length == 0) {
            return;
        }
        int size = msgIds.length;
        int i = 0;
        StringBuilder builder = new StringBuilder(MessageDBStoreProvider.Columns.MSG_ID + "=?");
        while (i < size - 1) {
            builder.append(" OR " + MessageDBStoreProvider.Columns.MSG_ID + "=?");
            i++;
        }
        dbStoreProvider.delete(builder.toString(), msgIds);
    }

    public void correctMessagesStatus() {
        dbStoreProvider.updateStatus(
                Msg_Status.FAILED.ordinal(),
                MessageDBStoreProvider.Columns.MSG_STATUS + "=? Or "
                        + MessageDBStoreProvider.Columns.MSG_STATUS + "=? Or "
                        + MessageDBStoreProvider.Columns.MSG_STATUS + "=?",
                new String[] {
                        String.valueOf(Msg_Status.SENDING.ordinal()),
                        String.valueOf(Msg_Status.UPLOADING.ordinal()),
                        String.valueOf(Msg_Status.LOCATING.ordinal())
                });
    }

    public void deleteChatMessage(MessageObject msg) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(MessageDBStoreProvider.Columns.MSG_SENDER_JID);
        builder.append("=?");
        builder.append(" AND ");
        builder.append(MessageDBStoreProvider.Columns.MSG_RECEIVER_JID);
        builder.append("=?");
        builder.append(")");
        builder.append(" OR ");
        builder.append("(");
        builder.append(MessageDBStoreProvider.Columns.MSG_SENDER_JID);
        builder.append("=?");
        builder.append(" AND ");
        builder.append(MessageDBStoreProvider.Columns.MSG_RECEIVER_JID);
        builder.append("=?");
        builder.append(")");

        String where = builder.toString();
        String[] selectionArgs = new String[] {
                msg.getSenderJid(), msg.getReceiverJid(), msg.getReceiverJid(), msg.getSenderJid()
        };
        dbStoreProvider.delete(where, selectionArgs);
    }

}
