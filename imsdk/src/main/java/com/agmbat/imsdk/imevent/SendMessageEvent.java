package com.agmbat.imsdk.imevent;

import org.jivesoftware.smackx.message.MessageObject;

public class SendMessageEvent {

    private MessageObject mMessageObject;

    public SendMessageEvent(MessageObject object) {
        mMessageObject = object;
    }

    public MessageObject getMessageObject() {
        return mMessageObject;
    }
}
