/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2016-07-23
 */
package com.agmbat.file;

import com.agmbat.text.StringParser;
import com.agmbat.text.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件大小格式化工具
 */
public class FileSizeFormat {

    private static final int KB = 1 << 10;
    private static final int MB = 1 << 20;
    private static final int GB = 1 << 30;

    private static final long GB_UNIT_THREDHOLD = 1000000000;
    private static final long MB_UNIT_THREDHOLD = 1000000;
    private static final long KB_UNIT_THREDHOLD = 1000;

    private static final String CONVERSION_BYTE = "Byte";
    private static final String BYTES_UNIT = "Byte(s)";
    private static final String KB_UNIT = "K";
    private static final String MB_UNIT = "M";
    private static final String GB_UNIT = "G";

    private static final String B_UNIT2 = "B";
    private static final String KB_UNIT2 = "KB";
    private static final String MB_UNIT2 = "MB";
    private static final String GB_UNIT2 = "GB";
    private static final String TB_UNIT2 = "TB";
    private static final String PB_UNIT2 = "PB";

    private static final String SIZE_FORMAT = "%,.2f %s";
    private static final String SIZE_FORMAT_WITHOUT_DIGIT = "%,.0f %s";

    /**
     * Like {@link #formatFileSize}, but trying to generate shorter numbers (showing fewer digits of precisin).
     */
    public static String formatShortSize(long number) {
        return formatSize(number, true);
    }

    /**
     * Formats a content size to be in the form of bytes, kilobytes, megabytes, etc
     *
     * @param number size value to be formated
     * @return formated string with the number
     */
    public static String formatSize(long number) {
        return formatSize(number, false);
    }

    private static String formatSize(long number, boolean shorter) {
        float result = number;
        String suffix = B_UNIT2;
        if (result > 900) {
            suffix = KB_UNIT2;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = MB_UNIT2;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = GB_UNIT2;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = TB_UNIT2;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = PB_UNIT2;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            if (shorter) {
                value = String.format("%.1f", result);
            } else {
                value = String.format("%.2f", result);
            }
        } else if (result < 100) {
            if (shorter) {
                value = String.format("%.0f", result);
            } else {
                value = String.format("%.2f", result);
            }
        } else {
            value = String.format("%.0f", result);
        }
        return value + suffix;
    }

    /**
     * transform file bytes to string with unit
     *
     * @param bytes
     * @return
     */
    // public static String formatFileSize(final Context context, final long bytes) {
    // return Formatter.formatFileSize(context, bytes);
    // }

    /**
     * transform file bytes to string with unit
     *
     * @param number
     * @return
     */
    public static String formatFileSize2(long number) {
        float displayValue = number;
        String unit = BYTES_UNIT;
        String format = SIZE_FORMAT_WITHOUT_DIGIT;
        if (number > GB_UNIT_THREDHOLD) {
            displayValue /= (double) GB;
            unit = GB_UNIT;
            format = SIZE_FORMAT;
        } else if (number > MB_UNIT_THREDHOLD) {
            displayValue /= (float) MB;
            unit = MB_UNIT;
            format = SIZE_FORMAT;
        } else if (number > KB_UNIT_THREDHOLD) {
            // For KB size, we don't need to display digits.
            displayValue = number / (float) KB;
            unit = KB_UNIT;
            format = SIZE_FORMAT_WITHOUT_DIGIT;
        }
        return String.format(format, displayValue, unit);
    }

