package com.agmbat.android.utils;

import com.agmbat.android.AppResources;

import android.content.Context;
import android.widget.Toast;

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
    public static void showToast(final CharSequence text) {
        final int duration;
        if (text.length() <= 15) {
            duration = Toast.LENGTH_SHORT;
        } else {
            duration = Toast.LENGTH_LONG;
        }
        show(text, duration);
    }

    public static void showToastLong(final int resId) {
        show(resId, Toast.LENGTH_LONG);
    }

    public static void showToastLong(final CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public static void showToastShort(final int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(final CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示Toast
     *
     * @param text
     * @param length
     */
    private static void show(final CharSequence text, final int length) {
        UiUtils.runOnUIThread(new Runnable() {
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
        UiUtils.runOnUIThread(new Runnable() {
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
     * @param length
     */
    private static void showToastSafely(final CharSequence text, final int length) {
        try {
            final Context context = AppResources.getAppContext();
            Toast.makeText(context, text, length).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安全的显示toast
     *
     * @param resId
     * @param length
     */
    private static void showToastSafely(final int resId, final int length) {
        try {
            final Context context = AppResources.getAppContext();
            Toast.makeText(context, context.getText(resId), length).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}