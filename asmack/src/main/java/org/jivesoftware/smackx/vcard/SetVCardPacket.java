package org.jivesoftware.smackx.vcard;

import org.jivesoftware.smack.packet.IQ;

public class SetVCardPacket extends IQ {

    private final VCardObject vcard;

    public SetVCardPacket(VCardObject newVCard) {
        vcard = newVCard;
        setType(Type.SET);
    }

    public String getChildElementXML() {
        return VCardObject.getXmlNode(vcard);
    }
}