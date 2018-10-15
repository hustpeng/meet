package com.agmbat.meetyou.helper;

import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

public class AvatarHelper {

    /**
     * 用户头像显示参数
     */
    private static DisplayImageOptions sOptions = null;

    /**
     * 群头像显示参数
     */
    private static DisplayImageOptions sGroupOptions = null;

    /**
     * 方形头像参数
     */
    private static DisplayImageOptions sRectAngleOptions = null;

    public static DisplayImageOptions getOptions() {
        if (sOptions == null) {
            sOptions = buildUserOptions();
        }
        return sOptions;
    }


    /**
     * 获取头像群显示参数
     *
     * @return
     */
    public static DisplayImageOptions getGroupOptions() {
        if (sGroupOptions == null) {
            sGroupOptions = buildGroupOptions();
        }
        return sGroupOptions;
    }

    public static DisplayImageOptions getRectangleUserOptions() {
        if (sRectAngleOptions == null) {
            sRectAngleOptions = buildRectAngleOptions(R.drawable.ic_default_avatar);
        }
        return sRectAngleOptions;
    }

    /**
     * 创建头像群显示参数
     *
     * @return
     */
    private static DisplayImageOptions buildGroupOptions() {
        return buildOptions(R.drawable.ic_default_circle_avatar);
    }


    /**
     * 创建用户显头像显示参数
     *
     * @return
     */
    private static DisplayImageOptions buildUserOptions() {
        return buildOptions(R.drawable.ic_default_avatar);
    }

    /**
     * 获取方形头像参数
     *
     * @param defaultDrawable
     * @return
     */
    public static DisplayImageOptions buildRectAngleOptions(int defaultDrawable) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageForEmptyUri(defaultDrawable);
        builder.showImageOnFail(defaultDrawable);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(false);
        return builder.build();
    }

    private static DisplayImageOptions buildOptions(int defaultDrawableId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageForEmptyUri(defaultDrawableId);
        builder.showImageOnFail(defaultDrawableId);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(false);
        builder.displayer(new CircleBitmapDisplayer());
        return builder.build();
    }
}
