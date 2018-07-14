package com.agmbat.text;

import java.io.UnsupportedEncodingException;

/**
 * 字符串处理工具类
 */
public class StringUtils {

    /**
     * utf-8编码
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 判断字符串是否为空
     *
     * @param text 待检查的字符串
     * @return 如果字符串为 ""，则返回 true, 否则返回 false
     */
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    /**
     * Get UTF-8 bytes of a string.
     *
     * @param text the string.
     * @return the UTF-8 bytes of the string.
     */
    public static byte[] getUtf8Bytes(String text) {
        return getBytes(text, UTF_8);
    }

    /**
     * 将字符串编码为byte数组
     *
     * @param text        需要编码的字符串
     * @param charsetName 指定编码类型
     * @return The resultant byte array
     */
    public static byte[] getBytes(String text, String charsetName) {
        if (text == null) {
            return null;
        }
        try {
            return text.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(charsetName + "  is not supported.");
        }
    }

}
