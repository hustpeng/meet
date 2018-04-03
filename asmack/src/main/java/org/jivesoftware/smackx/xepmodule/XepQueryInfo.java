package org.jivesoftware.smackx.xepmodule;

import org.jivesoftware.smack.PacketListener;

import java.util.Timer;

public class XepQueryInfo {

    private int queryType;
    private int timeout;
    private String param1;
    private String param2;
    private Object param3;
    private Timer timer;
    private PacketListener queryPacketListener;

    public XepQueryInfo(int aType) {
        this(aType, null, null);
    }

    public XepQueryInfo(int aType, String aParam1) {
        this(aType, aParam1, null, null);
    }

    public XepQueryInfo(int aType, String aParam1, String aParam2) {
        this(aType, aParam1, aParam2, null);
    }

    public XepQueryInfo(int aType, String aParam1, String aParam2, Object param3) {
        setQueryType(aType);
        setParam1(aParam1);
        setParam2(aParam2);
        setParam3(param3);
        setTimeout(30);
        setTimer(new Timer());
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public PacketListener getQueryPacketListener() {
        return queryPacketListener;
    }

    public void setQueryPacketListener(PacketListener queryPacketListener) {
        this.queryPacketListener = queryPacketListener;
    }

    public Object getParam3() {
        return param3;
    }

    public void setParam3(Object param3) {
        this.param3 = param3;
    }
}
