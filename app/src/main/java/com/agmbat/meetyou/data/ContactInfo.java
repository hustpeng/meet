package com.agmbat.meetyou.data;

/**
 * 联系人
 */
public class ContactInfo {

    private String mNickname;

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getDisplayName() {
        return mNickname;
    }


    public String getPersonalMsg() {
        return "getPersonalMsg";
    }
}
