package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.user.LoginUser;

/**
 * 获取登陆用户信息
 */
public interface OnFetchLoginUserListener {

    /**
     * 获取登陆用户信息
     *
     * @param user
     */
    public void onFetchLoginUser(LoginUser user);
}