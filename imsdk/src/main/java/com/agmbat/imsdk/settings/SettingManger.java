package com.agmbat.imsdk.settings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.agmbat.android.AppResources;

public class SettingManger {

    private static final String KEY_REMARK = "remark_";

    public static SettingManger sInstance = new SettingManger();
    private SharedPreferences prefs;

    private SettingManger() {
        prefs = PreferenceManager.getDefaultSharedPreferences(AppResources.getAppContext());
    }

    public static SettingManger getInstance() {
        return sInstance;
    }

    public String getUserRemark(String jid) {
        return prefs.getString(KEY_REMARK + jid, "");
    }

    public void setUserRemark(String jid, String remark) {
        prefs.edit().putString(KEY_REMARK + jid, remark).apply();
    }
}
