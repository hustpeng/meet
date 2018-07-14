package com.agmbat.android.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.agmbat.log.Log;

/**
 * 将一些方法放在主线程运行
 */
public class UiUtils {

    private static final String TAG = UiUtils.class.getSimpleName();

    /**
     * UI线程中的Handler
     */
    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 获取ui handler
     *
     * @return the UI_HANDLER
     */
    public static Handler getHandler() {
        return UI_HANDLER;
    }

    /**
     * 获取Ui线程
     *
     * @return
     */
    public static Thread getUiThread() {
        return Looper.getMainLooper().getThread();
    }

    /**
     * 当前线程是否为ui线程
     *
     * @return
     */
    public static boolean isOnUiThread() {
        return Thread.currentThread() == getUiThread();
    }

    /**
     * 在ui线程执行
     *
     * @param action
     */
    public static void runOnUiThread(Runnable action) {
        if (!isOnUiThread()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    /**
     * 延迟指定时间在主线程运行
     *
     * @param action
     * @param delayMillis
     */
    public static void runOnUiThreadDelay(Runnable action, long delayMillis) {
        getHandler().postDelayed(action, delayMillis);
    }


    /**
     * 将 runnable post到主线程运行
     *
     * @param runnable
     */
    public static void post(Runnable runnable) {
        getHandler().post(runnable);
    }


    /**
     * 显示对话框, 添加一些保护, 防止特殊环境异常
     *
     * @param dialog
     * @return
     */
    public static boolean showDialog(Dialog dialog) {
        try {
            dialog.show();
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return false;
    }

    /**
     * 关闭对话框, 添加一些保护, 防止特殊环境异常
     *
     * @param dialog
     * @return
     */
    public static boolean dismissDialog(DialogInterface dialog) {
        if (dialog == null) {
            return false;
        }
        try {
            dialog.dismiss();
            return true;
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
        return false;
    }

}
