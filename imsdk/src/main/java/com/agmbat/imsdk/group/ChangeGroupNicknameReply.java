package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class ChangeGroupNicknameReply extends IQ {

    private String mGroupNickname;
    private boolean success;

    public void setGroupNickname(String groupNickname) {
        mGroupNickname = groupNickname;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + ChangeGroupNicknameProvider.namespace() + "\">");
        builder.append("<action>changenickname</actoin>");
        builder.append("<succeed>" + success + "</succeed>");
        builder.append("<sendername>" + mGroupNickname + "</sendername>");
        builder.append("</query>");
        return builder.toString();
    }
}
