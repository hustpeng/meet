
package org.jivesoftware.smackx.paid;

import org.jivesoftware.smack.packet.IQ;

/**
 * Represents XMPP roster packets.
 *
 * @author Matt Tucker
 */
public class PaidAccountPacket extends IQ {

    private PaidAccountObject object = null;

    @Override
    public String getChildElementXML() {
        // TODO Auto-generated method stub
        return null;
    }

    public PaidAccountObject getObject() {
        return object;
    }

    public void setObject(PaidAccountObject object) {
        this.object = object;
    }
}
