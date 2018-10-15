package com.agmbat.android.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

import com.agmbat.android.SystemManager;

public class Brightness {

    /**
     * 设置屏幕亮度，这会反映到真实屏幕上
     *
     * @param activity
     * @param brightness
     */
    public static void setActivityBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness() {
        try {
            return Settings.System.getInt(SystemManager.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 保存亮度设置状态
     *
     * @param brightness
     */
    public static void setScreenBrightness(int brightness) {
        ContentResolver resolver = SystemManager.getContentResolver();
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    /**
     * 判断是否开启了自动亮度调节
     */
    public static boolean isAutoBrightness() {
        return getBrightnessMode() == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    }

    /**
     * 开启亮度自动调节
     */
    public static void setAutomaticMode() {
        setBrightnessMode(Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 停止自动亮度调节
     */
    public static void setManualMode() {
        setBrightnessMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 获取亮度的显示模式
     */
    public static int getBrightnessMode() {
        try {
            return Settings.System.getInt(SystemManager.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    }

    /**
     * 保存亮度的显示模式
     */
    public static void setBrightnessMode(int mode) {
        Settings.System.putInt(SystemManager.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }
}