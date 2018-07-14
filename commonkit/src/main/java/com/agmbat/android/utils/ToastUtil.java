package com.agmbat.android.utils;

import android.content.Context;
import android.widget.Toast;

import com.agmbat.android.AppResources;

/**
 * Toast工具类
 */
public class ToastUtil {

    /**
     * 根据文本长短决定显示的时间长度。
     *
     * @param resId
     */
    public static void showToast(int resId) {
        final Context context = AppResources.getAppContext();
        showToast(context.getText(resId));
    }

    /**
     * 根据文本长短决定显示的时间长度。
     *
     * @param text
     */
    public static void showToast(CharSequence text) {
        final int duration;
        if (text.length() <= 15) {
            duration = Toast.LENGTH_SHORT;
        } else {
            duration = Toast.LENGTH_LONG;
        }
        show(text, duration);
    }

    public static void showToastLong(int resId) {
        show(resId, Toast.LENGTH_LONG);
    }

    public static void showToastLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public static void showToastShort(int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示Toast
     *
     * @param text
     * @param length
     */
    private static void show(final CharSequence text, final int length) {
        UiUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastSafely(text, length);
            }
        });
    }

    /**
     * 显示Toast
     *
     * @param resId
     * @param length
     */
    private static void show(final int resId, final int length) {
        UiUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastSafely(resId, length);
            }
        });
    }

    /**
     * 安全的显示toast
     *
     * @param text
     * @param duration
     */
    private static void showToastSafely(final CharSequence text, final int duration) {
        try {
            Context context = AppResources.getAppContext();
            Toast.makeText(context, text, duration).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安全的显示toast
     *
     * @param resId
     * @param duration
     */
    private static void showToastSafely(final int resId, final int duration) {
        try {
            Context context = AppResources.getAppContext();
            Toast.makeText(context, context.getText(resId), duration).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}