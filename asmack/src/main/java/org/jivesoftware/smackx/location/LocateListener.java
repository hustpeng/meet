package org.jivesoftware.smackx.location;

public interface LocateListener {
    public void notifySetLocationResult(boolean result);

    public void notifyGetLocationResult(String jid, LocateObject location);
}