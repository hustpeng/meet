package com.agmbat.android.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.agmbat.log.Log;
import com.agmbat.task.Method;
import com.agmbat.task.SyncResult;
import com.agmbat.task.SyncRunnable;

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
     * Retrieve the UI_HANDLER.
     *
     * @return the UI_HANDLER
     */
    public static Handler getHandler() {
        return UI_HANDLER;
    }

    public static Thread getUiThread() {
        return Looper.getMainLooper().getThread();
    }

    public static void checkThread() {
        if (!isOnUiThread()) {
            throw new IllegalAccessError(
                    "this method should be called in main thread");
        }
    }

    public static boolean isOnUiThread() {
        return Thread.currentThread() == getUiThread();
    }

    public static void runOnUIThread(Runnable action) {
        if (!isOnUiThread()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static void runOnUiThreadDelay(Runnable action, long delayMillis) {
        getHandler().postDelayed(action, delayMillis);
    }

    /**
     * Execute a call on the application's main thread, blocking until it is complete. Useful for doing things that are
     * not thread-safe, such as looking at or modifying the view hierarchy.
     *
     * @param action The code to run on the main thread.
     */
    public static void runOnUiThreadSync(Runnable action) {
        if (!isOnUiThread()) {
            SyncRunnable sr = new SyncRunnable(action);
            getHandler().post(sr);
            sr.waitForComplete();
        } else {
            action.run();
        }
    }

    public static void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static boolean isAfterJellyBeanMR2() {
        return Build.VERSION.SDK_INT > 18; // Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * All WebView method must be called in UI Thread After SDK18
     */
    public static void runOnUIThreadAfterSDK18(Runnable action) {
        if (!isOnUiThread() && isAfterJellyBeanMR2()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static <T> T runOnUIThreadAfterSDK18(final Method<T> method) {
        if (isAfterJellyBeanMR2()) {
            return runOnUIThread(method);
        } else {
            return method.call();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T runOnUIThread(final Method<T> method) {
        if (!isOnUiThread()) {
            final SyncResult<T> result = new SyncResult<T>();
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    synchronized (result) {
                        T data = method.call();
                        result.notifyComplete(data);
                    }
                }
            });
            synchronized (result) {
                result.waitForComplete();
                return result.get();
            }
        } else {
            return method.call();
        }
    }

    public static boolean showDialog(Dialog dialog) {
        try {
            dialog.show();
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return false;
    }

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

    public static boolean sendRemoteMessageSafely(Message message) {
        if (null == message || message.replyTo == null) {
            return false;
        }
        try {
            // Use arg2 of message to pass the pid of browser to messager
            message.arg2 = Process.myPid();
            message.replyTo.send(message);
            return true;
        } catch (Exception e) {
            Log.e("Cannot send message", e);
        }
        return false;
    }

}
