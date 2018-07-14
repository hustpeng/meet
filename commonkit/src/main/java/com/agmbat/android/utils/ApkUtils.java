package com.agmbat.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import com.agmbat.android.SystemManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 提供从apk包中获取相关信息的操作
 */
public class ApkUtils {

    /**
     * 取得主程序包名
     *
     * @return
     */
    public static String getPackageName() {
        return SystemManager.getContext().getPackageName();
    }

    /**
     * 获取应用程序名字
     *
     * @return
     */
    public static String getAppName() {
        Context context = SystemManager.getContext();
        ApplicationInfo app = context.getApplicationInfo();
        return (String) app.loadLabel(SystemManager.getPackageManager());
    }

    /**
     * 获取应用程序名字
     *
     * @return
     */
    public static String getAppName(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo app = pm.getApplicationInfo(packageName, 0);
            return (String) app.loadLabel(pm);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得主程序version code
     *
     * @return
     */
    public static int getVersionCode() {
        return getVersionCode(getPackageName());
    }

    /**
     * 获取apk包版本
     *
     * @param packageName
     * @return
     */
    public static int getVersionCode(String packageName) {
        int versionCode = 0;
        PackageInfo packageInfo = getPackageInfo(packageName);
        if (null != packageInfo) {
            versionCode = packageInfo.versionCode;
        }
        return versionCode;
    }

    /**
     * 取得主程序version name
     *
     * @return
     */
    public static String getVersionName() {
        return getVersionName(getPackageName());
    }

    /**
     * 获取apk包版本名称
     *
     * @param packageName
     * @return
     */
    public static String getVersionName(String packageName) {
        String versionName = "";
        PackageInfo info = getPackageInfo(packageName);
        if (null != info) {
            versionName = info.versionName;
        }
        return versionName;
    }

    /**
     * 得到图标信息
     *
     * @param info
     * @return
     */
    public static Drawable getPackageIcon(PackageInfo info) {
        PackageManager pm = SystemManager.getPackageManager();
        return pm.getApplicationIcon(info.applicationInfo);
    }

    /**
     * 获取apk icon
     *
     * @param packageName
     * @return
     */
    public static Drawable getPackageIcon(String packageName) {
        Drawable appIcon = null;
        PackageManager pm = SystemManager.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            appIcon = applicationInfo.loadIcon(pm);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }

    /**
     * 获取设备上已安装包信息
     *
     * @return
     */
    public static List<PackageInfo> getInstalledPackages() {
        return getPackageManager().getInstalledPackages(0);
    }

    public static PackageInfo getPackageArchiveInfo(String apkPath) {
        return getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    }

    /**
     * 通过apk路径获取包信息
     *
     * @param apkPath
     * @param flag
     * @return
     */
    public static PackageInfo getPackageArchiveInfo(String apkPath, int flag) {
        PackageInfo info = getPackageManager().getPackageArchiveInfo(apkPath, flag);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
        }
        return info;
    }

    /**
     * 获取package info
     *
     * @param packageName
     * @return
     */
    public static PackageInfo getPackageInfo(String packageName) {
        return getPackageInfo(packageName, 0);
    }

    /**
     * 获取package info
     *
     * @param packageName
     * @param flags
     * @return
     */
    public static PackageInfo getPackageInfo(String packageName, int flags) {
        try {
            return getPackageManager().getPackageInfo(packageName, flags);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断目标 package name是否已经安装
     *
     * @param packageName
     * @return true if has install, otherwise return false.
     */
    public static boolean isInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        return getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES) != null;
    }

    /**
     * 安装指定路径下的APK文件
     *
     * @param context
     * @param apkPath
     */
    public static void installPackage(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        AppUtils.startActivity(context, intent);
    }

    /**
     * 获取包管理器
     *
     * @return
     */
    private static PackageManager getPackageManager() {
        return SystemManager.getPackageManager();
    }

}
