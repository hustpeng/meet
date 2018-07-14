package com.agmbat.android.utils;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;

import com.agmbat.android.SystemManager;
import com.agmbat.log.Log;

import java.util.List;

/**
 * 提供action操作，打开系统工具
 */
public class AppUtils {

    private static final String TAG = "AppUtils";

    /**
     * 是否为debug模式
     *
     * @return
     */
    public static boolean debuggable() {
        return debuggable(SystemManager.getContext());
    }

    /**
     * 是否为debug模式, 使用BuildConfig文件中DEBUG变量会不太稳定
     *
     * @param context
     * @return
     */
    public static boolean debuggable(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        return ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    /**
     * 杀死当前进程
     */
    public static void killMyProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取前台运行的Activity
     *
     * @return
     */
    public static ComponentName getTopActivity() {
        ActivityManager am = SystemManager.getActivityManager();
        List<ActivityManager.RunningTaskInfo> rti = am.getRunningTasks(1);
        return rti.get(0).topActivity;
    }

    /**
     * 启动Activity
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to start activity", e);
        }
        return false;
    }

    /**
     * 通过浏览器打开url
     *
     * @param context
     * @param url
     */
    public static boolean openBrowser(Context context, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivity(context, intent);
    }
}
