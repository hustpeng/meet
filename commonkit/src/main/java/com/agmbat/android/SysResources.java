package com.agmbat.android;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 提供系统的资源
 */
public class SysResources {

    /**
     * System package name
     */
    private static final String SYS_PACKAGE_NAME = "android";

    /**
     * Resources of System
     */
    private static final Resources SYS_RES = Resources.getSystem();

    public static DisplayMetrics getDisplayMetrics() {
        return SYS_RES.getDisplayMetrics();
    }

    /**
     * 获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight() {
        int id = getSysIdentifier("status_bar_height", "dimen");
        return SYS_RES.getDimensionPixelSize(id);
    }

    /**
     * 获取导航栏高度，有些没有虚拟导航栏的手机也能获取到，建议先判断是否有虚拟按键
     */
    public static int getNavigationBarHeight() {
        int id = getSysIdentifier("navigation_bar_height", "dimen");
        return id > 0 ? SYS_RES.getDimensionPixelSize(id) : 0;
    }

    public static int getSysDrawableId(String name) {
        return getSysIdentifier(name, "drawable");
    }

    public static Drawable getSysDrawable(String name) {
        return SYS_RES.getDrawable(getSysDrawableId(name));
    }

    public static int getSysLayoutId(String name) {
        return getSysIdentifier(name, "layout");
    }

    public static int getSysColorId(String name) {
        return getSysIdentifier(name, "color");
    }

    /**
     * Return a resource identifier of the system for the given resource name.
     *
     * @param name    The name of the desired resource.
     * @param defType Optional default resource type to find, if "type/" is not included in the name. Can be null to
     *                require an explicit type.
     * @return int The associated resource identifier. Returns 0 if no such resource was found. (0 is not a valid
     * resource ID.
     */
    private static int getSysIdentifier(String name, String defType) {
        return SYS_RES.getIdentifier(name, defType, SYS_PACKAGE_NAME);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density. NEEDS UTILS TO BE INITIALIZED
     * BEFORE USAGE.
     *
     * @param value A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dipToPixel(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getDisplayMetrics());
    }

}
