package com.agmbat.meetyou.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.agmbat.meetyou.MeetApplication;


/**
 * 一个方便在全局根据资源ID获取资源的工具类
 */

public class ResourceUtil {

    private static final String RES_ID = "id";
    private static final String RES_STRING = "string";
    private static final String RES_DRABLE = "drawable";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STYLE = "style";
    private static final String RES_COLOR = "color";
    private static final String RES_DIMEN = "dimen";
    private static final String RES_ANIM = "anim";
    private static final String RES_MENU = "menu";

    /**
     * 获取文本内容
     *
     * @param resId
     * @param args
     * @return
     */
    public static String getString(int resId, Object... args) {
        String text = "";
        try {
            text = MeetApplication.getInstance().getString(resId, args);
        } catch (Exception e) {
        }
        return text;
    }

    /**
     * 获取颜色值
     *
     * @param resId
     * @return
     */
    public static int getColor(int resId) {
        int color = 0;
        try {
            color = ContextCompat.getColor(MeetApplication.getInstance(), resId);
        } catch (Exception e) {
        }
        return color;
    }

    /**
     * 获取尺寸值
     *
     * @param resId
     * @return
     */
    public static float getDimension(int resId) {
        float dimen = 0f;
        try {
            dimen = MeetApplication.getInstance().getResources().getDimension(resId);
        } catch (Exception e) {
        }
        return dimen;
    }

    /**
     * 获取图片资源
     *
     * @param resId
     * @return
     */
    public static Drawable getDrawable(int resId) {
        Drawable drawable = null;
        try {
            drawable = ContextCompat.getDrawable(MeetApplication.getInstance(), resId);
        } catch (Exception e) {
        }
        return drawable;
    }

    /**
     * 获取图片资源
     *
     * @param resId
     * @return
     */
    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(MeetApplication.getInstance().getResources(), resId);
    }

    /**
     * 获取资源文件的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getId(Context context, String resName) {
        return getResId(context, resName, RES_ID);
    }

    /**
     * 获取资源文件string的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getStringId(Context context, String resName) {
        return getResId(context, resName, RES_STRING);
    }

    /**
     * 获取资源文件drable的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getDrawableId(Context context, String resName) {
        return getResId(context, resName, RES_DRABLE);
    }

    /**
     * 获取资源文件layout的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getLayoutId(Context context, String resName) {
        return getResId(context, resName, RES_LAYOUT);
    }

    /**
     * 获取资源文件style的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getStyleId(Context context, String resName) {
        return getResId(context, resName, RES_STYLE);
    }

    /**
     * 获取资源文件color的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getColorId(Context context, String resName) {
        return getResId(context, resName, RES_COLOR);
    }

    /**
     * 获取资源文件dimen的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getDimenId(Context context, String resName) {
        return getResId(context, resName, RES_DIMEN);
    }

    /**
     * 获取资源文件ainm的id
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getAnimId(Context context, String resName) {
        return getResId(context, resName, RES_ANIM);
    }

    /**
     * 获取资源文件menu的id
     */
    public static int getMenuId(Context context, String resName) {
        return getResId(context, resName, RES_MENU);
    }

    /**
     * 获取资源文件ID
     *
     * @param context
     * @param resName
     * @param defType
     * @return
     */
    public static int getResId(Context context, String resName, String defType) {
        int resId = 0;
        try {
            resId = context.getResources().getIdentifier(resName, defType, context.getPackageName());
        } catch (Exception e) {
        }
        return resId;
    }

}
