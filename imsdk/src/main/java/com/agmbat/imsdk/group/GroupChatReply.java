package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.message.MessageObject;

import java.util.ArrayList;
import java.util.List;

public class GroupChatReply extends IQ {

    private List<MessageObject> mMessages = new ArrayList<>();

    public void addMessage(MessageObject messageObject){
        mMessages.add(messageObject);
    }

    public List<MessageObject> getMessages(){
        return mMessages;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
