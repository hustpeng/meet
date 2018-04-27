package com.agmbat.imsdk.data;

import org.jivesoftware.smack.util.XmppStringUtils;

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

    /**
     * 用户头像url
     */
    private String mAvatar;

    /**
     * 性别
     */
    private int mGender;

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getNickName() {
        return mNickname;
    }

    public String getPersonalMsg() {
        return "getPersonalMsg";
    }

    public void setBareJid(String jid) {
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

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public int getGender() {
        return mGender;
    }

    /**
     * 获取用户名称,登录帐号名称
     *
     * @return
     */
    public String getUserName() {
        return XmppStringUtils.parseName(mBareJid);
    }
}
