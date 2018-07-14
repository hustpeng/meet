package com.agmbat.picker.wheelview;

import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;

/**
 * 选中项的分割线
 */
public class DividerConfig {

    public static final float FILL = 0f;
    public static final float WRAP = 1f;
    protected boolean visible = true;
    protected boolean shadowVisible = false;
    protected int color = WheelView.DIVIDER_COLOR;
    protected int shadowColor = WheelView.TEXT_COLOR_NORMAL;
    protected int shadowAlpha = 100;
    protected int alpha = WheelView.DIVIDER_ALPHA;
    protected float ratio = 0.1f;
    protected float thick = WheelView.DIVIDER_THICK;

    public DividerConfig() {
        super();
    }

    public DividerConfig(@FloatRange(from = 0, to = 1) float ratio) {
        this.ratio = ratio;
    }

    /**
     * 线是否可见
     */
    public DividerConfig setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * 阴影是否可见
     */
    public DividerConfig setShadowVisible(boolean shadowVisible) {
        this.shadowVisible = shadowVisible;
        if (shadowVisible && color == WheelView.DIVIDER_COLOR) {
            color = shadowColor;
            alpha = 255;
        }
        return this;
    }

    /**
     * 阴影颜色
     */
    public DividerConfig setShadowColor(@ColorInt int color) {
        shadowVisible = true;
        shadowColor = color;
        return this;
    }

    /**
     * 阴影透明度
     */
    public DividerConfig setShadowAlpha(@IntRange(from = 1, to = 255) int alpha) {
        this.shadowAlpha = alpha;
        return this;
    }

    /**
     * 线颜色
     */
    public DividerConfig setColor(@ColorInt int color) {
        this.color = color;
        return this;
    }

    /**
     * 线透明度
     */
    public DividerConfig setAlpha(@IntRange(from = 1, to = 255) int alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * 线比例，范围为0-1,0表示最长，1表示最短
     */
    public DividerConfig setRatio(@FloatRange(from = 0, to = 1) float ratio) {
        this.ratio = ratio;
        return this;
    }

    /**
     * 线粗
     */
    public DividerConfig setThick(float thick) {
        this.thick = thick;
        return this;
    }

    @Override
    public String toString() {
        return "visible=" + visible + ",color=" + color + ",alpha=" + alpha + ",thick=" + thick;
    }

}
