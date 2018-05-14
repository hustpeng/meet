package org.jivesoftware.smackx.message;

import org.jivesoftware.smack.packet.PacketExtension;

public class ChatStateExtension implements PacketExtension {

    private String elementName;

    public ChatStateExtension(String aElementName) {
        elementName = aElementName;
    }

    @Override
    public String getElementName() {
        return elementName;
    }

    @Override
    public String getNamespace() {
        return "http://jabber.org/protocol/chatstates";
    }

    @Override
    public String toXML() {
        return new StringBuffer().append("<").append(getElementName()).append(" xmlns=\"")
                .append(getNamespace()).append("\"/>").toString();
    }

    @Override
    public String toString() {
        return toXML();
    }
}