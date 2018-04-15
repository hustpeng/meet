
package org.jivesoftware.smackx.token;

import org.jivesoftware.smack.packet.IQ;

public class TokenPacket extends IQ {

    private TokenObject object = null;

    @Override
    public String getChildElementXML() {
        return null;
    }

    public TokenObject getObject() {
        return object;
    }

    public void setObject(TokenObject object) {
        this.object = object;
    }
}
