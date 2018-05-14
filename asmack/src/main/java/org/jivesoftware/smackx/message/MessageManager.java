package org.jivesoftware.smackx.message;

import android.content.ContentResolver;
import android.database.Cursor;
import android.text.TextUtils;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.MessageSubType;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageManager extends Xepmodule {

    private final List<MessageListener> listeners;

    private MessageStorage messageStorage;

    public MessageManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
                connection.addPacketListener(messagePacketListener, messagePacketFilter);
            }
        });
        listeners = new CopyOnWriteArrayList<MessageListener>();
        messageStorage = new MessageStorage();
        correctMessagesStatus();
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {
            abortAllQuery();
            xmppConnection.removePacketListener(messagePacketListener);
        }

        @Override
        public void connectionClosed() {
            abortAllQuery();
            xmppConnection.removePacketListener(messagePacketListener);
        }
    };

    public void addListener(MessageListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    public boolean willInsertReceivedMsg(MessageObject messageObject) {
        for (MessageListener listener : listeners) {
            if (listener.willInsertReceivedMsg(messageObject)) {
                return true;
            }
        }
        return false;
    }

    private PacketFilter messagePacketFilter = new PacketFilter() {
        public boolean accept(Packet packet) {
            if (!(packet instanceof Message)) {
                return false;
            }

            Type messageType = ((Message) packet).getType();
            // the Type.normal msg is filter in VisitorManager.java
            return messageType == Type.chat/*
                                                    * || messageType ==
                                                    * Message.Type.normal
                                                    */;
        }
    };

    private PacketListener messagePacketListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            if (("http://jabber.org/protocol/muc#verify".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#admin".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#hotcircle".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#owner".equals(message.getXmlns()))) {
                return;
            }

            MessageObject messageObject = phareMessageFromPacket(message);
            if (messageObject != null) {
                if (TextUtils.isEmpty(messageObject.getBody())) {
                    PacketExtension extension = message
                            .getExtension("http://jabber.org/protocol/chatstates");
                    if (extension != null) {
                        String elementName = extension.getElementName();
                        if ("delivered".equals(elementName)) {
                            MessageObject targetMessage = messageStorage.getMsg(messageObject
                                    .getMsg_id());
                            if (targetMessage != null) {
                                targetMessage.setMsg_status(MessageObjectStatus.UNREAD);
                                messageStorage.updateMsg(targetMessage);
                            }
                        } else if ("read".equals(elementName)) {
                            MessageObject targetMessage = messageStorage.getMsg(messageObject
                                    .getMsg_id());
                            if (targetMessage != null) {
                                targetMessage.setMsg_status(MessageObjectStatus.READ);
                                messageStorage.updateMsg(targetMessage);
                            }
                        } else if ("composing".equals(elementName)) {

                        }
                    }
                    return;
                }

                String sendMsgState = "delivered";
                if (willInsertReceivedMsg(messageObject)) {
                    messageObject.setMsg_status(MessageObjectStatus.READ);
                    sendMsgState = "read";
                }
                messageStorage.insertMsg(messageObject);
                sendChatStates(messageObject.getMsg_id(), sendMsgState, messageObject.getSenderJid());
            }
        }
    };

    private void sendChatStates(String msgId, String state, String toJid) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }

        if (TextUtils.isEmpty(msgId) || TextUtils.isEmpty(toJid)) {
            return;
        }

        Message message = new Message(toJid, Type.chat);
        message.setPacketID(msgId);
        message.setFrom(xmppConnection.getUser());

        ChatStateExtension chatStateExtension = new ChatStateExtension(state);
        message.addExtension(chatStateExtension);

        xmppConnection.sendPacket(message);
    }

    private MessageObject phareMessageFromPacket(Message message) {
        MessageObject messageObject = new MessageObject();
        messageObject.setMsg_id(message.getPacketID());
        messageObject.setSenderJid(XmppStringUtils.parseBareAddress(message.getFrom()));
        messageObject.setReceiverJid(XmppStringUtils.parseBareAddress(message.getTo()));
        messageObject.setMsg_type(message.getSubType());
        messageObject.setBody(message.getBody());
        Date date = message.getDate();
        if (date == null) {
            messageObject.setDate(System.currentTimeMillis());
        } else {
            messageObject.setDate(date.getTime());
        }
        messageObject.setSenderNickName(message.getSenderNickName());
        if (messageObject.getMsg_type() == MessageSubType.image
                || messageObject.getMsg_type() == MessageSubType.geoloc) {
            PacketExtension extension = message.getExtension(MessageHtmlProvider.elementName(),
                    MessageHtmlProvider.namespace());
            if (extension != null) {
                messageObject.setHtml(extension.toXML());
            }
        }
        if (xmppConnection.getBareJid().equals(messageObject.getSenderJid())) {
            messageObject.setOutgoing(true);
            messageObject.setMsg_status(MessageObjectStatus.SEND);
        } else {
            messageObject.setOutgoing(false);
            messageObject.setMsg_status(MessageObjectStatus.UNREAD);
        }
        return messageObject;
    }

    /**
     * 发送文本消息
     *
     * @param toJidString
     * @param fromNickName
     * @param text
     */
    public void sendTextMessage(String toJidString, String fromNickName, String text) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(toJidString)) {
            return;
        }
        Message message = new Message();
        message.setType(Type.chat);
        message.setBody(text);
        message.setSubType(MessageSubType.text);
        message.setTo(toJidString);
        message.setSenderNickName(fromNickName);
        message.setFrom(xmppConnection.getUser());
        xmppConnection.sendPacket(message);
        MessageObject messageObject = phareMessageFromPacket(message);
        if (messageObject != null) {
            messageStorage.insertMsg(messageObject);
        }
    }

    public void setMessageRead(String msg_id) {
        if (TextUtils.isEmpty(msg_id)) {
            return;
        }
        MessageObject targetMessage = messageStorage.getMsg(msg_id);
        if ((targetMessage != null) && (targetMessage.getMsg_status() != MessageObjectStatus.READ)) {
            targetMessage.setMsg_status(MessageObjectStatus.READ);
            messageStorage.updateMsg(targetMessage);
            sendChatStates(targetMessage.getMsg_id(), "read", targetMessage.getSenderJid());
        }
    }

    public String insertEmptyMsg(MessageSubType type, String toJidString, String fromNickName) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return null;
        }
        if (TextUtils.isEmpty(toJidString)) {
            return null;
        }
        String msgid = Packet.nextID();
        MessageObjectStatus status;
        if (type == MessageSubType.geoloc) {
            status = MessageObjectStatus.LOCATING;
        } else if (type == MessageSubType.image) {
            status = MessageObjectStatus.UPLOADING;
        } else {
            status = MessageObjectStatus.SENDING;
        }

        MessageObject messageObject = new MessageObject();
        messageObject.setDate(System.currentTimeMillis());
        messageObject.setMsg_id(msgid);
        messageObject.setMsg_type(type);
        messageObject.setMsg_status(status);
        messageObject.setReceiverJid(toJidString);
        messageObject.setSenderJid(xmppConnection.getBareJid());
        messageObject.setSenderNickName(fromNickName);
        messageObject.setOutgoing(true);

        messageStorage.insertMsg(messageObject);
        return msgid;
    }

    public String insertImageMsg(String toJidString, String fromNickName, String src, String thumb) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return null;
        }
        if (TextUtils.isEmpty(toJidString)) {
            return null;
        }
        String msgid = Packet.nextID();
        MessageObject messageObject = new MessageObject();
        messageObject.setDate(System.currentTimeMillis());
        messageObject.setMsg_id(msgid);
        messageObject.setMsg_type(MessageSubType.image);
        messageObject.setMsg_status(MessageObjectStatus.UPLOADING);
        messageObject.setReceiverJid(toJidString);
        messageObject.setSenderJid(xmppConnection.getBareJid());
        messageObject.setSenderNickName(fromNickName);
        messageObject.setOutgoing(true);

        MessageHtmlExtension imageExtension = new MessageHtmlExtension(MessageSubType.image, src, thumb);

        messageObject.setBody(thumb);
        messageObject.setHtml(imageExtension.toString());

        messageStorage.insertMsg(messageObject);
        return msgid;
    }

    public void updateImageMsg(String msgId, String src, String thumb) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }
        if (TextUtils.isEmpty(msgId)) {
            return;
        }

        MessageObject messageObject = messageStorage.getMsg(msgId);
        if (messageObject != null) {
            MessageHtmlExtension imageExtension = new MessageHtmlExtension(MessageSubType.image, src, thumb);
            messageObject.setBody(thumb);
            messageObject.setHtml(imageExtension.toString());
            messageStorage.updateMsg(messageObject);
        }
    }

    public void updateMsgStatus(String msgId, MessageObjectStatus status) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }
        if (TextUtils.isEmpty(msgId)) {
            return;
        }

        MessageObject messageObject = messageStorage.getMsg(msgId);
        if (messageObject != null) {
            messageObject.setMsg_status(status);
            messageStorage.updateMsg(messageObject);
        }
    }

    public void sendCompletedLocationMsg(String msgid, double lat, double lon) {
        MessageObject messageObject = messageStorage.getMsg(msgid);
        if (messageObject == null) {
            return;
        }

        Message message = new Message();
        message.setType(Type.chat);
        message.setBody(messageObject.getBody());
        message.setSubType(messageObject.getMsg_type());
        message.setTo(messageObject.getReceiverJid());
        message.setSenderNickName(messageObject.getSenderNickName());
        message.setFrom(xmppConnection.getUser());

        MessageHtmlExtension locationExtension = new MessageHtmlExtension(MessageSubType.geoloc, lat, lon);
        message.addExtension(locationExtension);

        xmppConnection.sendPacket(message);

        messageObject.setBody(lat + "," + lon);
        messageObject.setMsg_status(MessageObjectStatus.SEND);
        messageObject.setHtml(locationExtension.toString());
        messageObject.setDate(System.currentTimeMillis());
        messageStorage.updateMsg(messageObject);
    }

    public void sendCompletedImageMsg(String msgid, String src, String thumb) {
        MessageObject messageObject = messageStorage.getMsg(msgid);
        if (messageObject == null) {
            return;
        }

        Message message = new Message();
        message.setType(Type.chat);
        message.setBody(messageObject.getBody());
        message.setSubType(messageObject.getMsg_type());
        message.setTo(messageObject.getReceiverJid());
        message.setSenderNickName(messageObject.getSenderNickName());
        message.setFrom(xmppConnection.getUser());

        MessageHtmlExtension imageExtension = new MessageHtmlExtension(MessageSubType.image, src, thumb);
        message.addExtension(imageExtension);

        xmppConnection.sendPacket(message);

        messageObject.setBody(thumb);
        messageObject.setMsg_status(MessageObjectStatus.SEND);
        messageObject.setHtml(imageExtension.toString());
        messageObject.setDate(System.currentTimeMillis());
        messageStorage.updateMsg(messageObject);
    }

    public void deleteMessage(String msgId) {
        messageStorage.deleteMsg(msgId);
    }

    public void deleteAllMessage() {
        messageStorage.deleteAllMsg();
    }

    public void deleteMessage(MessageObject msg) {
        messageStorage.deleteChatMessage(msg);
    }

    public void deleteMessage(List<MessageObject> msgs) {
        for (int i = 0; i < msgs.size(); i++) {
            messageStorage.deleteChatMessage(msgs.get(i));
        }
    }

    public void correctMessagesStatus() {
        messageStorage.correctMessagesStatus();
    }


    //////
    // MessageFragment data
    public static List<MessageObject> getAllMessage(ContentResolver cr) {
        List<MessageObject> sender_array = getSenderMessageObjects(cr);
        List<MessageObject> receiver_array = getReceiverMessageObjects(cr);
        List<MessageObject> resultArray = mergeMessage(sender_array, receiver_array);
        return resultArray;
    }

    private static List<MessageObject> getReceiverMessageObjects(ContentResolver cr) {
        List<MessageObject> messages = new ArrayList<MessageObject>();
        Cursor cursor = cr.query(MessageDBStoreProvider.getContentUri(), null,
                Columns.MSG_RECEIVER_JID + "=? And " + Columns.MSG_IS_OUTGOING + "=0)"
                        + " group by " + " ( " + Columns.MSG_SENDER_JID, new String[]{
                        XMPPManager.getInstance().getXmppConnection().getBareJid()
                }, MessageStorage.Columns.MSG_DATE + " DESC");

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
                obj.setMsg_type(SubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(Msg_Status.values()[cursor.getInt(msgStatusIndex)]);
                obj.setDate(cursor.getLong(dateIndex));
                obj.setOutgoing(false);
                messages.add(obj);
            }
            cursor.close();
        }
        return messages;
    }

    private static List<MessageObject> getSenderMessageObjects(ContentResolver cr) {
        List<MessageObject> messages = new ArrayList<MessageObject>();
        Cursor cursor = cr.query(MessageDBStoreProvider.getContentUri(), null,
                Columns.MSG_SENDER_JID + "=? And " + Columns.MSG_IS_OUTGOING + "=1)" + " group by "
                        + " ( " + Columns.MSG_RECEIVER_JID, new String[]{
                        XMPPManager.getInstance().getXmppConnection().getBareJid()
                }, Columns.MSG_DATE + " DESC");

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
                obj.setMsg_type(SubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(Msg_Status.values()[cursor.getInt(msgStatusIndex)]);
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

    // ChatFragment data
    public static List<MessageObject> getMessages(ContentResolver cr, String chatJid) {
        List<MessageObject> resultArray = new ArrayList<MessageObject>();

        Cursor cursor = null;
        try {
            cursor = cr.query(MessageDBStoreProvider.getContentUri(), null, "("
                            + Columns.MSG_SENDER_JID + "=? And " + Columns.MSG_RECEIVER_JID + "=?) Or ("
                            + Columns.MSG_SENDER_JID + "=? And " + Columns.MSG_RECEIVER_JID + "=?)",
                    new String[]{
                            XMPPManager.getInstance().getXmppConnection().getBareJid(), chatJid,
                            chatJid, XMPPManager.getInstance().getXmppConnection().getBareJid()
                    }, Columns.MSG_DATE + " ASC");

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
                obj.setMsg_type(SubType.values()[cursor.getInt(msgTypeIndex)]);
                obj.setMsg_status(Msg_Status.values()[cursor.getInt(msgStatusIndex)]);
                obj.setDate(cursor.getLong(dateIndex));
                if (cursor.getInt(outgoingIndex) != 0) {
                    obj.setOutgoing(true);
                } else {
                    obj.setOutgoing(false);
                }
                resultArray.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return resultArray;
    }

    public static int getUnReadMsgCount(ContentResolver cr) {
        int count = 0;
        Cursor cursor = null;
        if (!XMPPManager.getInstance().isLogin()) {
            return 0;
        }
        String jid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        if (TextUtils.isEmpty(jid)) {
            return 0;
        }
        try {
            cursor = cr.query(MessageDBStoreProvider.getContentUri(), new String[]{
                            Columns.MSG_STATUS
                    }, Columns.MSG_RECEIVER_JID + "=? and " + Columns.MSG_STATUS + "=? and "
                            + Columns.MSG_IS_OUTGOING + "=?",

                    new String[]{

                            jid, String.valueOf(Msg_Status.UNREAD.ordinal()), String.valueOf(0)
                    }, null);
            if (null != cursor) {
                count = cursor.getCount();
            }
        } catch (Exception e) {
        }
        return count;
    }
    ////
}
