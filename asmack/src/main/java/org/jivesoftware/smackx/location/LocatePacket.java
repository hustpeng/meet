
package org.jivesoftware.smackx.location;

import org.jivesoftware.smack.packet.IQ;

public class LocatePacket extends IQ {

    private LocateObject object = null;

    @Override
    public String getChildElementXML() {
        // TODO Auto-generated method stub
        return null;
    }

    public LocateObject getObject() {
        return object;
    }

    public void setObject(LocateObject object) {
        this.object = object;
    }
}
