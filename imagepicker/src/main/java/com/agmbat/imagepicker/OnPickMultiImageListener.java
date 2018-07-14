package com.agmbat.imagepicker;

import com.agmbat.imagepicker.bean.ImageItem;

import java.util.List;

/**
 * 当选择多张图片后的回调
 */
public interface OnPickMultiImageListener {

    public void onPickImage(List<ImageItem> imageList);
}
