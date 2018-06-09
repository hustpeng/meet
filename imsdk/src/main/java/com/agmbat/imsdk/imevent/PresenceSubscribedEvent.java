package com.agmbat.imsdk.imevent;

import com.agmbat.imsdk.asmack.roster.ContactInfo;

/**
 * 其他人同意加自己为好友的事件
 */
public class PresenceSubscribedEvent {

    private ContactInfo mContactInfo;

    public PresenceSubscribedEvent(ContactInfo info) {
        mContactInfo = info;
    }

    public ContactInfo getContactInfo() {
        return mContactInfo;
    }
}
