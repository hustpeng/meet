package com.agmbat.imsdk.account;

import android.content.Context;
import android.content.SharedPreferences;

import com.agmbat.android.AppResources;

/**
 * 调试保存登录用户名
 */
public class AccountPrefs {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    /**
     * 最后一次登陆成功的用户名
     */
    private static final String KEY_LAST_USERNAME = "last_username";

    /**
     * 保存登陆用户信息
     *
     * @param username
     * @param password
     */
    public static void saveAccount(String username, String password) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_LAST_USERNAME, username);
        editor.commit();
    }

    /**
     * 清除登陆的用户信息
     */
    public static void clearAccount() {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.commit();
    }

    /**
     * 获取登陆用户名
     *
     * @return
     */
    public static String getUserName() {
        return getSharedPreferences().getString(KEY_USERNAME, null);
    }

    public static String getPassword() {
        return getSharedPreferences().getString(KEY_PASSWORD, null);
    }

    private static SharedPreferences getSharedPreferences() {
        return AppResources.getAppContext().getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE);
    }

    /**
     * 获取最后一次登陆成功的用户名, 用户api参数
     *
     * @return
     */
    public static String getLastLoginUserName() {
        return getSharedPreferences().getString(KEY_LAST_USERNAME, null);
    }

}
