package com.agmbat.android;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 系统服务管理，提供app需要访问的Manager
 */
public class SystemManager {

    public static Context getContext() {
        return AppResources.getAppContext();
    }

    public static TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static ActivityManager getActivityManager() {
        return (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static WifiManager getWifiManager() {
        return (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static PackageManager getPackageManager() {
        return getContext().getPackageManager();
    }

    public static ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }

    public static AlarmManager getAlarmManager() {
        return (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    public static Vibrator getVibrator() {
        return (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static LocationManager getLocationManager() {
        return (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public static WallpaperManager getWallpaperManager() {
        return WallpaperManager.getInstance(getContext());
    }

    /**
     * 获取InputMethodManager
     *
     * @return
     */
    public static InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

    }
}
