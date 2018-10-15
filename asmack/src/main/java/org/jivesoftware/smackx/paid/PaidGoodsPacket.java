package org.jivesoftware.smackx.paid;

import org.jivesoftware.smack.packet.IQ;

/**
 * Represents XMPP roster packets.
 *
 * @author Matt Tucker
 */
public class PaidGoodsPacket extends IQ {

    private PaidGoodsInfoObject object = null;

    @Override
    public String getChildElementXML() {
        // TODO Auto-generated method stub
        return null;
    }

    public PaidGoodsInfoObject getObject() {
        return object;
    }

    public void setObject(PaidGoodsInfoObject object) {
        this.object = object;
    }

}
