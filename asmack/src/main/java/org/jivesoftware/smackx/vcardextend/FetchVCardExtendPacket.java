package org.jivesoftware.smackx.vcardextend;

import org.jivesoftware.smack.packet.IQ;

public class FetchVCardExtendPacket extends IQ {

    public FetchVCardExtendPacket(String aJid) {
        setTo(aJid);
    }

    @Override
    public String getChildElementXML() {
        return new StringBuffer()
                .append("<")
                .append(VCardExtendProvider.elementName())
                .append(" xmlns=\"")
                .append(VCardExtendProvider.namespace())
                .append("\"/>")
                .toString();
    }
}