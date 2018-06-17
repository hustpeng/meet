package com.agmbat.imsdk.splash;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;

/**
 * Splash信息存储
 */
public class SplashStore {

    /**
     * 闪屏配置文件名
     */
    private static final String PREF_FILE = "splash";

    /**
     * 闪屛api版本 key
     */
    private static final String SPLASH_VERSION = "version";

    /**
     * 全局配置
     */
    private static final String GLOBAL_CONFIG_SENSITIVE_WORDS = "sensitiveWords";

    /**
     * Splash 信息
     */
    private static final String KEY_SPLASH_INFO = "splash_info";

    /**
     * 获取splash version
     *
     * @return
     */
    public static int getSplashVersion() {
        return getSplashPrefs().getInt(SPLASH_VERSION, 0);
    }

    /**
     * 保存版本号
     *
     * @param version
     */
    public static void setSplashVersion(int version) {
        getSplashPrefs().edit().putInt(SPLASH_VERSION, version).commit();
    }

    /**
     * 保存敏感词
     *
     * @param words
     */
    public static void saveSensitiveWords(String words) {
        if (words != null) {
            getSplashPrefs().edit().putString(GLOBAL_CONFIG_SENSITIVE_WORDS, words).commit();
        }
    }

    /**
     * 保存Splash info
     *
     * @param splashInfo
     */
    public static void saveSplashInfo(SplashInfo splashInfo) {
        if (splashInfo != null) {
            // 转化为毫秒
            splashInfo.displayTime = splashInfo.displayTime * 1000;
            String text = GsonHelper.toJson(splashInfo);
            getSplashPrefs().edit().putString(KEY_SPLASH_INFO, text).commit();
        }
    }

    /**
     * 获取Splash信息
     *
     * @return
     */
    public static SplashInfo getSplashInfo() {
        String text = getSplashPrefs().getString(KEY_SPLASH_INFO, null);
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        return GsonHelper.fromJson(text, SplashInfo.class);
    }

    /**
     * 获取敏感永词
     *
     * @return
     */
    public static String getSensitiveWords() {
        return getSplashPrefs().getString(GLOBAL_CONFIG_SENSITIVE_WORDS, null);
    }

    private static SharedPreferences getSplashPrefs() {
        return AppResources.getAppContext().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

}
