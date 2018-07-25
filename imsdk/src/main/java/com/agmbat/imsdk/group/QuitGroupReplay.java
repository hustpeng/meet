package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class QuitGroupReplay extends IQ {

    private boolean success;

    private String reason;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
