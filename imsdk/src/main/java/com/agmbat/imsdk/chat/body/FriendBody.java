package com.agmbat.imsdk.chat.body;

import com.agmbat.imsdk.asmack.roster.ContactInfo;

public class FriendBody extends Body {

    private final ContactInfo mContactInfo;

    public FriendBody(ContactInfo contactInfo) {
        mContactInfo = contactInfo;
    }

    public ContactInfo getContactInfo() {
        return mContactInfo;
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.FRIEND;
    }

    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<wrap>");
        builder.append("<type>").append(getBodyType()).append("</type>");
        builder.append("<friend_jid>").append(mContactInfo.getBareJid()).append("</friend_jid>");
        builder.append("</wrap>");
        return builder.toString();
    }
}
