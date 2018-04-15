package com.agmbat.imsdk.account;

/**
 * 注册信息
 */
public class RegisterInfo {

    /**
     * 用户名, 也就是手机号
     */
    private String mUserName;

    /**
     * 密码
     */
    private String mPassword;

    /**
     * 验证码
     */
    private String mVerificationCode;

    /**
     * 昵称
     */
    private String mNickName;

    /**
     * 性别 1 表示男, 2表示女
     */
    private int mGender;


    /**
     * 出生年份
     */
    private int mBirthYear;

    /**
     * 邀请码
     */
    private String mInviteCode;

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getVerificationCode() {
        return mVerificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        mVerificationCode = verificationCode;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public int getGender() {
        return mGender;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setBirthYear(int year) {
        mBirthYear = year;
    }

    public int getBirthYear() {
        return mBirthYear;
    }

    public String getInviteCode() {
        return mInviteCode;
    }

    public void setInviteCode(String code) {
        mInviteCode = code;
    }
}
