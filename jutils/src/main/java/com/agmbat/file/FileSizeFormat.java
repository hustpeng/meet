package com.agmbat.file;

/**
 * 文件大小格式化工具
 */
public class FileSizeFormat {

    private static final String B_UNIT2 = "B";
    private static final String KB_UNIT2 = "KB";
    private static final String MB_UNIT2 = "MB";
    private static final String GB_UNIT2 = "GB";
    private static final String TB_UNIT2 = "TB";
    private static final String PB_UNIT2 = "PB";

    /**
     * 格式化大小
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


}
