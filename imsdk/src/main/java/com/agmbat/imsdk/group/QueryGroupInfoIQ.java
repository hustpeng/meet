package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class QueryGroupInfoIQ extends IQ {

    public QueryGroupInfoIQ(String groupJid) {
        setTo(groupJid);
        setType(Type.GET);
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + QueryGroupIQProvider.namespace() + "\"/>");
        return builder.toString();
    }

    @Override
    public String getXmlns() {
        return super.getXmlns();
    }
}
