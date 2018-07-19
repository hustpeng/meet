package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class QueryGroupIQ extends IQ {

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + QueryGroupIQProvider.namespace() + "\"/>");
        return builder.toString();
    }

}
