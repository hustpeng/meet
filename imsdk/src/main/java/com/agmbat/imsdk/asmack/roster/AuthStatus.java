package com.agmbat.imsdk.asmack.roster;

public interface AuthStatus {

    public static final int AUTH_STATE_NOT_SUBMIT = -1; //未提交认证资料
    public static final int AUTH_STATE_SUBMITED = 0; //已提交认证资料待审核
    public static final int AUTH_STATE_AUTHENTICATED = 1; //已认证会员
    public static final int AUTH_STATE_DENIED = 2; //认证未通过（即被拒绝）
    public static final int AUTH_STATE_SENIOR = 3; //高级会员

}
