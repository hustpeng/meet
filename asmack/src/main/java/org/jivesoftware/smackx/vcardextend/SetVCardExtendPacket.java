package org.jivesoftware.smackx.vcardextend;

import org.jivesoftware.smack.packet.IQ;

public class SetVCardExtendPacket extends IQ {

    private final VCardExtendObject vcardExtend;

    public SetVCardExtendPacket(VCardExtendObject newVCardExtend) {
        vcardExtend = newVCardExtend;
        setType(Type.SET);
    }

    public String getChildElementXML() {
        return VCardExtendObject.getXmlNode(vcardExtend);
    }
}