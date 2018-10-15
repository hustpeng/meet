/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-01-19
 */
package com.agmbat.time;

import com.agmbat.text.StringParser;

/**
 * 时长格式化工具
 */
public class DurationFormat {

    public static long toDuration(String hours, String mins, String secs, String msecs) {
        int h = StringParser.parseInt(hours);
        int m = StringParser.parseInt(mins);
        int s = StringParser.parseInt(secs);
        int ms = StringParser.parseInt(msecs);
        return toDuration(h, m, s, ms);
    }

    public static long toDuration(int hours, int mins, int secs, int msecs) {
        long h = hours * TimeUtils.MILLISECONDS_PER_HOUR;
        long m = mins * TimeUtils.MILLISECONDS_PER_MINUTE;
        long s = secs * TimeUtils.MILLISECONDS_PER_SECOND;
        return h + m + s + msecs;
    }

    public static String formatDuration(long duration) {
        String h = formatNumber(getHours(duration), 1);
        String m = formatNumber(getMins(duration), 2);
        String s = formatNumber(getSecs(duration), 2);
        String ms = formatNumber(getMSecs(duration), 1);
        return String.format("%s:%s:%s.%s", h, m, s, ms);
    }

    public static String formatDurationWithOutMsec(long duration) {
        String h = formatNumber(getHours(duration), 1);
        String m = formatNumber(getMins(duration), 2);
        String s = formatNumber(getSecs(duration), 2);
        return String.format("%s:%s:%s", h, m, s);
    }

    /**
     * 格式化为分秒 01:02
     */
    public static String formatDurationShort(long duration) {
        int h = getHours(duration);
        if (h == 0) {
            String m = formatNumber(getMins(duration), 2);
            String s = formatNumber(getSecs(duration), 2);
            return String.format("%s:%s", m, s);
        } else {
            return formatDurationWithOutMsec(duration);
        }
    }

    /**
     * 格式化数字
     *
     * @param num   需要格式化的数
     * @param width 格式化后数的宽度
     * @return
     */
    private static String formatNumber(int num, int width) {
        return String.format("%0" + width + "d", num);
    }

    /**
     * 格式化为分秒 01'02" ;
     *
     * @param duration
     * @return
     */
    public static String formatDurationShort2(long duration) {
        int m = getMins(duration);
        int s = getSecs(duration);
        if (m > 0) {
            return m + "'" + s + "\"";
        } else {
            return s + "\"";
        }
    }

    /**
     * 转换时间显示
     *
     * @param time 毫秒
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d",
                minutes, seconds);
    }

    /**
     * 根据秒速获取时间格式
     */
    public static String gennerTime(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private static int getHours(long duration) {
        return (int) (duration / TimeUtils.MILLISECONDS_PER_HOUR);
    }

    private static int getMins(long duration) {
        long time = duration / TimeUtils.MILLISECONDS_PER_SECOND;
        return (int) ((time % TimeUtils.MILLISECONDS_PER_SECOND) / 60);
    }

    private static int getSecs(long duration) {
        long time = duration / TimeUtils.MILLISECONDS_PER_SECOND;
        return (int) (time % 60);
    }

    private static int getMSecs(long duration) {
        return (int) (duration % TimeUtils.MILLISECONDS_PER_SECOND);
    }
}
