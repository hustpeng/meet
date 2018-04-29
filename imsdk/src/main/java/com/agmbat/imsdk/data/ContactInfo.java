package com.agmbat.imsdk.data;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 联系人
 */
@Table(name = "contact")
public class ContactInfo {


    /**
     * 此id为数据库存储id值
     */
    @Column(name = "id", isId = true)
    private long mId;

    /**
     * 联系人id标识
     */
    @Column(name = "jid")
    @SerializedName("jid")
    private String mBareJid = "";

    /**
     * 联系人昵称
     */
    @Column(name = "nickname")
    @SerializedName("nickname")
    private String mNickname;

    /**
     * 用户给好友的备注
     */
    @Column(name = "remark")
    private String mRemark;

    /**
     * 用户头像url
     */
    @Column(name = "avatar")
    @SerializedName("avatar_url")
    private String mAvatar;

    /**
     * 性别
     */
    @Column(name = "gender")
    @SerializedName("gender")
    private int mGender;


    /****** 附近的人需要的字段 start **************/

    @SerializedName("creation")
    private long creation;

    @SerializedName("auth_status")
    private int auth_status;

    @SerializedName("geo")
    private String geo;

    @SerializedName("last_logout")
    private long last_logout;

    @SerializedName("last_login")
    private long last_login;

    @SerializedName("age")
    private int age;

    @SerializedName("dist")
    private double dist;

    /****** 附近的人需要的字段 end   **************/

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

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    /**
     * 更新联想人信息
     *
     * @param contactInfo
     */
    public void apply(ContactInfo contactInfo) {
        mId = contactInfo.mId;
        mBareJid = contactInfo.mBareJid;
        mNickname = contactInfo.mNickname;
        mRemark = contactInfo.mRemark;
        mAvatar = contactInfo.mAvatar;
        mGender = contactInfo.mGender;
    }

    /**
     * 获取距离
     *
     * @return
     */
    public double getDist() {
        return dist;
    }
}
