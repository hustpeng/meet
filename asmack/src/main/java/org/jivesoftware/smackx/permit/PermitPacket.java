package org.jivesoftware.smackx.permit;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;

public class PermitPacket extends IQ {
    private final ArrayList<PermitObject> permitItems = new ArrayList<PermitObject>();

    private PermitObject mObject = null;

    public void AddPermitItem(PermitObject item) {
        synchronized (permitItems) {
            permitItems.add(item);
        }
    }

    public ArrayList<PermitObject> getPermitItems() {
        synchronized (permitItems) {
            return permitItems;
        }
    }

    @Override
    public String getChildElementXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<");
        buffer.append(PermitProvider.elementName());
        buffer.append(" xmlns=\"");
        buffer.append(PermitProvider.namespace());
        buffer.append("\"/>");
        return buffer.toString();
    }

    public PermitObject getObj() {
        return mObject;
    }

    public void setObj(PermitObject object) {
        mObject = object;
    }
}
