/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2017-02-19
 */
package com.agmbat.text;

/**
 * 文件下载速度格式化文本
 */
public class SpeedFormat {

    private static final int CONVERSION_FACTOR = 1 << 10;

    private static final String B_S = "B/s";
    private static final String KB_S = "KB/s";
    private static final String MB_S = "MB/s";

    /**
     * 传进来到的速度是 B/s
     *
     * @param speed
     * @return
     */
    public static String formatSpeed(float speed) {
        if (speed < CONVERSION_FACTOR) {
            return String.format("%.2f", speed) + B_S;
        }
        float k = speed / CONVERSION_FACTOR;
        if (k < CONVERSION_FACTOR) {
            return String.format("%.2f", k) + KB_S;
        }
        k = k / CONVERSION_FACTOR;
        return String.format("%.2f", k) + MB_S;
    }

    public static String formatSpeed(long size, long time) {
        if (size <= 500) {
            return "0KB/s";
        }
        if (time <= 0) {
            time = 1500;
        }
        float v = (float) size * 1000 / time;
        return formatSpeed(v);
    }
}