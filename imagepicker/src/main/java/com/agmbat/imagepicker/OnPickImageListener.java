package com.agmbat.imagepicker;

import com.agmbat.imagepicker.bean.ImageItem;

/**
 * 当选择图片后的回调
 */
public interface OnPickImageListener {

    public void onPickImage(ImageItem imageItem);
}
