package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class JoinGroupReply extends IQ {

    private boolean success;

    private boolean waitForAgree;

    public void setSuccess(boolean success){
        this.success = success;
    }

    public void setWaitForAgree(boolean waitForAgree){
        this.waitForAgree = waitForAgree;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isWaitForAgree() {
        return waitForAgree;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
