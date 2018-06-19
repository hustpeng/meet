package com.agmbat.imsdk.imevent;

import org.jivesoftware.smackx.message.MessageObject;

/**
 * 收到系统消息事件
 */
public class ReceiveSysMessageEvent {

    private MessageObject mMessageObject;

    public ReceiveSysMessageEvent(MessageObject object) {
        mMessageObject = object;
    }

    public MessageObject getMessageObject() {
        return mMessageObject;
    }
}
