package com.agmbat.android.utils;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * 位置辅助工具
 */
public class LocationHelper {

    /**
     * 检查是否开启了地理位置服务
     *
     * @return 若已经开启，则返回true，否则返回false。
     */
    public static boolean isLocationProviderEnable(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String str = Settings.Secure.getString(resolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return !TextUtils.isEmpty(str);
    }

    public static void startGPSSettingActivity(Context context) {
        try {
            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算距离
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double radLat1 = lat1 * Math.PI / 180;
        double radLat2 = lat2 * Math.PI / 180;
        double a = radLat1 - radLat2;
        double b = lon1 * Math.PI / 180 - lon2 * Math.PI / 180;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 检查当前GPS的状态
     *
     * @return 若已经开启，则返回true，否则返回false。
     */
    private boolean isGPSProviderEnable(Context context) {
        final String str =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        boolean enable = false;
        if (!TextUtils.isEmpty(str)) {
            enable = str.contains("gps");
        }
        return enable;
    }

    /**
     * 检查当前网络定位服务的状态
     *
     * @return 若已经开启，则返回true，否则返回false。
     */
    private boolean isNetworkProviderEnable(Context context) {
        final String str =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        boolean enable = false;
        if (!TextUtils.isEmpty(str)) {
            enable = str.contains("network");
        }
        return enable;
    }

}
