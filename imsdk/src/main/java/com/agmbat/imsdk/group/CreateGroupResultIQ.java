package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class CreateGroupResultIQ extends IQ {

    private String groupJid;

    public String getGroupJid() {
        return groupJid;
    }

    public void setGroupJid(String groupJid) {
        this.groupJid = groupJid;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"http://jabber.org/protocol/muc#owner\">");
        builder.append("<circle jid=\"" + groupJid + "\"></circle>");
        builder.append("</query>");
        return builder.toString();
    }

}
