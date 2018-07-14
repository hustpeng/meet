/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * Android Common Kit
 *
 * @author mayimchen
 * @since 2016-07-23
 */
package com.agmbat.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.agmbat.android.SystemManager;
import com.agmbat.file.FileUtils;
import com.agmbat.security.SecurityUtil;
import com.agmbat.io.IoUtils;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 提供当前设备的信息
 */
public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    /**
     * 返回此设备的设备的软件版本。
     * <p>
     * 需要 {@link android.Manifest.permission#READ_PHONE_STATE} 权限。
     *
     * @return 返回此设备的软件版本。
     */
    public static String getSystemDeviceSoftwareVersion() {
        return SystemManager.getTelephonyManager().getDeviceSoftwareVersion();
    }

    /**
     * Return the kernal version of the device.
     *
     * @return the kenral version of the device.
     */
    public static String getFormattedKernelVersion() {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            String procVersionStr;
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }
            final String procVersionRegex = "\\w+\\s+" + /* ignore: Linux */
                    "\\w+\\s+" + /* ignore: version */
                    "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                    "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                    "\\(.*?(?:\\(.*?\\)).*?\\)\\s+" + /* ignore: (gcc ..) */
                    "([^\\s]+)\\s+" + /* group 3: #26 */
                    "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                    "(.+)"; /* group 4: date */

            final Pattern p = Pattern.compile(procVersionRegex);
            final Matcher m = p.matcher(procVersionStr);
            if (!m.matches()) {
                Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount() + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(m.group(2)).append(" ").append(m.group(3))
                        .append("\n").append(m.group(4))).toString();
            }
        } catch (final IOException e) {
            Log.e(TAG, "IO Exception when getting kernel version for Device Info screen", e);
            return "Unavailable";
        }
    }

    public static String getDeviceId() {
        return SystemManager.getTelephonyManager().getDeviceId();
    }

    /**
     * 返回此设备的设备标识符。
     *
     * @return 返回此设备的系统标识符。
     */
    public static String getAndroidId() {
        ContentResolver resolver = SystemManager.getContentResolver();
        return Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID);
    }

    public static String getPhoneNumber() {
        TelephonyManager telephonyManager = SystemManager.getTelephonyManager();
        String number = telephonyManager.getLine1Number();
        if (number != null && number.startsWith("+86")) {
            number = number.substring(3);
        }
        return number;
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getMemoryUsedPercent() {
        long totalMemorySize = getTotalMemory();
        long used = getUsedMemory();
        int percent = (int) (used / (float) totalMemorySize * 100);
        return percent + "%";
    }

    /**
     * 获取已用的内存数值
     */
    public static long getUsedMemory() {
        return getTotalMemory() - getAvailableMemory();
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     */
    public static long getAvailableMemory() {
        return getMemoryInfo().availMem;
    }

    public static long getTotalMemory() {
        return getMemoryInfo().totalMem;
    }

    private static ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        SystemManager.getActivityManager().getMemoryInfo(mi);
        return mi;
    }

    public static long getTotalMemory2() {
        String path = "/proc/meminfo";
        BufferedReader br = null;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
            br = new BufferedReader(reader);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            long totalMemorySize = Long.parseLong(subMemoryLine.replaceAll("\\D+", ""));
            return totalMemorySize;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(br);
        }
        return 0;
    }

    public static String getScreenSizeText() {
        Point size = getScreenSize();
        return String.format("%s*%d", size.x, size.y);
    }

    /**
     * 获取屏幕大小, 使用DisplayMetrics
     *
     * @param context
     * @return
     */
    public static String getScreenSizeText(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels + "X" + displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕大小, 取到的高度是不包含下面虚拟导航键的高度
     *
     * @return
     */
    public static Point getScreenSize() {
        Point point = new Point();
        WindowManager wm = SystemManager.getWindowManager();
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            point.x = display.getWidth(); // 屏幕宽（像素，如：480px）
            point.y = display.getHeight(); // 屏幕高（像素，如：800p）
        } else {
            display.getSize(point);
        }
        return point;
    }

    /**
     * 获取屏幕真实大小, 包含下面虚拟导航键
     *
     * @param context
     * @return
     */
    public static Point getRealScreenSize(Context context) {
        WindowManager wm = SystemManager.getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        Point point = new Point();
        point.x = dm.widthPixels;
        point.y = dm.heightPixels;
        return point;
    }

    /**
     * 获取手机制造商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备类型
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取android 系统版本
     *
     * @return
     */
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取系统的SDK版本
     *
     * @return
     */
    public static int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取当前手机使用的语言
     *
     * @return
     */
    public static String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 是否有sim卡
     *
     * @param context
     * @return
     */
    public static boolean hasSimCard() {
        return SystemManager.getTelephonyManager().getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * 获取系统已运行时间
     *
     * @return
     */
    public static long getUptime() {
        String path = "/proc/uptime";
        String text = FileUtils.readFileText(new File(path));
        if (text != null) {
            text = text.trim();
            String[] array = text.split("\\s{1,}");
            if (array != null && array.length == 2) {
                String timeText = array[0];
                long time = (long) (Double.parseDouble(timeText) * 1000);
                return time;
            }
        }
        return 0;
    }

    /**
     * 判断手机是否是miui系统
     *
     * @return
     */
    public static boolean isMiui() {
        final String fingerprint = Build.FINGERPRINT;
        return fingerprint.contains("MIUI") || fingerprint.contains("Xiaomi");
    }

    /**
     * 判断当前设备是否是模拟器。如果返回TRUE，则是模拟器，不是返回FALSE
     *
     * @return
     */
    public static boolean isEmulator() {
        String imei = DeviceUtils.getDeviceId();
        if (imei != null && imei.equals("000000000000000")) {
            return true;
        }
        String model = DeviceUtils.getDeviceModel();
        return ("sdk".equalsIgnoreCase(model)) || ("google_sdk".equalsIgnoreCase(model));
    }

    public static String getUserAgent() {
        String packageName = ApkUtils.getPackageName();
        PackageInfo packageInfo = ApkUtils.getPackageInfo(packageName);
        if (packageInfo != null) {
            return String.format("%s/%s; %s/%s/%s/%s; %s/%s/%s", packageInfo.packageName, packageInfo.versionName,
                    Build.BRAND, Build.DEVICE, Build.MODEL, Build.ID, Build.VERSION.SDK_INT, Build.VERSION.RELEASE,
                    Build.VERSION.INCREMENTAL);
        }
        return null;
    }

    /**
     * 获取Android client id ,如果设备id无效，则自动生成一个
     *
     * @return
     */
    public static String getClientId() {
        final String keyUuid = "client_guid";
        String did = getDeviceId();
        if (TextUtils.isEmpty(did)) {
            did = getAndroidId();
        }
        if (TextUtils.isEmpty(did)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SystemManager.getContext());
            String guid = preferences.getString(keyUuid, null);
            if (TextUtils.isEmpty(guid)) {
                guid = SecurityUtil.generateGUID();
                preferences.edit().putString(keyUuid, guid).commit();
            }
            did = guid;
        }
        return SecurityUtil.md5Hash(did);
    }

    private static Camera sCamera;

    public static void openFlashLight() {
        if (sCamera == null) {
            sCamera = Camera.open();
            try {
                sCamera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Camera.Parameters params = sCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            sCamera.setParameters(params);
        }
        sCamera.startPreview();
    }

    public static void closeFlashLight() {
        if (sCamera != null) {
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue() {
        long totalMemorySize = getTotalMemory2();
        if (totalMemorySize <= 0) {
            return "";
        }
        long availableSize = getAvailableMemory() / 1024;
        int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
        return percent + "%";
    }

}
