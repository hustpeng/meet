package org.jivesoftware.smackx.paid;

import org.jivesoftware.smack.packet.IQ;

/**
 * Represents XMPP roster packets.
 *
 * @author Matt Tucker
 */
public class PaidPageInfoPacket extends IQ {

    private PaidPageInfoObject object = null;

    @Override
    public String getChildElementXML() {
        return null;
    }

    public PaidPageInfoObject getObject() {
        return object;
    }

    public void setObject(PaidPageInfoObject object) {
        this.object = object;
    }


}
