package com.agmbat.meetyou.helper;

import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

public class AvatarHelper {

    /**
     * 图片显示参数
     */
    private static DisplayImageOptions sOptions = null;

    public static DisplayImageOptions getOptions() {
        if (sOptions == null) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            // builder.showImageOnLoading(R.drawable.ic_stub);
            int drawableId = R.drawable.ic_default_avatar;
            builder.showImageForEmptyUri(drawableId);
            builder.showImageOnFail(drawableId);
            builder.cacheInMemory(true);
            builder.cacheOnDisk(true);
            builder.considerExifParams(false);
//            builder.displayer(new SimpleBitmapDisplayer()); // new RoundedBitmapDisplayer(20)
            builder.displayer(new CircleBitmapDisplayer()); // new RoundedBitmapDisplayer(20)
            sOptions = builder.build();
        }
        return sOptions;
    }
}
