package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class QueryGroupChatIQ extends IQ {

    private int limitNumber = 100;
    private long since;

    public int getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }

    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + GroupChatIQProvider.namespace() + "\">");
        builder.append("<history maxstanzas=\"" + limitNumber + "\"");
        if (since > 0) {
            builder.append(" since=\"" + since + "\"");
        }
        builder.append("/>");
        builder.append("</query>");
        return builder.toString();
    }
}
