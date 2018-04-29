package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.user.LoginUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现异步数据获取, 用于同步交互线程所需要的信息
 */
public class XMPPApi {

    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * 获取联系人信息
     *
     * @param jid
     * @param l
     */
    public static void fetchContactInfo(String jid, OnFetchContactListener l) {
        Runnable runnable = new FetchContactInfoRunnable(jid, l);
        SINGLE_THREAD_EXECUTOR.execute(runnable);
    }

    /**
     * 拉取登陆用户信息
     *
     * @param jid
     * @param l
     */
    public static void fetchLoginUser(String jid, OnFetchLoginUserListener l) {
        Runnable runnable = new FetchLoginUserRunnable(jid, l);
        SINGLE_THREAD_EXECUTOR.execute(runnable);
    }

    /**
     * 保存当前用户信息
     *
     * @param user
     */
    public static void saveUserInfo(LoginUser user, OnSaveUserInfoListener l) {
        Runnable runnable = new SaveUserInfoRunnable(user, l);
        SINGLE_THREAD_EXECUTOR.execute(runnable);
    }


}

