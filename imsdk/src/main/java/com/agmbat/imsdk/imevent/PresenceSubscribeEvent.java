package com.agmbat.imsdk.imevent;

import com.agmbat.imsdk.asmack.roster.ContactInfo;

/**
 * 其他人申请添加自己为好友的事件
 */
public class PresenceSubscribeEvent {

    private ContactInfo mContactInfo;

    public PresenceSubscribeEvent(ContactInfo info) {
        mContactInfo = info;
    }

    public ContactInfo getContactInfo() {
        return mContactInfo;
    }
}
