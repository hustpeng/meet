/**
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * Android Common Kit
 *
 * @author mayimchen
 * @since 2016-06-29
 */
package com.agmbat.android.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.agmbat.android.SystemManager;
import com.agmbat.utils.ReflectionUtils;

/**
 * 管理网络的工具类
 */
public class NetworkUtil {

    /**
     * 没有网络连接
     */
    public static final int NETWORK_NONE = 0;

    /**
     * WIFI连接
     */
    public static final int NETWORK_WIFI = 1;

    /**
     * 手机网络数据连接类型
     */
    public static final int NETWORK_2G = 2;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_4G = 4;
    public static final int NETWORK_MOBILE = 5;

    /**
     * 未知运营商
     */
    public static final int OPERATOR_UNKNOWN = 0;
    /**
     * 中国移动
     */
    public static final int OPERATOR_CHINA_MOBILE = 1;

    /**
     * 中国联通
     */
    public static final int OPERATOR_CHINA_UNICOM = 2;

    /**
     * 中国电信
     */
    public static final int OPERATOR_CHINA_TELECOM = 3;

    /**
     * 获取当前网络连接类型
     *
     * @param context
     *
     * @return
     */
    public static int getNetworkState() {
        // 获取系统的网络服务
        ConnectivityManager connManager = getConnectivityManager();
        // 如果当前没有网络
        if (null == connManager) {
            return NETWORK_NONE;
        }
        // 获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }
        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
            }
        }
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_4G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA")
                                    || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORK_3G;
                            } else {
                                return NETWORK_MOBILE;
                            }
                    }
                }
            }
        }
        return NETWORK_NONE;
    }

    /**
     * 获取运营商名字
     */
    public static String getOperatorName() {
        int operator = getOperator();
        String name = null;
        switch (operator) {
            case OPERATOR_UNKNOWN:
                name = "未知";
                break;
            case OPERATOR_CHINA_MOBILE:
                name = "中国移动";
                break;
            case OPERATOR_CHINA_UNICOM:
                name = "中国联通";
                break;
            case OPERATOR_CHINA_TELECOM:
                name = "中国电信";
                break;
            default:
                name = "未知";
                break;
        }
        return name;
    }

    /**
     * 获取网络运营商
     *
     * @param context
     *
     * @return
     */
    public static int getOperator() {
        String operator = getTelephonyManager().getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002")) {
                return OPERATOR_CHINA_MOBILE;
            } else if (operator.equals("46001")) {
                return OPERATOR_CHINA_UNICOM;
            } else if (operator.equals("46003")) {
                return OPERATOR_CHINA_TELECOM;
            }
        }
        return OPERATOR_UNKNOWN;
    }

    /**
     * 获取网络运营商
     *
     * @return
     */
    public static String getNetworkOperator() {
        TelephonyManager manager = getTelephonyManager();
        return manager.getNetworkOperator();
    }

    /**
     * 打开网络设置界面
     *
     * @param context
     */
    public static void showWirelessSettings(Context context) {
        String packageName = "com.android.settings";
        String className = "com.android.settings.WirelessSettings";
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        intent.setClassName(packageName, className);
        AppUtils.startActivity(context, intent);
    }

    /**
     * 打开wifi设置界面
     *
     * @param context
     */
    public static void showWifiSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AppUtils.startActivity(context, intent);
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static String getPhoneIpText() {
        InetAddress address = getPhoneIpAddress();
        if (address != null) {
            return address.getHostAddress();
        }
        return null;
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static int getPhoneIpInt() {
        InetAddress address = getPhoneIpAddress();
        if (address != null) {
            return inetAddressToInt(address);
        }
        return 0;
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static InetAddress getPhoneIpAddress() {
        if (isWifiConnected()) {
            int address = getWifiIp();
            return intToInetAddress(address);
        }
        return getIpAddress();
    }

    /**
     * 需要权限 android.permission.ACCESS_WIFI_STATE android:name="android.permission.INTERNET 获取本机IP地址 由于网卡多，导致获取的ip不确定
     */
    public static InetAddress getIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                Enumeration<InetAddress> enIp = ni.getInetAddresses();
                while (enIp.hasMoreElements()) {
                    InetAddress address = enIp.nextElement();
                    if (!address.isLoopbackAddress() && !(address instanceof Inet6Address)) {
                        return address;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHostAddress() {
        InetAddress address = getIpAddress();
        if (address != null) {
            return address.getHostAddress();
        }
        return null;
    }

    /**
     * Gets the local ip address
     *
     * @return local ip adress or null if not found
     */
    public static InetAddress getLocalInetAddress() {
        if (!isConnectedToLocalNetwork()) {
            return null;
        }
        return getPhoneIpAddress();
    }

    /**
     * android.permission.ACCESS_WIFI_STATE
     */
    public static int getWifiIp() {
        WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
        int ipAddress = 0;
        if (wifiInfo != null) {
            ipAddress = wifiInfo.getIpAddress();
        }
        return ipAddress;
    }

    /**
     * 获取本机mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        WifiInfo info = getWifiManager().getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        }
        return null;
    }

    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 判断Wifi是否连接
     *
     * @return
     */
    public static boolean isWifiConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断移动网络是否连接上
     *
     * @return
     */
    public static boolean isMobileNetworkConnected() {
        boolean isMobileNetworkConnected = false;
        final NetworkInfo mobileNetworkInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != mobileNetworkInfo) {
            isMobileNetworkConnected = mobileNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return isMobileNetworkConnected;
    }

    /**
     * Checks to see if we are connected to a local network, for instance wifi or ethernet
     *
     * @return true if connected to a local network
     */
    public static boolean isConnectedToLocalNetwork() {
        NetworkInfo info = getActiveNetworkInfo();
        // @TODO: this is only defined starting in api level 13
        final int typeEthernet = 0x00000009;
        return info != null && info.isConnected() == true
                && (info.getType() & (ConnectivityManager.TYPE_WIFI | typeEthernet)) != 0;
    }

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

    public static int getActiveNetworkInfoType() {
        NetworkInfo activeInfo = getActiveNetworkInfo();
        if (activeInfo == null) {
            return -1;
        }
        return activeInfo.getType();
    }

    public static String getNetworkTypeName() {
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null) {
            return info.getTypeName();
        }
        return null;
    }

    public static NetworkInfo getActiveNetworkInfo() {
        return getConnectivityManager().getActiveNetworkInfo();
    }

    /**
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     *
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr) throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        if (addr.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) | ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }

    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     *
     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = new byte[4];
        addressBytes[0] = (byte) (0xff & hostAddress);
        addressBytes[1] = (byte) (0xff & (hostAddress >> 8));
        addressBytes[2] = (byte) (0xff & (hostAddress >> 16));
        addressBytes[3] = (byte) (0xff & (hostAddress >> 24));
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    /**
     * Convert a {@link Integer} based encoded IP address to {@link String}
     *
     * @param addr Address to convert
     *
     * @return Converted IP address
     */
    public static String intToIp(int addr) {
        StringBuilder builder = new StringBuilder();
        builder.append(addr & 0xff).append(".");
        builder.append((addr >>>= 8) & 0xff).append(".");
        builder.append((addr >>>= 8) & 0xff).append(".");
        builder.append((addr >>>= 8) & 0xff);
        return builder.toString();
    }

    // 将android 返回的ip，转换为需要要的ip
    public static int socketIp(int ip) {
        return (ip & 0xff) << 24 | ((ip >> 8) & 0xff) << 16 | ((ip >> 16) & 0xff) << 8 | (ip >> 24 & 0xff);
    }

    /**
     * 1、IP地址转换为整数 原理：IP地址每段可以看成是8位无符号整数即0-255， 把每段拆分成一个二进制形式组合起来，然后把这个二进制数转变成 一个无符号32为整数。 举例：一个ip地址为10.0.3.193 每段数字
     * 相对应的二进制数 10 00001010 0 00000000 3 00000011 193 11000001 组合起来即为：00001010 00000000 00000011 11000001,
     * 转换为10进制就是：167773121，即该IP地址转换后的数字就是它了。
     */
    public static int ipToInt(String ip) {
        String[] items = ip.split("\\.");
        return Integer.valueOf(items[0]) << 24 | Integer.valueOf(items[1]) << 16 | Integer.valueOf(items[2]) << 8
                | Integer.valueOf(items[3]);
    }

    /**
     * 将mac地址转换为整型数
     *
     * @param macAddress
     *
     * @return
     */
    public static long macToLong(String macAddress) {
        long address = 0;
        if (macAddress != null) {
            String[] items = macAddress.split(":");
            for (int i = 0; i < items.length; i++) {
                address |= (Long.valueOf(items[i], 16) << (8 * (items.length - 1 - i)));
            }
        }
        return address;
    }

    /**
     * Gets the value of the setting for enabling Mobile data.
     *
     * @return Whether mobile data is enabled.
     */
    public static boolean getMobileDataEnabled() {
        if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0
            return (Boolean) ReflectionUtils.invokeMethod(getTelephonyManager(), "getDataEnabled", null, null);
        }
        return (Boolean) ReflectionUtils.invokeMethod(getConnectivityManager(), "getMobileDataEnabled", null, null);
    }

    /**
     * Sets the persisted value for enabling/disabling Mobile data.
     *
     * @param enabled Whether the mobile data connection should be used or not.
     */
    public static void setMobileDataEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0 需要MODIY PHONE STATE 权限，不能正常工作
            ReflectionUtils.invokeMethod(getTelephonyManager(), "setDataEnabled", new Class<?>[] {boolean.class},
                    new Object[] {enabled});
        } else if (Build.VERSION.SDK_INT >= 9) {
            ReflectionUtils.invokeMethod(getConnectivityManager(), "setMobileDataEnabled",
                    new Class<?>[] {boolean.class}, new Object[] {enabled});
        } else {
            Object telephony = ReflectionUtils.invokeMethod(getTelephonyManager(), "getITelephony", null, null);
            if (enabled) {
                ReflectionUtils.invokeMethod(telephony, "enableDataConnectivity", null, null);
            } else {
                ReflectionUtils.invokeMethod(telephony, "disableDataConnectivity", null, null);
            }
        }
    }

    /**
     * 只判断WIFI是否已打开，不能反应出WIFI的连接状态（是否连接上）
     *
     * @return
     */
    public static boolean isWifiEnable() {
        return getWifiManager().isWifiEnabled();
    }

    /**
     * 打开或者关闭WIFI开关。调用后，请用广播事件监听WIFI的连接状态
     *
     * @param enable
     */
    public static void setWifiEnable(boolean enable) {
        getWifiManager().setWifiEnabled(enable);
    }

    /**
     * 切换WIFI状态
     */
    public static void toggleWifi() {
        WifiManager wifiManager = getWifiManager();
        boolean enable = wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(!enable);
    }

    public static String getSsid() {
        NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
            if (wifiInfo != null) {
                return formatSsid(wifiInfo.getSSID());
            }
        }
        return null;
    }

    public static String formatSsid(String ssid) {
        if (ssid != null) {
            return ssid.replaceAll("(^\")|(\"$)", "");
        }
        return null;
    }

    private static ConnectivityManager getConnectivityManager() {
        return SystemManager.getConnectivityManager();
    }

    private static WifiManager getWifiManager() {
        return SystemManager.getWifiManager();
    }

    private static TelephonyManager getTelephonyManager() {
        return SystemManager.getTelephonyManager();
    }
}
