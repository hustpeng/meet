/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * Android Common Kit
 *
 * @author mayimchen
 * @since 2016-07-23
 */
package com.agmbat.android.utils;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import com.agmbat.android.SystemManager;
import com.agmbat.io.IoUtils;
import com.agmbat.security.SecurityUtil;
import com.agmbat.text.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 提供从apk包中获取相关信息的操作
 */
public class ApkUtils {

    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

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

    public static String getVersionName(String packageName) {
        String versionName = "";
        PackageInfo info = getPackageInfo(packageName);
        if (null != info) {
            versionName = info.versionName;
        }
        return versionName;
    }

    /**
     * Generate the application tag with application name.
     */
    public static String generateApplicationTagWithoutVersionName() {
        Context context = SystemManager.getContext();
        if (context != null) {
            ApplicationInfo app = context.getApplicationInfo();
            String applicationTag = context.getString(app.labelRes);
            return applicationTag;
        }
        return null;
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

    public static List<PackageInfo> getInstalledPackages() {
        return getPackageManager().getInstalledPackages(0);
    }

    public static PackageInfo getPackageArchiveInfo(String apkPath) {
        return getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    }

    public static PackageInfo getPackageArchiveInfo(String apkPath, int flag) {
        PackageInfo info = getPackageManager().getPackageArchiveInfo(apkPath, flag);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
        }
        return info;
    }

    public static PackageInfo getPackageInfo(String packageName) {
        return getPackageInfo(packageName, 0);
    }

