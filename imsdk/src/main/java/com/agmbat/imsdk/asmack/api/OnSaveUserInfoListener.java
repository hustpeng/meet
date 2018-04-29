package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.user.LoginUser;

/**
 * 保存当前用户信息
 */
public interface OnSaveUserInfoListener {

    public void onSaveUserInfo(LoginUser user);
}
