/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.text;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 */
public class StringUtils {

    public static final String UTF_8 = "UTF-8";
    public static final String ASCII = "ASCII";

    /**
     * 空白字符。
     */
    private static final char[] WHITE_SPACE_CHARS = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004',
            '\u0005', '\u0006', '\u0007', '\u0008', '\u0009', '\n', '\u000b', '\u000c', '\r', '\u000e', '\u000f',
            '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019',
            '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '\u0020'

    };

    /**
     * 判断一个字符串是否为空
     *
     * @param value 待检查的字符串
     * @return 如果字符串为 ""，则返回 true；否则返回 false。
     */
    public static boolean isEmpty(CharSequence value) {
        return value == null || value.length() == 0;
    }

    /**
     * 判断一个字符串是否为 null 或者 ""。
     *
     * @param value 待检查的字符串 。
     * @return 如果字符串为 null 或者 ""，则返回 true；否则返回 false。
     */
    public static boolean isNullOrEmpty(String value) {
        return isEmpty(value) || "null".equalsIgnoreCase(value);
    }

    public static boolean isWhitespaceCharSequence(CharSequence chars) {
        if (chars == null || chars.length() == 0) {
            return true;
        }
        for (int i = chars.length() - 1; i >= 0; i--) {
            char c = chars.charAt(i);
            if (!containsChar(WHITE_SPACE_CHARS, c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一个字符串是否为 null 或者只包含空白字符（空格、回车、换行等）。
     *
     * @param value 待检查的字符串 。
     * @return 如果字符串为 null 或者只包含空白字符，则返回 true；否则返回 false。
     */
    public static boolean isNullOrWhitespaces(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * 判断一个字符串是否只包含空白字符（空格、回车、换行等）。
     *
     * @param value 如果字符串只包含空白字符，则返回 true；否则返回 false。
     * @return 待检查的字符串 。
     */
    public static boolean isWhitespaces(String value) {
        return value != null && value.trim().length() == 0;
    }

    /**
     * 规范化字符串。
     *
     * @param s 要规范化的字符串。
     * @return 如果字符串为 null 或者长度大于零，则返回 s；如果字符串长度为零，则返回 null。
     */
    public static String normalize(String s) {
        if (s == null || s.length() > 0) {
            return s;
        }
        return null;
    }

    public static String ensureNotNull(String value) {
        return value == null ? "" : value;
    }


    /**
     * Determine whether two strings are equal, ignoring case.
     *
     * @param a the first object.
     * @param b the second object.
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        if (a != null && b != null) {
            return a.equalsIgnoreCase(b);
        }
        return a == b;
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * 去除字符串中所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
     *
     * @param text
     * @return
     */
    public static String removeAllBlank(String text) {
        if (text == null) {
            return null;
        }
        String result = text.replaceAll("[\\s*|\t|\r|\n|　]", "");
        return result;
    }

    /**
     * 去除特殊字符
     *
     * @param text
     * @return
     */
    public static String removeSpecilChar(String text) {
        if (text == null) {
            return null;
        }
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        return text.replaceAll(regEx, "");
    }


    /**
     * 以空白符分隔字符串
     *
     * @param text
     * @return
     */
    public static String[] splitWhitespace(String text) {
        if (text == null) {
            return null;
        }
        return text.split("\\s{1,}");
    }

    /**
     * 将首字母转为大写
     */
    public static String toUpperCaseFirstLetter(String text) {
        if (!isEmpty(text)) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    /**
     * 判断一个字符串是否全为小写
     */
    public static boolean isLowerCase(String text) {
        char[] array = text.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (!Character.isLowerCase(array[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含中文
     *
     * @param text
     * @return
     */
    public static boolean containChinese(String text) {
        if (isEmpty(text)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    // string regexstr = @"<[^>]*>"; //去除所有的标签
    // @"<script[^>]*?>.*?</script>" //去除所有脚本，中间部分也删除
    // string regexstr = @"<img[^>]*>"; //去除图片的正则
    // string regexstr = @"<(?!br).*?>"; //去除所有标签，只剩br
    // string regexstr = @"<table[^>]*?>.*?</table>"; //去除table里面的所有内容
    // string regexstr = @"<(?!img|br|p|/p).*?>"; //去除所有标签，只剩img,br,p
    // 去掉所有html标签
    public static String fromHtml(String html) {
        if (!isEmpty(html)) {
            return html.replaceAll("<[^>]*>", "");
        }
        return html;
    }

    /**
     * Html-decode the string.
     *
     * @param str the string to be decode
     * @return the decode string
     */
    public static String htmlDecode(String str) {
        String text = str;
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");
        text = text.replaceAll("&apos;", "\'");
        return text;
    }

    /**
     * @return String with special XML characters escaped.
     */
    public static String escapeXml(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = s.length(); i < len; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#039;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String grep(String text, String grep) {
        if (isEmpty(text)) {
            return text;
        }
        StringBuilder builder = new StringBuilder();
        String[] array = text.split("\n");
        for (String str : array) {
            if (str.contains(grep)) {
                builder.append(str).append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * 将给定的二进制数据转换为十六进制字符串。
     *
     * @param data 要转换的二进制数据。
     * @return data 的十六进制字符串表示。
     */
    public static String asHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 将给定的二进制数据转换为十六进制字符串,字节之间使用符号:分隔。
     *
     * @param data 要转换的二进制数据。
     * @return data 的十六进制字符串表示。
     */
    public static String bytesToString(final byte[] data) {
        StringBuilder builder = new StringBuilder(data.length * 2);
        for (byte b : data) {
            if (builder.length() > 0) {
                builder.append(":");
            }
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    /**
     * 将给定的十六进制字符串转换为二进制数据。
     *
     * @param hexString 要转换的十六进制字符串。
     * @return hexString 的字节数组表示。
     */
    public static byte[] fromHexString(String hexString) {
        if (isEmpty(hexString)) {
            return new byte[0];
        } else if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("The length of the hex string must be 2x.");
        }
        final int size = hexString.length() / 2;
        final byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return result;
    }

    /**
     * 将整型数据(0-15)转化为(0-f),其它数据返回' '
     *
     * @param value value
     * @return char hex
     */
    public static char binaryToHex(int value) {
        if (value >= 0 && value <= 9) {
            return (char) (value + '0');
        } else if (value >= 10 && value <= 15) {
            return (char) ((value - 10) + 'a');
        }
        return ' ';
    }

    /**
     * 移除给定字符串开头和结尾处的空白字符。
     *
     * @param value 要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static String trim(String value) {
        return trim(value, WHITE_SPACE_CHARS);
    }

    /**
     * 移除给定字符串开头和结尾处的特定字符。
     *
     * @param value 要修剪的字符串。
     * @param chars 要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trim(String value, char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int startIndex = 0;
        int endIndex = value.length() - 1;
        boolean flag = containsChar(chars, value.charAt(startIndex));
        while (flag && startIndex <= endIndex) {
            startIndex++;
            flag = containsChar(chars, value.charAt(startIndex));
        }

        flag = containsChar(chars, value.charAt(endIndex));
        while (flag && startIndex <= endIndex) {
            endIndex--;
            flag = containsChar(chars, value.charAt(endIndex));
        }
        if (startIndex >= endIndex) {
            return "";
        }
        return value.substring(startIndex, endIndex + 1);
    }

    /**
     * @param value
     * @return
     */
    public static String trimStart(CharSequence value) {
        if (isEmpty(value)) {
            return "";
        }
        String text = value.toString();
        return trimStart(text);
    }

    /**
     * 移除给定字符串开头的空白字符。
     *
     * @param value 要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static String trimStart(String value) {
        return trimStart(value, WHITE_SPACE_CHARS);
    }

    /**
     * 移除给定字符串开头的特定字符。
     *
     * @param value 要修剪的字符串。
     * @param chars 要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trimStart(String value, char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int startIndex = 0;
        boolean flag = containsChar(chars, value.charAt(startIndex));
        while (flag) {
            startIndex++;
            flag = containsChar(chars, value.charAt(startIndex));
        }
        if (startIndex >= value.length()) {
            return "";
        }
        return value.substring(startIndex);
    }

    /**
     * 移除给定字符串结尾的空白字符。
     *
     * @param value 要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static String trimEnd(String value) {
        return trimEnd(value, WHITE_SPACE_CHARS);
    }

    /**
     * 移除给定字符串结尾处的特定字符。
     *
     * @param value 要修剪的字符串。
     * @param chars 要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trimEnd(String value, char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int endIndex = value.length() - 1;
        boolean flag = containsChar(chars, value.charAt(endIndex));
        while (flag) {
            endIndex--;
            flag = containsChar(chars, value.charAt(endIndex));
        }
        if (0 >= endIndex) {
            return "";
        }
        return value.substring(0, endIndex + 1);
    }

    public static String neutralFormat(String format, Object... args) {
        return String.format(new Locale("", ""), format, args);
    }

    /**
     * 判断给定的字符数组（已排序）中是否包含给定的字符。
     *
     * @param chars 已经排序的字符数组。
     * @param ch    要检查的字符。
     * @return 如果 ch 存在于 chars 中则返回 true；否则返回 false。
     */
    public static boolean containsChar(char[] chars, char ch) {
        return Arrays.binarySearch(chars, ch) >= 0;
    }

    /**
     * Get UTF-8 or default bytes of a string.
     *
     * @param s the string.
     * @return the UTF-8 or default bytes of the string.
     */
    public static byte[] getUtf8OrDefaultBytes(String s) {
        if (null == s) {
            throw new IllegalArgumentException("s may not be null.");
        }
        try {
            return s.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    /**
     * Creates an string using UTF-8 or default encoding.
     *
     * @return a string in UTF-8 or default encoding.
     */
    public static String newUtf8OrDefaultString(byte[] data, int offset, int length) {
        if (null == data) {
            throw new IllegalArgumentException("data may not be null.");
        }
        try {
            return new String(data, offset, length, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }

    /**
     * Creates an string using UTF-8 or default encoding.
     *
     * @return a string in UTF-8 or default encoding.
     */
    public static String newUtf8OrDefaultString(byte[] data) {
        return newUtf8OrDefaultString(data, 0, data.length);
    }

    /**
     * Creates an string using UTF-8 encoding.
     *
     * @return a string in UTF-8 encoding.
     */
    public static String newUtf8String(byte[] data) {
        if (null == data) {
            throw new IllegalArgumentException("data may not be null.");
        }
        return newUtf8String(data, 0, data.length);
    }

    /**
     * Creates an string using UTF-8 encoding.
     *
     * @return a string in UTF-8 encoding.
     */
    public static String newUtf8String(byte[] data, int offset, int length) {
        return newString(data, offset, length, UTF_8);
    }

    /**
     * Creates an string using ASCII encoding.
     *
     * @return a string in ASCII encoding.
     */
    public static String newAsciiString(byte[] data) {
        if (null == data) {
            throw new IllegalArgumentException("data may not be null.");
        }
        return newAsciiString(data, 0, data.length);
    }

    /**
     * Creates an string using ASCII encoding.
     *
     * @param s the string.
     * @return the ASCII bytes of the string.
     */
    public static String newAsciiString(byte[] data, int offset, int length) {
        return newString(data, offset, length, ASCII);
    }

    public static String newString(byte[] data, int offset, int length, String charsetName) {
        try {
            return new String(data, offset, length, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(charsetName + " is not supported.");
        }
    }

    /**
     * Get UTF-8 bytes of a string.
     *
     * @param s the string.
     * @return the UTF-8 bytes of the string.
     */
    public static byte[] getUtf8Bytes(String s) {
        return getBytes(s, UTF_8);
    }

    /**
     * Get ASCII bytes of a string.
     *
     * @param s the string.
     * @return the ASCII bytes of the string.
     */
    public static byte[] getAsciiBytes(String s) {
        return getBytes(s, ASCII);
    }

    public static byte[] getBytes(String text, String charsetName) {
        if (text == null) {
            return null;
        }
        try {
            return text.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(charsetName + "  is not supported.");
        }
    }

    public static String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    /**
     * 拼接字符串
     *
     * @param array
     * @return
     */
    public static String concat(String... array) {
        StringBuilder builder = new StringBuilder();
        if (array != null) {
            for (String str : array) {
                if (str != null) {
                    builder.append(str);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 查找字符串，找到返回，没找到返回空
     */
    public static String findString(String search, String start, String end) {
        int startLen = start.length();
        int startPos = isEmpty(start) ? 0 : search.indexOf(start);
        if (startPos > -1) {
            int endPos = isEmpty(end) ? -1 : search.indexOf(end, startPos + startLen);
            if (endPos > -1) {
                return search.substring(startPos + start.length(), endPos);
            }
        }
        return "";
    }

    /**
     * 截取字符串
     *
     * @param search       待搜索的字符串
     * @param start        起始字符串 例如：<title>
     * @param end          结束字符串 例如：</title>
     * @param defaultValue
     * @return
     */
    public static String substring(String search, String start, String end, String defaultValue) {
        int startLen = start.length();
        int startPos = isEmpty(start) ? 0 : search.indexOf(start);
        if (startPos > -1) {
            int endPos = isEmpty(end) ? -1 : search.indexOf(end, startPos + startLen);
            if (endPos > -1) {
                return search.substring(startPos + start.length(), endPos);
            } else {
                return search.substring(startPos + start.length());
            }
        }
        return defaultValue;
    }

    /**
     * 截取字符串
     *
     * @param search 待搜索的字符串
     * @param start  起始字符串 例如：<title>
     * @param end    结束字符串 例如：</title>
     * @return
     */
    public static String substring(String search, String start, String end) {
        return substring(search, start, end, "");
    }

    /**
     * 将字符串补齐到指定长度
     *
     * @param source
     * @param tarLength
     * @param filledChar
     * @return
     */
    public static String fill(String source, int tarLength, char filledChar) {
        if (StringUtils.isEmpty(source)) {
            return "";
        }
        int len = tarLength - source.length();
        if (len <= 0) {
            return source;
        }
        StringBuilder builder = new StringBuilder(source);
        for (int i = 0; i < len; i++) {
            builder.append(filledChar);
        }
        return builder.toString();
    }

    /**
     * Helper function for making null strings safe for comparisons, etc.
     *
     * @return (s = = null) ? "" : s;
     */
    public static String makeSafe(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * 将所有字符为160空格转为32的空格
     *
     * @param text
     * @return
     */
    public static String replaceSpace(String text) {
        // text = text.replace((char) 160, ' ');
        return text.replaceAll("\\u00A0", " ");
    }
}
