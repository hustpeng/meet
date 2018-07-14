package com.agmbat.menu;

import android.graphics.drawable.Drawable;

import com.agmbat.android.AppResources;

/**
 * 菜单item项信息
 */
public class MenuInfo {

    /**
     * item文本颜色
     */
    public static final int BLUE = 0xFF037BFF;

    /**
     * item文本颜色
     */
    public static final int RED = 0xFFFD4A2E;

    /**
     * menu id
     */
    private int mItemId;

    /**
     * 菜单标题
     */
    private CharSequence mTitle;

    /**
     * 标题颜色
     */
    private int mTitleColor;

    /**
     * Icon
     */
    private Drawable mDrawable;

    /**
     * 背景
     */
    private Drawable mBackgroundDrawable;

    /**
     * 是否可见
     */
    private boolean mVisible = true;

    /**
     * 是否启用
     */
    private boolean mEnabled = true;

    /**
     * 当menu点击时回调
     */
    private OnClickMenuListener mListener;

    public MenuInfo() {
    }

    public MenuInfo(int icon, String title) {
        mDrawable = AppResources.getDrawable(icon);
        mTitle = title;
    }

    public MenuInfo(CharSequence name, OnClickMenuListener l) {
        this(name, BLUE, l);
    }

    public MenuInfo(CharSequence name, int color, OnClickMenuListener l) {
        mTitle = name;
        mTitleColor = color;
        mListener = l;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        mBackgroundDrawable = drawable;
    }

    public Drawable getBackgroundDrawable() {
        return mBackgroundDrawable;
    }


    public void setItemId(int itemId) {
        mItemId = itemId;
    }

    public int getItemId() {
        return mItemId;
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public void setTitleColor(int color) {
        mTitleColor = color;
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public void setIcon(Drawable icon) {
        mDrawable = icon;
    }

    public void setIcon(int resId) {
        setIcon(AppResources.getResources().getDrawable(resId));
    }

    public Drawable getIcon() {
        return mDrawable;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setOnClickMenuListener(OnClickMenuListener l) {
        mListener = l;
    }

    public OnClickMenuListener getOnClickMenuListener() {
        return mListener;
    }

    /**
     * 确认点击
     *
     * @param position
     */
    public void performClick(int position) {
        if (mListener != null) {
            mListener.onClick(this, position);
        }
    }
}
