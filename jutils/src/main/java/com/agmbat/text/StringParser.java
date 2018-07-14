package com.agmbat.text;

import com.agmbat.log.Log;

/**
 * 将String解析为基本数据类型
 */
public class StringParser {

    private static final String TAG = StringParser.class.getSimpleName();

    /**
     * 将给定的字符串解析为int型, 如果解析失败，默认返回0
     *
     * @param content
     * @return
     */
    public static int parseInt(String content) {
        return parseInt(content, 0);
    }

    /**
     * 将给定的字符串解析为int型, 如果解析失败，返回给定的默认值
     *
     * @param content
     * @return
     */
    public static int parseInt(String content, int defaultValue) {
        if (StringUtils.isEmpty(content)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(content);
        } catch (NumberFormatException e) {
            Log.e(TAG, "parse int error, content:" + content);
        }
        return defaultValue;
    }

    /**
     * 将给定的字符串解析为long型, 如果解析失败，默认返回0
     *
     * @param content
     * @return
     */
    public static long parseLong(String content) {
        return parseLong(content, 0);
    }

    /**
     * 将给定的字符串解析为long型, 如果解析失败，返回给定的默认值
     *
     * @param content
     * @return
     */
    public static long parseLong(String content, long defaultValue) {
        if (StringUtils.isEmpty(content)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(content);
        } catch (NumberFormatException e) {
            Log.e(TAG, "parseLong error, content:" + content);
        }
        return defaultValue;
    }

    /**
     * 将给定的字符串解析为float型, 如果解析失败，默认返回0
     *
     * @param content
     * @return
     */
    public static float parseFloat(String content) {
        return parseFloat(content, 0);
    }

    /**
     * 将给定的字符串解析为float型, 如果解析失败，返回给定的默认值
     *
     * @param content
     * @return
     */
    public static float parseFloat(String content, float defaultValue) {
        if (StringUtils.isEmpty(content)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(content);
        } catch (NumberFormatException e) {
            Log.e(TAG, "parse float error, content:" + content);
        }
        return defaultValue;
    }

    /**
     * 将给定的字符串解析为double型, 如果解析失败，默认返回0
     *
     * @param content
     * @return
     */
    public static double parseDouble(String content) {
        return parseDouble(content, 0);
    }

    /**
     * 将给定的字符串解析为double型, 如果解析失败，返回给定的默认值
     *
     * @param content
     * @return
     */
    public static double parseDouble(String content, double defaultValue) {
        if (StringUtils.isEmpty(content)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(content);
        } catch (NumberFormatException e) {
            Log.e(TAG, "parse double error, content:" + content);
        }
        return defaultValue;
    }

}
