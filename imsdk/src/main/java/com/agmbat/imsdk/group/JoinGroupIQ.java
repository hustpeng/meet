package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class JoinGroupIQ extends IQ {

    private String mSenderName;

    public JoinGroupIQ(String senderName) {
        mSenderName = senderName;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + JoinGroupProvider.namespace() + "\" >");
        builder.append("<action>applycircle</action>");
        builder.append("<sendername>" + mSenderName + "</sendername>");
        builder.append("</query>");
        return builder.toString();
    }


}
