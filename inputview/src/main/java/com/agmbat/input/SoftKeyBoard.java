package com.agmbat.input;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.agmbat.android.AppResources;
import com.agmbat.android.SysResources;
import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.app.PreferenceUtils;

/**
 * 处理软键逻辑
 */
public class SoftKeyBoard {

    /**
     * prefence key
     */
    private static final String KEY_SOFTKEYBOARD_HEIGHT = "inputview_SoftKeyBoardHeight";
    /**
     * 默认高度
     */
    private int mSoftKeyBoardHeight = getHeightFromPrefs();
    /**
     * 默认不显示
     */
    private boolean mIsSoftKeyBoardShown = false;
    private OnSoftKeyBoardHeightChangeListener mListener;

    public SoftKeyBoard(View view) {
        final View rootView = view.getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 计算出的高度
                int height = calcSoftKeyBoardHeight(rootView);
                if (height > 0) {
                    if (mSoftKeyBoardHeight != height) {
                        mSoftKeyBoardHeight = height;
                        saveHeightToPrefs(height);
                        if (mListener != null) {
                            mListener.onSoftKeyBoardHeightChange(height);
                        }
                    }
                    mIsSoftKeyBoardShown = true;
                } else {
                    mIsSoftKeyBoardShown = false;
                }
            }
        });
    }

    /**
     * 计算软键盘高度
     *
     * @return
     */
    private static int calcSoftKeyBoardHeight(View rootView) {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        // 屏幕当前可见高度，不包括状态栏
        int displayHeight = rect.bottom - rect.top;
        // 屏幕可用高度
        int availableHeight = DeviceUtils.getScreenSize().y;
        // 用于计算键盘高度
        int inputMethodHeight = availableHeight - displayHeight - SysResources.getStatusBarHeight();
        if (inputMethodHeight < 0) {
            inputMethodHeight = 0;
        }
        return inputMethodHeight;
    }

    /**
     * 从Prefs中读取高度
     *
     * @return
     */
    private static int getHeightFromPrefs() {
        // 默认295dp
        int defaultHeight = (int) SysResources.dipToPixel(295);
        return PreferenceUtils.getInt(KEY_SOFTKEYBOARD_HEIGHT, defaultHeight, AppResources.getAppContext());
    }

    /**
     * 将高度保存到Prefs文件中
     *
     * @param height
     */
    private static void saveHeightToPrefs(int height) {
        PreferenceUtils.putInt(KEY_SOFTKEYBOARD_HEIGHT, height, AppResources.getAppContext());
    }

    public void setOnSoftKeyBoardHeightChangeListener(OnSoftKeyBoardHeightChangeListener l) {
        mListener = l;
    }

    /**
     * 判断软键盘是否显示
     *
     * @return
     */
    public boolean isSoftKeyBoardShown() {
        return mIsSoftKeyBoardShown;
    }

    /**
     * 获取软键盘高度
     *
     * @return
     */
    public int getSoftKeyBoardHeight() {
        return mSoftKeyBoardHeight;
    }

    /**
     * 软键盘高度发生了变化
     */
    public interface OnSoftKeyBoardHeightChangeListener {

        /**
         * 回调高度变化
         *
         * @param height
         */
        public void onSoftKeyBoardHeightChange(int height);
    }

}
