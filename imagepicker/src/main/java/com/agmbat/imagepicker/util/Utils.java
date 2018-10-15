package com.agmbat.imagepicker.util;

import android.content.Context;

/**
 */
public class Utils {

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列，并获取Item宽度
     */
    public static int getImageItemWidth(Context activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
        int cols = screenWidth / densityDpi;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2 * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }


}