    /**
     * 转换文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize3(long size) {
        String fileSize;
        if (size < KB) {
            fileSize = size + B_UNIT2;
        } else if (size < MB) {
            fileSize = String.format("%.1f" + KB_UNIT2, size / (float) KB);
        } else if (size < GB) {
            fileSize = String.format("%.1f" + MB_UNIT2, size / (float) MB);
        } else {
            fileSize = String.format("%.1f" + GB_UNIT2, size / (double) GB);
        }
        return fileSize;
    }

    public static String formatFileSize4(long size) {
        if (size < KB) {
            return size + CONVERSION_BYTE;
        }
        float k = (size / KB);
        if (k < KB) {
            return String.format("%.2f" + KB_UNIT, k);
        }
        k = k / KB;
        return String.format("%.2f" + MB_UNIT, k);
    }

    public static int formatSizeToK(int number) {
        return number / KB;
    }

    public static String formatSizeToM(long size) {
        float s = size / MB;
        return String.format("%.2fM", s);
    }

    public static long formatTextSizeToByte(String textSize) {
        if (StringUtils.isEmpty(textSize)) {
            return 0;
        }
        String upper = textSize.toUpperCase();
        String[] unit = new String[] { "PB", "TB", "GB", "MB", "KB", "B" };
        String[] unit2 = new String[] { "P", "T", "G", "M", "K", "B" };
        for (int i = 0; i < unit.length; i++) {
            if (upper.endsWith(unit[i])) {
                String numberText = textSize.substring(0, textSize.length() - unit[i].length());
                float value = StringParser.parseFloat(numberText);
                value = (float) (value * Math.pow(1024, unit.length - i - 1));
                return (long) value;
            }
        }

        for (int i = 0; i < unit2.length; i++) {
            if (upper.endsWith(unit2[i])) {
                String numberText = textSize.substring(0, textSize.length() - unit2[i].length());
                float value = StringParser.parseFloat(numberText);
                value = (float) (value * Math.pow(1024, unit2.length - i - 1));
                return (long) value;
            }
        }
        return 0;
    }

    /**
     * @param str 12.4M
     */
    public static long formatTextSizeToByte2(String str) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("str may not be null.");
        }
        String unit = getUnits(str);
        String num = getNums(str);
        double realNum = 0;
        double[] nums = { getNum(0), getNum(1), getNum(1), getNum(2), getNum(2), getNum(3), getNum(3), getNum(4) };
        String[] units = { "B", "KB", "K", "MB", "M", "GB", "G", "TB" };
        for (int k = 0; k < units.length; k++) {
            if (unit.toUpperCase().equals(units[k])) {
                double n = Double.parseDouble(num);
                realNum = n * nums[k];
                break;
            }
        }
        return (long) realNum;
    }

    /**
     * Helper function to build the downloading text.
     */
    public static String getDownloadingText(long totalBytes, long currentBytes) {
        if (totalBytes <= 0) {
            return "0%";
        }
        long progress = currentBytes * 100 / totalBytes;
        StringBuilder sb = new StringBuilder();
        sb.append(progress);
        sb.append('%');
        return sb.toString();
    }

    /**
     * 格式化为下载 进度
     */
    public static String formatSizeForDownload(long current, long total) {
        float m = (float) MB;
        final float c = current / m; // current / 1024 / 1024
        final float t = total / m;
        return String.format("%.2fM/%.2fM", c, t);
    }

    private static String getNums(String str) {
        String num = null;
        List<String> list = getList(str);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String a = list.get(i);
            if (isNumber(a)) {
                num = a;
            }
        }
        return num;
    }

    private static String getUnits(String str) {
        String unit = null;
        List<String> list = getList(str);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String a = list.get(i);
            if (isLetter(a)) {
                unit = a;
            }
        }
        return unit;
    }

    private static double getNum(int n) {
        return Math.pow(1024, n);
    }

    private static List<String> getList(String str) {
        List<String> list = new ArrayList<String>();
        String s = "\\d+(.\\d)?|\\w+";
        Pattern pattern = Pattern.compile(s);
        Matcher ma = pattern.matcher(str);
        while (ma.find()) {
            list.add(ma.group());
        }
        return list;
    }

    private static boolean isNumber(String str) {
        return isMatch("\\d+", str);
    }

    private static boolean isLetter(String str) {
        return isMatch("\\w+", str);
    }

    private static boolean isMatch(String reg, String str) {
        Pattern pattern = Pattern.compile(reg);
        Matcher ma = pattern.matcher(str);
        return ma.find();
    }

}
