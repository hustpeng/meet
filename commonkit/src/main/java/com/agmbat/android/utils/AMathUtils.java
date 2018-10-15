package com.agmbat.android.utils;

import android.graphics.Point;

import com.agmbat.utils.MathUtils;

/**
 * Created by chenming03 on 16/10/8.
 */
public class AMathUtils {

    /**
     * 获取两个点之间与水平线之间的夹角，以水平向左方向为0度，逆时针方向为正方向
     *
     * @param pointO 圆心点 O
     * @param pointA
     * @return
     */
    public static double calcHorizontalDegrees(Point pointO, Point pointA) {
        return MathUtils.calcHorizontalDegrees(pointO.x, pointO.y, pointA.x, pointA.y);
    }

    /**
     * 获取两个点之间与垂直线之间的夹角，以垂直向上方向为0度，顺时针方向为正方向
     *
     * @param pointO 圆心点 O
     * @param pointB
     * @return
     */
    public static double calcVerticalDegrees(Point pointO, Point pointB) {
        return MathUtils.calcVerticalDegrees(pointO.x, pointO.y, pointB.x, pointB.y);
    }
}