    public static PackageInfo getPackageInfo(String packageName, int flags) {
        try {
            return getPackageManager().getPackageInfo(packageName, flags);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ApplicationInfo getApplicationInfo(String packageName, int flags) {
        try {
            return getPackageManager().getApplicationInfo(packageName, flags);
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
     * 判断包名所对应的应用是否安装在SD卡上
     *
     * @return, true if install on SD card
     */
    public static boolean isInstallOnSDCard(String packageName) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, 0);
            if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPackageLaunchable(String packageName) {
        return getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void openPackageDetails(Context context, String packageName) {
        Intent intent = null;
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.fromParts("package", packageName, null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        } else {
            String appPkgNameKey = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgNameKey, packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        AppUtils.startActivity(context, intent);
    }

    // 安装指定路径下的APK文件。
    public static void installPackage(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        if (null == file || !file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        AppUtils.startActivity(context, intent);
    }

    // 卸载某个程序
    public static void uninstallPackage(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppUtils.startActivity(context, intent);
    }

    /**
     * 获取所有launcher app的包名
     *
     * @return
     */
    public static List<String> getLauncherApps() {
        List<String> launcherAppList = new ArrayList<String>();
        List<ResolveInfo> resolveInfos = queryLauncherActivities();
        if (null != resolveInfos) {
            for (ResolveInfo ri : resolveInfos) {
                launcherAppList.add(ri.activityInfo.packageName);
            }
        }
        return launcherAppList;
    }

    /**
     * 查询所有launcher apps
     *
     * @return
     */
    public static List<ResolveInfo> queryLauncherActivities() {
        final PackageManager pm = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Query for the set of apps
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        return apps;
    }

    /**
     * Query the package manager for MAIN/LAUNCHER activities in the supplied package.
     */
    public static List<ResolveInfo> findLauncherActivitiesForPackage(String packageName) {
        final PackageManager pm = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    public static List<String> getHomePackages() {
        List<String> names = new ArrayList<String>();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos =
                getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfos) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    public static boolean isValidPackageComponent(ComponentName cn) {
        if (cn == null) {
            return false;
        }
        final PackageManager pm = getPackageManager();
        try {
            // Skip if the application is disabled
            PackageInfo pi = pm.getPackageInfo(cn.getPackageName(), 0);
            if (!pi.applicationInfo.enabled) {
                return false;
            }
            // Check the activity
            return (pm.getActivityInfo(cn, 0) != null);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSystemPackage(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        if (packageName.matches("com\\.android\\..*")) {
            return true;
        }
        if (packageName.matches("android") || packageName.matches("com\\.sec\\.android\\.app\\..*")) {
            return true;
        }
        return false;
    }

    public static String getPackageNameFromAppName(String appName) {
        if (null == appName) {
            return null;
        }
        PackageManager pm = getPackageManager();
        List<PackageInfo> packageInfos = getInstalledPackages();
        for (PackageInfo info : packageInfos) {
            String name = info.applicationInfo.loadLabel(pm).toString();
            if (name.equals(appName)) {
                return info.packageName;
            }
        }
        return null;
    }

    /**
     * get meta data from AndroidManifest.xml base on the key.
     *
     * @param key
     * @return
     */
    public static String getMetaValue(String key) {
        ApplicationInfo info = getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        if (info != null) {
            Bundle metaData = info.metaData;
            if (metaData != null) {
                return metaData.getString(key);
            }
        }
        return null;
    }

    public static String getSignatureText(String packageName) {
        PackageInfo packageInfo = getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        return getSignatureText(packageInfo);
    }

    private static String getSignatureText(PackageInfo packageInfo) {
        Signature signature = getSignature(packageInfo);
        if (signature != null) {
            return generateSignatureText(signature);
        }
        return null;
    }

    private static String generateSignatureText(Signature signature) {
        byte[] data = signature.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final StringBuilder builder = new StringBuilder();
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final Collection<?> c = cf.generateCertificates(bais);
            byte[] bytes = null;
            final Map<String, byte[]> map = new HashMap<String, byte[]>();
            int index = 0;
            final Iterator<?> iterator = c.iterator();
            while (iterator.hasNext()) {
                Certificate cert = (Certificate) iterator.next();
                bytes = cert.getEncoded();
                map.put("" + index, bytes);
                bytes = null;
                index++;
            }
            MessageDigest digest = MessageDigest.getInstance("sha-1");
            if (map.get("0") != null) {
                digest.update(map.get("0"));
                final byte[] digesta = digest.digest();
                for (int i = 0; i < digesta.length; i++) {
                    builder.append(digesta[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(bais);
        }
        return builder.toString();
    }

    public static String getSignatureTextByPath(String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return "";
        }
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
        return getSignatureText(packageInfo);
    }

    public static Signature getSignature(PackageInfo packageInfo) {
        if (packageInfo == null) {
            return null;
        }
        Signature[] info = packageInfo.signatures;
        if (info == null || info.length <= 0) {
            return null;
        }
        return info[0];
    }

    /**
     * 检测程序自身的签名是否与指定的签名相符.该方法是通过比较签名证书的认证指纹来实现的.<br>
     * <b>该方法可以用来判断程序有没有被非法修改</b>
     *
     * @param context                程序的上下文
     * @param certificateFingerprint 签名证书的认证指纹 <br>
     *                               证书的认证指纹可以通过下面的命令获取:keytool -list -keystore [yourkeystore].keystore</br>
     * @return 签名证书相符返回true, 否则返回false
     */
    public static boolean checkApkSignature(String certificateFingerprint) {
        PackageInfo info = getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        Signature[] signatures = info.signatures;
        if (signatures != null && signatures.length == 1) {
            byte[] data = signatures[0].toByteArray();
            byte[] md5 = SecurityUtil.md5Hash(data);
            String fingerprint = StringUtils.bytesToString(md5);
            return (certificateFingerprint.equals(fingerprint));
        }
        return false;
    }

    /**
     * 检测给定的上下文是否具有特定的权限。
     *
     * @param context     要进行检查的上下文。
     * @param permissions 要检查的权限列表。
     * @throws SecurityException 给定的 Contenxt 不具备 permissions 中列出的某个权限。
     */
    public static void checkPermissions(Context context, String... permissions) throws SecurityException {
        for (String permission : permissions) {
            if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                throw new SecurityException("You must add permission:<uses-permission android:name=\"" + permission
                        + "\" />");
            }
        }
    }

    private static PackageManager getPackageManager() {
        return SystemManager.getPackageManager();
    }

}
