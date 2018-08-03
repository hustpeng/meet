package com.agmbat.meetyou.util;

/**
 * Created by MPC on 2017/10/25.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.agmbat.android.utils.ToastUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 系统工具箱
 */
public class SystemUtil {


    /**
     * App是否已经安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean isAppInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a stopCountDown and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取软件版本名称
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (null != packageInfo) {
                version = packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (null != packageInfo) {
                versionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    /**
     * 获取自己应用程序的名称
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;

        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var4) {
            applicationInfo = null;
        }

        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * 获取自己应用程序的图标
     */
    public static Bitmap getAppIcon(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

    /**
     * 安装APK
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开系统浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast("找不到可用的浏览器");
        }
    }

    /**
     * 打开QQ客户端，并跟指定QQ号聊天
     *
     * @param context
     */
    public static void openQQClient(Context context, String qq) {
        try {
            String url = String.format("mqqwpa://im/chat?chat_type=wpa&uin=%s&version=1", qq);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast("找不到QQ客户端");
        }
    }

    /**
     * 判断本程序界面是否传入后台运行了
     *
     * @param context
     * @return
     */
    public static boolean isAppBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                deviceId = tm.getDeviceId();
            }
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        }
        return null == deviceId ? "" : deviceId;
    }

    /**
     * 获取Android mac地址，4.0一直到6.0，7.0系统都可以获取得到Mac地址，方法由google提供
     *
     * @return
     */
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取AndroidId
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return null == androidId ? "" : androidId;
    }

    /**
     * 获取状态栏高度，不能在Activity初始化使用。
     * 在onWindowFocusChanged中回调可以获取到正确高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 复制内容到剪贴板
     *
     * @param context
     * @param label
     * @param content
     */
    public static void copyToClipBoard(Context context, String label, String content) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        manager.setPrimaryClip(ClipData.newPlainText(label, content));
    }

    /**
     * 获取剪贴版内容
     *
     * @param context
     * @return
     */
    public static String getClipBoardContent(Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        //GET贴板是否有内容
        ClipData clipData = manager.getPrimaryClip();
        if (null != clipData) {
            //获取到内容
            if (clipData.getItemCount() > 0) {
                ClipData.Item item = clipData.getItemAt(0);
                return item.getText().toString();
            }
        }
        return "";
    }

    /**
     * 打开邮件客户端
     *
     * @param context
     * @param email
     */
    public static void openEmailClient(Context context, String email) {
        Intent intent = null;
        try {
            String[] emails = {email}; // 需要注意，email必须以数组形式传入
            Uri uri = Uri.parse("mailto:" + email);
            intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra(Intent.EXTRA_EMAIL, emails); // 接收人
            //intent.putExtra(Intent.EXTRA_CC, emails); // 抄送人
            Intent chooserIntent = Intent.createChooser(intent, "请选择邮件客户端");
            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooserIntent);
        } catch (Exception e) {
            ToastUtil.showToast("找不到可用的邮件客户端");
        }

    }

    /**
     * 获取手机状态栏的高度
     *
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

}