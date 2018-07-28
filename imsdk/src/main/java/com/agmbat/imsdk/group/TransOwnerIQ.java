package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class TransOwnerIQ extends IQ {

    private String newOwner;

    public TransOwnerIQ(String groupJid, String newOwner) {
        this.newOwner = newOwner;
        setTo(groupJid);
        setType(Type.SET);
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"http://jabber.org/protocol/muc#admin\" >");
        builder.append("<action>transowner</action>");
        builder.append("<member>" + newOwner + "</member>");
        builder.append("</query>");
        return builder.toString();
    }


}
