package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class QueryGroupFormIQ extends IQ {

    public QueryGroupFormIQ(String groupJid) {
        setType(Type.GET);
        setTo(groupJid);
    }

    @Override
    public String getChildElementXML() {
        return "<query xml:lang=\"en\" xmlns=\"" + GroupFormIQProvider.namespace() + "\"/>";
    }
}
