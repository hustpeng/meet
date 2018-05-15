package com.agmbat.imsdk.imevent;

import org.jivesoftware.smackx.message.MessageObject;

public class ReceiveMessageEvent {

    private MessageObject mMessageObject;

    public ReceiveMessageEvent(MessageObject object) {
        mMessageObject = object;
    }

    public MessageObject getMessageObject() {
        return mMessageObject;
    }
}
