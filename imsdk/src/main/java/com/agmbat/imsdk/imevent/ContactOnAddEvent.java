package com.agmbat.imsdk.imevent;

/**
 * 验证添加联系人成功事件
 */
public class ContactOnAddEvent {
    private String mJid;

    public ContactOnAddEvent(String jid) {
        mJid = jid;
    }

    public String getJid() {
        return mJid;
    }
}
