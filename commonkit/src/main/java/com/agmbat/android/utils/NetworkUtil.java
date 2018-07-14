package com.agmbat.android.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.agmbat.android.SystemManager;

/**
 * 管理网络的工具类
 */
public class NetworkUtil {

    /**
     * 检测网络连接是否可用
     */
    public static boolean isNetworkAvailable() {
        NetworkInfo[] info = getConnectivityManager().getAllNetworkInfo();
        if (info == null) {
            return false;
        }
        for (int i = 0; i < info.length; i++) {
            if (info[i].isConnected()) {
                return true;
            }
        }
        return false;
    }

    private static ConnectivityManager getConnectivityManager() {
        return SystemManager.getConnectivityManager();
    }
}
