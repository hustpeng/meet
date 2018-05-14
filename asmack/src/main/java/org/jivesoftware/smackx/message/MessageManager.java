package org.jivesoftware.smackx.message;

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

}
