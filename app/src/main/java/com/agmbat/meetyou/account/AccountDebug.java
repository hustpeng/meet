package com.agmbat.meetyou.account;


import android.content.Context;
import android.content.SharedPreferences;

import com.agmbat.android.AppResources;

/**
 * 调试保存登录用户名
 */
public class AccountDebug {

    public static void saveAccount(String username, String password) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public static String getUserName() {
        return getSharedPreferences().getString("username", null);
    }

    public static String getPassword() {
        return getSharedPreferences().getString("password", null);
    }

    private static SharedPreferences getSharedPreferences() {
        return AppResources.getAppContext().getSharedPreferences("AccountDebug", Context.MODE_PRIVATE);
    }

}
