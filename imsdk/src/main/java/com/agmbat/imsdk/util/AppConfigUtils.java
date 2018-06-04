package com.agmbat.imsdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.agmbat.imsdk.R;
import com.agmbat.log.Log;

public class AppConfigUtils {

    private static final String TAG = AppConfigUtils.class.getSimpleName();

    private static final String KEY_APK_RUN_COUNT = "KEY_APK_RUN_COUNT";

    private static final int DEFAULT_APK_RUN_COUNT = 0;

    private static final String KEY_SCREEN_DISPLAY_WIDTH = "KEY_SCREEN_DISPLAY_WIDTH";

    private static final int DEFAULT_SCREEN_DISPLAY_WIDTH = 0;

    private static final String KEY_SCREEN_DISPLAY_HEIGHT = "KEY_SCREEN_DISPLAY_HEIGHT";

    private static final int DEFAULT_SCREEN_DISPLAY_HEIGHT = 0;

    private static final String KEY_IS_ENABLE_SOUND = "KEY_IS_ENABLE_SOUND";

    private static final boolean DEFAULT_IS_ENABLE_SOUND = true;

    private static final String KEY_IS_ENABLE_VIBRATE = "KEY_IS_ENABLE_VIBRATE";

    private static final boolean DEFAULT_IS_ENABLE_VIBRATE = true;

    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";
    private static final String KEY_FB_UID = "KEY_FACEBOOK_UID";
    private static final String KEY_EMAIL = "KEY_EMAIL";

    private static final String KEY_FILTER_START_AGE = "KEY_FILTER_START_AGE";
    private static final String KEY_FILTER_END_AGE = "KEY_FILTER_END_AGE";
    private static final String KEY_FILTER_ETHNICITY = "KEY_FILTER_ETHNICITY";

    private static SharedPreferences getPreferences(Context context) {
        if (context == null) {
            Log.e(TAG, "getPreferences ERR. context is nil");
            return null;
        }
        return context.getSharedPreferences(context.getResources().getString(R.string.app_name),
                Context.MODE_PRIVATE);
    }

    /**
     * @param context
     * @param apkruncount
     * @return
     */
    public static boolean setApkRunCount(Context context, int apkruncount) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt(KEY_APK_RUN_COUNT, apkruncount).commit();
        return true;

    }

    /**
     * 程序首次运行时,返回0
     *
     * @param context
     * @return
     */
    public static int getApkRunCount(Context context) {
        SharedPreferences preferences = getPreferences(context);
        int count = preferences.getInt(KEY_APK_RUN_COUNT, DEFAULT_APK_RUN_COUNT);
        return count;
    }

    /**
     * 程序首次运行时，将手机屏幕宽度存储</br> 详细说明：{@link setScreenDisplayHeight}
     *
     * @param context
     * @param width
     * @return
     */
    public static boolean setScreenDisplayWidth(Context context, int width) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt(KEY_SCREEN_DISPLAY_WIDTH, width).commit();
        return true;
    }

    public static int getScreenDisplayWidth(Context context) {
        SharedPreferences preferences = getPreferences(context);
        int width = preferences.getInt(KEY_SCREEN_DISPLAY_WIDTH, DEFAULT_SCREEN_DISPLAY_WIDTH);
        return width;
    }

    /**
     * 程序首次运行时，将屏幕高度存储</br> 此高度，并不包括状态栏高度</br> 此高度用作Profile界面中动态设置ScrollView的高度
     *
     * @param context
     * @param height
     * @return
     */
    public static boolean setScreenDisplayHeight(Context context, int height) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt(KEY_SCREEN_DISPLAY_HEIGHT, height).commit();
        return true;
    }

    public static int getScreenDisplayHeight(Context context) {
        SharedPreferences preferences = getPreferences(context);
        int height = preferences.getInt(KEY_SCREEN_DISPLAY_HEIGHT, DEFAULT_SCREEN_DISPLAY_HEIGHT);
        return height;
    }

    public static boolean setEnableVibrate(Context context, boolean isVibrate) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(KEY_IS_ENABLE_VIBRATE, isVibrate).commit();
        return true;
    }

    public static boolean isEnableVibrate(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(KEY_IS_ENABLE_VIBRATE, DEFAULT_IS_ENABLE_VIBRATE);
    }

    public static boolean setEnableSound(Context context, boolean isVibrate) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(KEY_IS_ENABLE_SOUND, isVibrate).commit();
        return true;
    }

    public static boolean isEnableSound(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(KEY_IS_ENABLE_SOUND, DEFAULT_IS_ENABLE_SOUND);
    }

    public static void saveNormalLoginInfo(Context context, String username, String password) {
        Editor editor = getPreferences(context).edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public static String getUserName(Context context) {
        return getPreferences(context).getString(KEY_USERNAME, "");
    }

    public static String getPassword(Context context) {
        return getPreferences(context).getString(KEY_PASSWORD, "");
    }

    public static void setPassword(Context context, String newPassword) {
        Editor editor = getPreferences(context).edit();
        editor.putString(KEY_PASSWORD, newPassword);
        editor.commit();
    }

    public static String getEmail(Context context) {
        return getPreferences(context).getString(KEY_EMAIL, "");
    }

    public static boolean setFilterStartAge(Context context, int age) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.edit().putInt(KEY_FILTER_START_AGE, age).commit();
    }

    public static int getFilterStartAge(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt(KEY_FILTER_START_AGE, 18);
    }

    public static boolean setFilterEndAge(Context context, int age) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.edit().putInt(KEY_FILTER_END_AGE, age).commit();
    }

    public static int getFilterEndAge(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt(KEY_FILTER_END_AGE, 80);
    }

    public static boolean setFilterCity(Context context, String city) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.edit().putString(KEY_FILTER_ETHNICITY, city).commit();
    }

    public static String getFilterCity(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getString(KEY_FILTER_ETHNICITY, "");
    }

}
