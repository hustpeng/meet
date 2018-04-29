package com.agmbat.imsdk.imevent;

import com.agmbat.imsdk.user.LoginUser;

/**
 * 登录用户事件更新
 */
public class LoginUserUpdateEvent {

    private final LoginUser mUser;

    public LoginUserUpdateEvent(LoginUser user) {
        mUser = user;
    }

    public LoginUser getLoginUser() {
        return mUser;
    }

}
