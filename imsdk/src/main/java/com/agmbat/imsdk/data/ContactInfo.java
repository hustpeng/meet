package com.agmbat.imsdk.data;

/**
 * 联系人
 */
public class ContactInfo {

    /**
     * 联系人id标识
     */
    private String mBareJid = "";

    /**
     * 联系人昵称
     */
    private String mNickname;

    /**
     * 用户给好友的备注
     */
    private String mRemark;


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

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }
}
