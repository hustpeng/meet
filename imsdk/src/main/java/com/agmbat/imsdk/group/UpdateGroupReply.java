package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class UpdateGroupReply extends IQ {

    private boolean success ;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"http://jabber.org/protocol/muc#owner\">");
        builder.append("</query>");
        return null;
    }
}
