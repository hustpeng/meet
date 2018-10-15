package com.agmbat.android.utils;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.agmbat.android.AppResources;
import com.agmbat.android.SystemManager;
import com.agmbat.utils.ReflectionUtils;

import java.io.File;

/**
 * Package archive parsing {@hide}
 * <p>
 * 使用反射获取包信息
 * android.content.pm.PackageParser
 */
public class PackageParser {

    public static String getName(Object packageInfo, String apkPath) {
        ApplicationInfo info = getApplicationInfo(packageInfo);
        if (info == null) {
            return null;
        }
        if (info.labelRes != 0) {
            Resources res = getApkResources(apkPath);
            return res.getString(info.labelRes);
        } else if (info.nonLocalizedLabel != null) {
            return info.nonLocalizedLabel.toString();
        }
        return null;
    }

    public static String getPackageName(Object packageInfo) {
        try {
            return (String) ReflectionUtils.getFieldValue(packageInfo, "packageName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersionName(Object packageInfo) {
        try {
            return (String) ReflectionUtils.getFieldValue(packageInfo, "mVersionName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Object packageInfo) {
        try {
            return (Integer) ReflectionUtils.getFieldValue(packageInfo, "mVersionCode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Bitmap loadApkIcon(String path) {
        Object packageInfo = parsePackage(new File(path));
        return getIcon(packageInfo, path);
    }

    private static Bitmap getIcon(Object packageInfo, String apkPath) {
        ApplicationInfo info = getApplicationInfo(packageInfo);
        if (info == null) {
            return null;
        }
        if (info.icon != 0) {
            Resources res = getApkResources(apkPath);
            if (res != null) {
                return BitmapFactory.decodeResource(res, info.icon);
            }
        }
        return null;
    }

    private static ApplicationInfo getApplicationInfo(Object packageInfo) {
        if (packageInfo == null) {
            return null;
        }
        try {
            return (ApplicationInfo) ReflectionUtils.getFieldValue(packageInfo, "applicationInfo");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object parsePackage(File sourceFile) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            SystemManager.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Object packageParser =
                    ReflectionUtils.newInstance("com.agmbat.android.utils.PackageParser", sourceFile.toString());
            Class<?>[] parameterTypes = new Class<?>[]{File.class, String.class, DisplayMetrics.class, int.class};
            Object packageInfo =
                    ReflectionUtils.invokeMethod(packageParser, "parsePackage", parameterTypes, new Object[]{
                            sourceFile, sourceFile.toString(), metrics, 0});
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Resources getApkResources(String apkPath) {
        try {
            AssetManager assetManager = (AssetManager) ReflectionUtils.newInstance("android.content.res.AssetManager");
            ReflectionUtils.invokeMethod(assetManager, "addAssetPath", new Class<?>[]{String.class},
                    new Object[]{apkPath});
            Resources contextRes = AppResources.getResources();
            Resources res = new Resources(assetManager, contextRes.getDisplayMetrics(), contextRes.getConfiguration());
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
