/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.utils;

import java.text.DecimalFormat;

/**
 * 数学工具类
 */
public class MathUtils {

    public static int compare(long a, long b) {
        return a < b ? -1 : a == b ? 0 : 1;
    }

    public static int ceilLog2(float value) {
        int i;
        for (i = 0; i < 31; i++) {
            if ((1 << i) >= value) {
                break;
            }
        }
        return i;
    }

    public static int floorLog2(float value) {
        int i;
        for (i = 0; i < 31; i++) {
            if ((1 << i) > value) {
                break;
            }
        }
        return i - 1;
    }

    public static float interpolateAngle(float source, float target, float progress) {
        // interpolate the angle from source to target
        // We make the difference in the range of [-179, 180], this is the
        // shortest path to change source to target.
        float diff = target - source;
        if (diff < 0) {
            diff += 360f;
        }
        if (diff > 180) {
            diff -= 360f;
        }
        float result = source + diff * progress;
        return result < 0 ? result + 360f : result;
    }

    public static float interpolateScale(float source, float target, float progress) {
        return source + progress * (target - source);
    }

    /**
     * Returns the input value x clamped to the range [min, max].
     *
     * @param x
     * @param min
     * @param max
     * @return
     */
    public static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     * Returns the input value x clamped to the range [min, max].
     *
     * @param x
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float x, float min, float max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     * Returns the input value x clamped to the range [min, max].
     *
     * @param x
     * @param min
     * @param max
     * @return
     */
    public static long clamp(long x, long min, long max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     * Returns the next power of two. Returns the input if it is already power of 2. Throws IllegalArgumentException if
     * the input is <= 0 or the answer overflows.
     *
     * @param n
     * @return
     */
    public static int nextPowerOf2(int n) {
        if (n <= 0 || n > (1 << 30)) {
            throw new IllegalArgumentException("n is invalid: " + n);
        }
        n -= 1;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        return n + 1;
    }

    /**
     * Returns the previous power of two. Returns the input if it is already power of 2. Throws IllegalArgumentException
     * if the input is <= 0
     *
     * @param n
     * @return
     */
    public static int prevPowerOf2(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        return Integer.highestOneBit(n);
    }

    /**
     * Return an integer that ranges from min inclusive to max inclusive.
     *
     * @param min the min
     * @param max the max
     * @return the int
     */
    public static int randomInt(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }

    /**
     * Return an double that ranges from min inclusive to max inclusive.
     *
     * @param min the min
     * @param max the max
     * @return the double
     */
    public static double randomDouble(double min, double max) {
        return min + (max - min) * Math.random();
    }

    /**
     * 计算数组的平均值
     *
     * @param pixels 数组
     * @return int 平均值
     */
    public static int average(int[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int) m;
    }

    /**
     * 获取两个点之间与水平线之间的夹角，以水平向左方向为0度，逆时针方向为正方向
     *
     * @param pox pointO 圆心点 O
     * @param poy pointO 圆心点 O
     * @param pax pointA
     * @param pay pointA
     * @return
     */
    public static double calcHorizontalDegrees(int pox, int poy, int pax, int pay) {
        if (pax == pox) {
            // 如果两点相同,角度为0
            if (pay == poy) {
                return 0;
            }
            return (pay < poy) ? 90 : 270;
        }
        double tanAob = Math.abs((pay - poy) / (double) (pax - pox));
        double aob = Math.atan(tanAob);
        double degrees = Math.toDegrees(aob);
        // 手机屏幕坐标系是以左上角为原点,Y轴正方向是竖直向下
        if (pay <= poy && pax > pox) {
            // 第一象限
            degrees = 0 + degrees;
        } else if (pay <= poy && pax < pox) {
            // 第二象限
            degrees = 180 - degrees;
        } else if (pay > poy && pax < pox) {
            // 第三象限
            degrees = 180 + degrees;
        } else {
            // 第四象限
            // by > oy && bx < ox
            degrees = 360 - degrees;
        }
        return degrees;
    }

    /**
     * 获取两个点之间与垂直线之间的夹角，以垂直向上方向为0度，顺时针方向为正方向
     *
     * @param pox 圆心点 O
     * @param poy 圆心点 O
     * @param pbx pointB
     * @param pby pointB
     * @return
     */
    public static double calcVerticalDegrees(int pox, int poy, int pbx, int pby) {
        if (pbx == pox) {
            return pby <= poy ? 0 : 180;
        }
        double tanAob = Math.abs((pby - poy) / (double) (pbx - pox));
        double aob = Math.atan(tanAob);
        double degrees = Math.toDegrees(aob);
        // 屏幕坐标系是以左上角为原点
        if (pby <= poy && pbx > pox) {
            degrees = 90 - degrees;
        } else if (pby >= poy && pbx > pox) {
            degrees = 90 + degrees;
        } else if (pby <= poy && pbx < pox) {
            degrees = 270 + degrees;
        } else {
            degrees = 270 - degrees;
        }
        return degrees;
    }


    /**
     * format a number properly with the given number of digits
     *
     * @param number the number to format
     * @param digits the number of digits
     * @return
     */
    public static String formatDecimal(double number, int digits) {
        StringBuffer a = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0) {
                a.append(".");
            }
            a.append("0");
        }
        DecimalFormat nf = new DecimalFormat("###,###,###,##0" + a.toString());
        String formatted = nf.format(number);
        return formatted;
    }

    /**
     * Returns the appropriate number of decimals to be used for the provided number.
     *
     * @param number
     * @return
     */
    public static int getDecimals(float number) {
        float i = roundToNextSignificant(number);
        return (int) Math.ceil(-Math.log10(i)) + 2;
    }

    /**
     * rounds the given number to the next significant number
     *
     * @param number
     * @return
     */
    public static float roundToNextSignificant(double number) {
        final float d = (float) Math.ceil((float) Math.log10(number < 0 ? -number : number));
        final int pw = 1 - (int) d;
        final float magnitude = (float) Math.pow(10, pw);
        final long shifted = Math.round(number * magnitude);
        return shifted / magnitude;
    }
}