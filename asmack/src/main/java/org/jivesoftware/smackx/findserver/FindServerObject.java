package org.jivesoftware.smackx.findserver;

import org.jivesoftware.smackx.db.ICacheStoreObject;

public class FindServerObject implements ICacheStoreObject {

    private String tokenServer;
    private String paidServer;

    /**
     * 通过发现服务获取circle服务器地址
     */
    private String circleServer;

    @Override
    public String getKey() {
        return "";
    }

    public String getTokenServer() {
        return tokenServer;
    }

    public void setTokenServer(String tokenServer) {
        this.tokenServer = tokenServer;
    }

    public String getPaidServer() {
        return paidServer;
    }

    public void setPaidServer(String paidServer) {
        this.paidServer = paidServer;
    }

    public String getCircleServer() {
        return circleServer;
    }

    public void setCircleServer(String circleServer) {
        this.circleServer = circleServer;
    }

    @Override
    public String toString() {
        return "token server:" + tokenServer + " paid server:" + paidServer + " circle server:" + circleServer;
    }
}