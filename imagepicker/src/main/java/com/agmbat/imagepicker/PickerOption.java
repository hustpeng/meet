package com.agmbat.imagepicker;

import com.agmbat.imagepicker.view.CropImageView;

/**
 * 图片选择相关界面参数配置
 */
public class PickerOption {

    /**
     * 是否显示拍照
     */
    private boolean mShowCamera = true;

    /**
     * 裁剪参数
     */
    private CropParams mCropParams;

    public boolean isShowCamera() {
        return mShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        mShowCamera = showCamera;
    }

    public CropParams getCropParams() {
        return mCropParams;
    }

    public void setCropParams(CropParams cropParams) {
        mCropParams = cropParams;
    }


    /**
     * 裁剪参数
     */
    public static class CropParams {

        /**
         * 裁剪框的形状
         */
        private CropImageView.Style style = CropImageView.Style.RECTANGLE;

        /**
         * 裁剪保存宽度
         */
        private int outPutX = 800;

        /**
         * 裁剪保存高度
         */
        private int outPutY = 800;

        /**
         * 焦点框的宽度
         */
        private int focusWidth = 280;

        /**
         * 焦点框的高度
         */
        private int focusHeight = 280;

        public CropImageView.Style getStyle() {
            return style;
        }

        public void setStyle(CropImageView.Style style) {
            this.style = style;
        }

        public int getOutPutX() {
            return outPutX;
        }

        public void setOutPutX(int outPutX) {
            this.outPutX = outPutX;
        }

        public int getOutPutY() {
            return outPutY;
        }

        public void setOutPutY(int outPutY) {
            this.outPutY = outPutY;
        }

        public int getFocusWidth() {
            return focusWidth;
        }

        public void setFocusWidth(int focusWidth) {
            this.focusWidth = focusWidth;
        }

        public int getFocusHeight() {
            return focusHeight;
        }

        public void setFocusHeight(int focusHeight) {
            this.focusHeight = focusHeight;
        }


//        ImagePicker imagePicker;
//
//        public void a() {
//            imagePicker.setStyle(CropImageView.Style.RECTANGLE); //裁剪框的形状
//            imagePicker.setFocusWidth(800); //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//            imagePicker.setFocusHeight(800); //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//            imagePicker.setOutPutX(200);//保存文件的宽度。单位像素
//            imagePicker.setOutPutY(200);//保存文件的高度。单位像素 }
//        }

    }
}
