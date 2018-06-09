package com.agmbat.imsdk.imevent;

/**
 * 删除联系人事件
 */
public class ContactDeleteEvent {

    public String mJid;

    public ContactDeleteEvent(String jid) {
        mJid = jid;
    }

    public String getJid() {
        return mJid;
    }
}
