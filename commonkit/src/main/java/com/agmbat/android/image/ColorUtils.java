package com.agmbat.android.image;

/**
 * Color常用工具类
 */
public class ColorUtils {

    /**
     * 灰度值计算
     *
     * @param pixels 像素
     * @return int 灰度值
     */
    public static int rgbToGray(int pixels) {
        // int alpha = (pixels >> 24) & 0xFF;
        int red = (pixels >> 16) & 0xFF;
        int green = (pixels >> 8) & 0xFF;
        int blue = (pixels) & 0xFF;
        return (int) (0.3 * red + 0.59 * green + 0.11 * blue);
    }

    public static int getRGB(int color) {
        return color | 0xFF000000;
    }

    /**
     * 是否为不透明
     *
     * @param color
     * @return
     */
    public static boolean isOpaque(int color) {
        return color >>> 24 == 0xFF;
    }
}