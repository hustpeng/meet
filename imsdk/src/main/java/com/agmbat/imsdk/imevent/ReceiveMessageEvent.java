package com.agmbat.imsdk.imevent;

import org.jivesoftware.smackx.message.MessageObject;

/**
 * 收到聊天消息事件
 */
public class ReceiveMessageEvent {

    private MessageObject mMessageObject;

    public ReceiveMessageEvent(MessageObject object) {
        mMessageObject = object;
    }

    public MessageObject getMessageObject() {
        return mMessageObject;
    }
}
