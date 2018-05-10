package com.agmbat.imsdk;

import com.agmbat.imsdk.user.UserManager;

/**
 * 对外暴露统一的接口
 */
public class IM {

    public static final IM INSTANCE = new IM();

    public static IM get() {
        return INSTANCE;
    }

    private static final String TAG = "IM";

    public void init() {
        // 初始化
        UserManager.getInstance();
    }


}
