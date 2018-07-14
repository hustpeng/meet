/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.time;

import com.agmbat.log.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间格式化工具
 */
public class TimeUtils {

    static final long MILLISECONDS_PER_SECOND = 1000;
    static final long MILLISECONDS_PER_MINUTE = 60 * 1000;
    static final long MILLISECONDS_PER_HOUR = 60 * 60 * 1000;
    static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * 默认日期格式化
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";

    /**
     * 包含时间的格式化
     */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final SimpleDateFormat TIME_FORMATER = new SimpleDateFormat("hh:mm", Locale.CHINA);

    private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("yyyy.MM.dd EEEE", Locale.CHINA);

    private static final SimpleDateFormat DATE_FORMAT_PART = new SimpleDateFormat("HH:mm");

    public static String currentTimeString() {
        return DATE_FORMAT_PART.format(Calendar.getInstance().getTime());
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        return TIME_FORMAT.format(new Date(time));
    }

    /**
     * 获取当前日期 格式为yyyy-MM-dd 例如2011-07-08
     *
     * @return
     */
    public static String getCurrentDate() {
        return formatDate(new Date(), DEFAULT_DATE_PATTERN);
    }

    /**
     * 格式化日期字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String formatDate(long date, String pattern) {
        return formatDate(new Date(date), pattern);
    }

    public static Date parseDate(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).getTime();
    }

    /**
     * format time to yyyy-MM-dd
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    private static String formatDateText(int year, int month, int day) {
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append('-');
        if (month < 10) {
            builder.append(0);
        }
        builder.append(month);
        builder.append('-');
        if (day < 10) {
            builder.append(0);
        }
        builder.append(day);
        return builder.toString();
    }

    public static String formatHHMM(long time) {
        Date date = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat("hh:mm");
        return f.format(date);
    }

    public static String androidLogTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
        return formatter.format(new Date());
    }

    public static String getDateTime(long time) {
        Date date = new Date(time);
        // 2011-11-07 16:41:35.033
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return f.format(date);
    }

    public static boolean isToday(long time) {
        long today = System.currentTimeMillis();
        return isSameDay(time, today);
    }

    public static boolean isYesterday(long time) {
        long yesterday = System.currentTimeMillis() - MILLISECONDS_PER_DAY;
        return isSameDay(time, yesterday);
    }

    /**
     * 判断两个日期是否为同一天
     */
    public static boolean isSameDay(long ldatea, long ldateb) {
        Date dateA = new Date(ldatea);
        Date dateB = new Date(ldateb);
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);
        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断是否润年
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && year % 100 != 0) {
            return true;
        } else if (year % 400 == 0) {
            return true;
        }
        return false;
    }

    public static boolean isBeforeToady(Date date) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTimeInMillis() > date.getTime();
    }

    public static boolean isValidDate(int year, int month, int day) {
        return isValidDate(formatDateText(year, month, day));
    }

    public static boolean isValidDate(String text) {
        return parseDate(text) != null;
    }

    public static String changeLongDateToString(Date date) {
        String dateString = null;
        try {
            dateString = DATE_FORMATER.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String changeTimeToString(Date date) {
        String timeText = null;
        try {
            timeText = TIME_FORMATER.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String preText = getHourPreText(hourOfDay);
        return "—" + preText + " " + timeText;
    }

    private static String getHourPreText(int hourOfDay) {
        if (hourOfDay < 5) {
            return "凌晨";
        } else if (hourOfDay < 7) {
            return "清晨";
        } else if (hourOfDay < 9) {
            return "早上";
        } else if (hourOfDay < 12) {
            return "上午";
        } else if (hourOfDay < 14) {
            return "中午";
        } else if (hourOfDay < 17) {
            return "下午";
        } else if (hourOfDay < 19) {
            return "傍晚";
        } else if (hourOfDay < 21) {
            return "晚上";
        } else if (hourOfDay < 24) {
            return "深夜";
        }
        return "";
    }

    public static String timeToString(long time) {
        return timeToString(time, Locale.getDefault());
    }

    public static String timeToString(long time, Locale locale) {
        final String dateString = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(time);
        final String timeString = DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(time);
        final String dateTimeString = String.format("%s, %s", dateString, timeString);
        return dateTimeString;
    }

    /**
     * 将给定的时间格式化为 XSD/SOAP 的格式。
     *
     * @param date 要格式化的日期。
     * @return 该日期的 yyyy-MM-ddTHH:mm:ss+ZZ:ZZ 的格式。
     * @deprecated 请使用 {@link android.util.XmlUtil#dateToXsd(Date)} ，它提供了相同的功能。 方法。
     */
    @Deprecated
    public static final String formatDate2(final Date date) {
        final String dateTime = String.format("%1$tFT%1$tT", date);
        final String zone = String.format("%1$tz", date);
        return dateTime + zone.substring(0, 3) + ":" + zone.substring(3);
    }

    /**
     * 解析日期
     *
     * @param text
     * @return
     */
    public static Date parseDate(String text) {
        return parseDate(text, DEFAULT_DATE_PATTERN);
    }

    /**
     * 解析日期
     *
     * @param text
     * @return
     */
    public static Date parseDate(String text, String pattern) {
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern).parse(text);
        } catch (ParseException e) {
            Log.e("parse date error, text:" + text + ", pattern:" + pattern);
        }
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getDateTime() {
        return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 格式化日期时间字符串
     *
     * @param date
     * @return 例如2011-11-30 16:06:54
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, DEFAULT_DATETIME_PATTERN);
    }

    public static String formatDateTime(long date) {
        return formatDate(new Date(date), DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 格式化时间,yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static String toYMD(long time) {
        return formatDate(new Date(time), DEFAULT_DATE_PATTERN);
    }

    /**
     * 格林威时间转换
     *
     * @param gmt
     * @return
     */
    public static String formatGMTDate(String gmt) {
        TimeZone timeZoneLondon = TimeZone.getTimeZone(gmt);
        return formatDate(Calendar.getInstance(timeZoneLondon).getTimeInMillis(), DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 将给定的时间格式化为 XSD/SOAP 的格式。
     *
     * @param date 要格式化的日期。
     * @return 该日期的 yyyy-MM-ddTHH:mm:ss+ZZ:ZZ 的格式。
     * @deprecated 请使用 {@link android.util.XmlUtil#dateToXsd(Date)} ，它提供了相同的功能方法。
     */
    @Deprecated
    public static final String dateToXsd(Date date) {
        final String dateTime = String.format("%1$tFT%1$tT", date);
        final String zone = String.format("%1$tz", date);
        return dateTime + zone.substring(0, 3) + ":" + zone.substring(3);
    }

}
