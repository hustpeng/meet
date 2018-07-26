package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class TransOwnerReply extends IQ {

    private String newOwner;

    private String action;

    public String getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(String newOwner) {
        this.newOwner = newOwner;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
