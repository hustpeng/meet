package com.agmbat.imsdk.asmack;

import android.text.TextUtils;

import com.agmbat.android.utils.UiUtils;
import com.agmbat.imsdk.asmack.api.FetchContactInfoRunnable;
import com.agmbat.imsdk.asmack.api.OnFetchContactListener;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.imevent.ReceiveMessageEvent;
import com.agmbat.imsdk.imevent.ReceiveSysMessageEvent;
import com.agmbat.log.Debug;
import com.agmbat.log.Log;

import org.greenrobot.eventbus.EventBus;
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
import org.jivesoftware.smackx.message.ChatStateExtension;
import org.jivesoftware.smackx.message.MessageHtmlExtension;
import org.jivesoftware.smackx.message.MessageHtmlProvider;
import org.jivesoftware.smackx.message.MessageListener;
import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageObjectStatus;
import org.jivesoftware.smackx.message.MessageStorage;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageManager extends Xepmodule {

    private final List<MessageListener> listeners;

    private MessageStorage messageStorage;

    /**
     * 内存中消息列表
     */
    private Map<String, List<MessageObject>> mMessageMap = new HashMap<>();

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

    private PacketFilter messagePacketFilter = new PacketFilter() {
        public boolean accept(Packet packet) {
            if (!(packet instanceof Message)) {
                return false;
            }
            Type messageType = ((Message) packet).getType();
            // the Type.normal msg is filter in VisitorManager.java
            return messageType == Type.chat || messageType == Type.groupchat/*
             * || messageType ==
             * Message.Type.normal
             */;
        }
    };
    private PacketListener messagePacketListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            Log.d("SMACK: receive message: " + message.getBody());
            if (("http://jabber.org/protocol/muc#verify".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#admin".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#hotcircle".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#owner".equals(message.getXmlns()))) {
                return;
            }

            final MessageObject messageObject = phareMessageFromPacket(message);
            if (messageObject == null) {
                return;
            }
            if (TextUtils.isEmpty(messageObject.getBody())) {
                PacketExtension extension = message
                        .getExtension("http://jabber.org/protocol/chatstates");
                if (extension != null) {
                    String elementName = extension.getElementName();
                    if ("delivered".equals(elementName)) {
                        MessageObject targetMessage = messageStorage.getMsg(messageObject
                                .getMsgId());
                        if (targetMessage != null) {
                            targetMessage.setMsgStatus(MessageObjectStatus.UNREAD);
                            messageStorage.updateMsg(targetMessage);
                        }
                    } else if ("read".equals(elementName)) {
                        MessageObject targetMessage = messageStorage.getMsg(messageObject
                                .getMsgId());
                        if (targetMessage != null) {
                            targetMessage.setMsgStatus(MessageObjectStatus.READ);
                            messageStorage.updateMsg(targetMessage);
                        }
                    } else if ("composing".equals(elementName)) {
                    }
                }
                return;
            }

            String sendMsgState = "delivered";
            if (willInsertReceivedMsg(messageObject)) {
                messageObject.setMsgStatus(MessageObjectStatus.READ);
                sendMsgState = "read";
            }
            messageStorage.insertMsg(messageObject);
            UiUtils.post(new Runnable() {
                @Override
                public void run() {
                    addMessage(messageObject.getFromJid(), messageObject);
                    if (isSystemMessage(messageObject)) {
                        EventBus.getDefault().post(new ReceiveSysMessageEvent(messageObject));
                    } else {
                        EventBus.getDefault().post(new ReceiveMessageEvent(messageObject));
                    }
                }
            });
            sendChatStates(messageObject.getMsgId(), sendMsgState, messageObject.getFromJid());
        }
    };

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
        messageObject.setMsgId(message.getPacketID());
        messageObject.setFromJid(XmppStringUtils.parseBareAddress(message.getFrom()));
        messageObject.setToJid(XmppStringUtils.parseBareAddress(message.getTo()));
        messageObject.setMsgType(message.getSubType());
        messageObject.setBody(message.getBody());
        Date date = message.getDate();
        if (date == null) {
            messageObject.setDate(System.currentTimeMillis());
        } else {
            messageObject.setDate(date.getTime());
        }
        messageObject.setChatType(message.getType());
        if(message.getType() == Type.groupchat) {
            messageObject.setSenderJid(XmppStringUtils.parseBareAddress(message.getSenderJid()));
        }else {
            messageObject.setSenderJid(XmppStringUtils.parseBareAddress(message.getFrom()));
        }
        messageObject.setSenderNickName(message.getSenderNickName());
        messageObject.setSenderAvatar(message.getSenderAvatar());

        if (messageObject.getMsgType() == MessageSubType.image
                || messageObject.getMsgType() == MessageSubType.geoloc) {
            PacketExtension extension = message.getExtension(MessageHtmlProvider.elementName(),
                    MessageHtmlProvider.namespace());
            if (extension != null) {
                messageObject.setHtml(extension.toXML());
            }
        }
        if (xmppConnection.getBareJid().equals(messageObject.getFromJid())) {
            messageObject.setOutgoing(true);
            messageObject.setMsgStatus(MessageObjectStatus.SEND);
        } else {
            messageObject.setOutgoing(false);
            messageObject.setMsgStatus(MessageObjectStatus.UNREAD);
        }
        return messageObject;
    }

    /**
     * 发送文本消息
     *
     * @param toJidString
     * @param senderNickName
     * @param text
     */
    public MessageObject sendTextMessage(Type chatType, String toJidString, String senderNickName, String senderAvatar, String text) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return null;
        }
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(toJidString)) {
            return null;
        }
        Message message = new Message();
        message.setType(chatType);
        message.setBody(text);
        message.setFrom(xmppConnection.getBareJid());
        message.setTo(toJidString);
        message.setSenderJid(xmppConnection.getBareJid());
        message.setSenderNickName(senderNickName);
        message.setSenderAvatar(senderAvatar);
        xmppConnection.sendPacket(message);
        MessageObject messageObject = phareMessageFromPacket(message);
        messageStorage.insertMsg(messageObject);
        addMessage(toJidString, messageObject);
        return messageObject;
    }


    public void setMessageRead(String msg_id) {
        if (TextUtils.isEmpty(msg_id)) {
            return;
        }
        MessageObject targetMessage = messageStorage.getMsg(msg_id);
        if ((targetMessage != null) && (targetMessage.getMsgStatus() != MessageObjectStatus.READ)) {
            targetMessage.setMsgStatus(MessageObjectStatus.READ);
            messageStorage.updateMsg(targetMessage);
            sendChatStates(targetMessage.getMsgId(), "read", targetMessage.getFromJid());
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
        messageObject.setMsgId(msgid);
        messageObject.setMsgType(type);
        messageObject.setMsgStatus(status);
        messageObject.setToJid(toJidString);
        messageObject.setFromJid(xmppConnection.getBareJid());
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
        messageObject.setMsgId(msgid);
        messageObject.setMsgType(MessageSubType.image);
        messageObject.setMsgStatus(MessageObjectStatus.UPLOADING);
        messageObject.setToJid(toJidString);
        messageObject.setFromJid(xmppConnection.getBareJid());
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
            messageObject.setMsgStatus(status);
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
        message.setSubType(messageObject.getMsgType());
        message.setTo(messageObject.getToJid());
        message.setSenderNickName(messageObject.getSenderNickName());
        message.setFrom(xmppConnection.getUser());

        MessageHtmlExtension locationExtension = new MessageHtmlExtension(MessageSubType.geoloc, lat, lon);
        message.addExtension(locationExtension);

        xmppConnection.sendPacket(message);

        messageObject.setBody(lat + "," + lon);
        messageObject.setMsgStatus(MessageObjectStatus.SEND);
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
        message.setSubType(messageObject.getMsgType());
        message.setTo(messageObject.getToJid());
        message.setSenderNickName(messageObject.getSenderNickName());
        message.setFrom(xmppConnection.getUser());

        MessageHtmlExtension imageExtension = new MessageHtmlExtension(MessageSubType.image, src, thumb);
        message.addExtension(imageExtension);

        xmppConnection.sendPacket(message);

        messageObject.setBody(thumb);
        messageObject.setMsgStatus(MessageObjectStatus.SEND);
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


    public void deleteMessage(List<MessageObject> msgs) {
        for (int i = 0; i < msgs.size(); i++) {
            messageStorage.deleteChatMessage(msgs.get(i));
        }
    }

    public void correctMessagesStatus() {
        messageStorage.correctMessagesStatus();
    }

    /**
     * 获取所有聊天信息
     *
     * @param jid
     * @return
     */
    public List<MessageObject> getAllMessage(String jid) {
        List<MessageObject> list = messageStorage.getAllMessage(jid);
        return list;
    }

    /**
     * 获取最近的所有聊天信息, 过虑掉不在用户列表中的聊天记录
     *
     * @param jid
     * @return
     */
    public List<MessageObject> getRecentMessage(String jid) {
        List<MessageObject> retList = new ArrayList<>();
        List<MessageObject> list = messageStorage.getAllMessage(jid);
        // 检测所有消息中用户是否存在, 如果不存在, 则不显示
        for (MessageObject messageObject : list) {
            // 如果用户信息不存在, 则不显示此消息记录, 等同步服务器上的用户信息后, 可直接删除相关信息
            if (isToOthers(messageObject)) {
                String receiverJid = messageObject.getToJid();
                // 如果数据库中没有此用户则直接删除与此用户相关的所有聊天记录
                ContactInfo info = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(receiverJid);
                if (info != null) {
                    retList.add(messageObject);
                } else {
                    Debug.print("not found message:" + receiverJid);
                    Debug.printStackTrace();
                }
            } else {
                String senderJid = messageObject.getFromJid();
                // 如果数据库中没有此用户则直接删除与此用户相关的所有聊天记录
                ContactInfo info = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(senderJid);
                if (info != null) {
                    retList.add(messageObject);
                } else {
                    Debug.print("not found message:" + senderJid);
                    Debug.printStackTrace();
                }
            }
        }
        return retList;

    }


    /**
     * 查询与指定人对话的聊天记录
     *
     * @param jid
     * @return
     */
    public List<MessageObject> getMessageList(String jid) {
        List<MessageObject> list = mMessageMap.get(jid);
        if (list == null) {
            String user = XmppStringUtils.parseBareAddress(xmppConnection.getUser());
            list = messageStorage.getMessages(user, jid);
            mMessageMap.put(jid, list);
        }
        return list;
    }

    /**
     * 添加消息
     *
     * @param jid
     * @param messageObject
     */
    private void addMessage(String jid, MessageObject messageObject) {
        List<MessageObject> list = mMessageMap.get(jid);
        if (list == null) {
            String user = XmppStringUtils.parseBareAddress(xmppConnection.getUser());
            list = messageStorage.getMessages(user, jid);
            mMessageMap.put(jid, list);
        }
        list.add(messageObject);
    }

    /**
     * 移除内存中消息记录
     *
     * @param jid
     */
    private void removeMessage(String jid) {
        mMessageMap.remove(jid);
    }


    /**
     * 获取对话的人
     *
     * @param messageObject
     * @return
     */
    public static ContactInfo getTalkContactInfo(MessageObject messageObject) {
        String jid = getTalkJid(messageObject);
        return XMPPManager.getInstance().getRosterManager().getContactFromMemCache(jid);
    }


    /**
     * 判断两个消息是否为同一个对话者
     *
     * @param m1
     * @param m2
     * @return
     */
    public static boolean isSameTalk(MessageObject m1, MessageObject m2) {
        String talk1 = getTalkJid(m1);
        String talk2 = getTalkJid(m2);
        return talk1.equals(talk2);
    }

    /**
     * 删除两个的聊天记录
     *
     * @param msg
     */
    public void deleteMessage(MessageObject msg) {
        String talkJid = getTalkJid(msg);
        removeMessage(talkJid);
        messageStorage.deleteChatMessage(msg);
    }

    /**
     * 删除两个的聊天记录
     *
     * @param loginUserId
     * @param bareJid
     */
    public void deleteMessage(String loginUserId, String bareJid) {
        removeMessage(bareJid);
        messageStorage.deleteChatMessage(loginUserId, bareJid);
    }

    /**
     * 获取对话者的jid
     *
     * @param messageObject
     * @return
     */
    public static String getTalkJid(MessageObject messageObject) {
        String jid = null;
        if (isToOthers(messageObject)) {
            jid = messageObject.getToJid();
        } else {
            jid = messageObject.getFromJid();
        }
        return jid;
    }

    /**
     * 判断消息是否是当前用户发送的
     *
     * @param messageObject
     * @return
     */
    public static boolean isToOthers(MessageObject messageObject) {
        String loginUserJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        return messageObject.getFromJid().equals(loginUserJid);
    }

    /**
     * 是否为系统消息
     *
     * @return
     */
    public static boolean isSystemMessage(MessageObject messageObject) {
        String jid = getTalkJid(messageObject);
        String userName = XmppStringUtils.parseName(jid);
        return ("support".equals(userName) || "system".equals(userName));
    }

    /**
     * 保证所有聊天中的用户存在
     *
     * @param list
     */
    @Deprecated
    private void ensureMessageList(List<MessageObject> list) {
        // 检测所有消息中用户是否存在, 如果不存在, 则不显示
        for (MessageObject messageObject : list) {
            // 如果用户信息不存在, 则不显示此消息记录, 等同步服务器上的用户信息后, 可直接删除相关信息
            if (isToOthers(messageObject)) {
                String receiverJid = messageObject.getToJid();
                // 如果数据库中没有此用户则直接删除与此用户相关的所有聊天记录
                ensureUser(receiverJid);
            } else {
                String senderJid = messageObject.getFromJid();
                ensureUser(senderJid);
            }
        }
    }

    /**
     * 确保用户信息存在中
     *
     * @param messageObject
     */
    @Deprecated
    private void ensureMessageUser(MessageObject messageObject) {
        // 如果用户信息不存在, 则不显示此消息记录, 等同步服务器上的用户信息后, 可直接删除相关信息
        if (isToOthers(messageObject)) {
            String receiverJid = messageObject.getToJid();
            // 如果数据库中没有此用户则直接删除与此用户相关的所有聊天记录
            ensureUser(receiverJid);
        } else {
            String senderJid = messageObject.getFromJid();
            ensureUser(senderJid);
        }
    }

    /**
     * 加载指定用户并保存到缓存中
     *
     * @param jid
     */
    @Deprecated
    private void ensureUser(String jid) {
        ContactInfo contactInfo = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(jid);
        if (contactInfo == null) {
            OnFetchContactListener listener = new OnFetchContactListener() {
                @Override
                public void onFetchContactInfo(ContactInfo contactInfo) {
                    XMPPManager.getInstance().getRosterManager().addContactToMemCache(contactInfo);
                }
            };
            FetchContactInfoRunnable runnable = new FetchContactInfoRunnable(jid, listener);
            runnable.run();
        }
    }


}
