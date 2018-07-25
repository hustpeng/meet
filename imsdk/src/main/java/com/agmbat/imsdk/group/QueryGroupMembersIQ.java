package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class QueryGroupMembersIQ extends IQ {

    public QueryGroupMembersIQ(String groupJid){
        setType(Type.GET);
        setTo(groupJid);
    }

    @Override
    public String getChildElementXML() {
        return "<query xmlns=\"" + QueryGroupMembersIQProvider.namespace() + "\"/>";
    }
}
