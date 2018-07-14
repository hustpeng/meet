package com.agmbat.imagepicker;

import android.content.Context;
import android.content.Intent;

import com.agmbat.android.transit.ActionHelper;
import com.agmbat.android.transit.PendingAction;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.loader.UILImageLoader;
import com.agmbat.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;

/**
 * 选择图片
 */
public class ImagePickerHelper {

    /**
     * 拍照
     *
     * @param context
     * @param listener
     */
    public static void takePicture(Context context, OnPickImageListener listener) {
        ActionHelper.request(context, new TakePictureAction(listener));
    }

    /**
     * 选择一张l图片
     */
    public static void pickImage(Context context, OnPickImageListener listener) {
        PickerOption option = new PickerOption();
        option.setShowCamera(false);
        pickImage(context, option, listener);
    }

    /**
     * 选择一张图片, 业务配置相关参数
     *
     * @param context
     * @param option
     * @param listener
     */
    public static void pickImage(Context context, PickerOption option, OnPickImageListener listener) {
        ActionHelper.request(context, new ImageAction(option, listener));
    }

    /**
     * 选取多张图片
     *
     * @param context
     * @param listener
     */
    public static void pickMultiImage(Context context, OnPickMultiImageListener listener) {
        ActionHelper.request(context, new MultiImageAction(listener));
    }

    /**
     * 选择单张图片
     */
    private static class ImageAction implements PendingAction {

        private OnPickImageListener mOnPickImageListener;
        private PickerOption mOption;

        public ImageAction(PickerOption option, OnPickImageListener l) {
            mOption = option;
            mOnPickImageListener = l;
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    if (images.size() > 0) {
                        ImageItem imageItem = images.get(0);
                        if (mOnPickImageListener != null) {
                            mOnPickImageListener.onPickImage(imageItem);
                        }
                    }
                }
            }
        }

        @Override
        public Intent getActionIntent(Context context) {
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setImageLoader(new UILImageLoader());
            imagePicker.setPickerOption(mOption); // 显示拍照按钮
            imagePicker.setSelectLimit(1); // 选中数量限制
            imagePicker.setMultiMode(false); // 设置为单选

            PickerOption.CropParams cropParams = mOption.getCropParams();
            if (cropParams != null) {
                //允许裁剪（单选才有效）
                imagePicker.setCrop(true);
                //是否按矩形区域保存
                imagePicker.setSaveRectangle(true);
            } else {
                imagePicker.setCrop(false);
                imagePicker.setSaveRectangle(false);
            }
            return new Intent(context, ImageGridActivity.class);
        }
    }

    private static class MultiImageAction implements PendingAction {

        private OnPickMultiImageListener mOnPickImageListener;

        public MultiImageAction(OnPickMultiImageListener l) {
            mOnPickImageListener = l;
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    if (mOnPickImageListener != null) {
                        mOnPickImageListener.onPickImage(images);
                    }
                }
            }
        }

        @Override
        public Intent getActionIntent(Context context) {
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setImageLoader(new UILImageLoader());
            PickerOption option = new PickerOption();
            // 不显示拍照按钮
            option.setShowCamera(false);
            imagePicker.setPickerOption(option);
            imagePicker.setCrop(false); // 允许裁剪（单选才有效）
            imagePicker.setSaveRectangle(false); // 是否按矩形区域保存
            imagePicker.setSelectLimit(9); // 选中数量限制
            imagePicker.setMultiMode(true); // 设置为单选
            Intent intent = new Intent(context, ImageGridActivity.class);
            return intent;
        }
    }

    private static class TakePictureAction implements PendingAction {

        private OnPickImageListener mOnPickImageListener;

        public TakePictureAction(OnPickImageListener l) {
            mOnPickImageListener = l;
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    if (images.size() > 0) {
                        ImageItem imageItem = images.get(0);
                        if (mOnPickImageListener != null) {
                            mOnPickImageListener.onPickImage(imageItem);
                        }
                    }
                }
            }
        }

        @Override
        public Intent getActionIntent(Context context) {
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setImageLoader(new UILImageLoader());
            PickerOption option = new PickerOption();
            option.setShowCamera(false);
            imagePicker.setPickerOption(option);
            imagePicker.setSelectLimit(1); // 选中数量限制
            imagePicker.setMultiMode(false); // 设置为单选
            imagePicker.setCrop(false); //允许裁剪（单选才有效）
            Intent intent = new Intent(context, ImageGridActivity.class);
            // 是否是直接打开相机
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true);
            return intent;
        }
    }
}
