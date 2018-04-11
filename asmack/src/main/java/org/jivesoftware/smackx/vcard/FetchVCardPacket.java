package org.jivesoftware.smackx.vcard;

import org.jivesoftware.smack.packet.IQ;

public class FetchVCardPacket extends IQ {

    public FetchVCardPacket(String aJid) {
        setTo(aJid);
    }

    public String getChildElementXML() {
        return new StringBuffer()
                .append("<")
                .append(VCardProvider.elementName())
                .append(" xmlns=\"")
                .append(VCardProvider.namespace())
                .append("\"/>")
                .toString();
    }
}