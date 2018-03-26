package com.agmbat.imsdk.data;

/**
 * 联系人
 */
public class ContactInfo {

    /**
     * 联系人id标识
     */
    private String mBareJid = "";

    private String mNickname;

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getNickName() {
        return mNickname;
    }


    public String getPersonalMsg() {
        return "getPersonalMsg";
    }

    public ContactInfo(String jid) {
        mBareJid = jid;
    }

    public String getBareJid() {
        return mBareJid;
    }
}
