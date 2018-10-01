package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class ChangeGroupNicknameIQ extends IQ {

    private String mGroupNickname;

    public void setGroupNickname(String groupNickname) {
        mGroupNickname = groupNickname;
    }


    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + ChangeGroupNicknameProvider.namespace() + "\">");
        builder.append("<action>changenickname</actoin>");
        builder.append("<sendername>" + mGroupNickname + "</sendername>");
        builder.append("</query>");
        return builder.toString();
    }
}
