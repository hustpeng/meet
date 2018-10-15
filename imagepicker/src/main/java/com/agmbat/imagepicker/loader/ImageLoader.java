package com.agmbat.imagepicker.loader;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * 外部需要实现这个类去加载图片
 */
public interface ImageLoader extends Serializable {

    void displayImage(Context activity, String path, ImageView imageView, int width, int height);

    void displayImagePreview(Context activity, String path, ImageView imageView, int width, int height);

    void clearMemoryCache();
}
