package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class KickMemberReply extends IQ {

    private String member;

    private String action;

    private String reason;

    private String sender;


    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
