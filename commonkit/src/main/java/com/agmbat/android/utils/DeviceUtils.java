package com.agmbat.android.utils;

import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.agmbat.android.SystemManager;

/**
 * 提供当前设备的信息
 */
public class DeviceUtils {

    /**
     * 获取屏幕大小, 取到的高度是不包含下面虚拟导航键的高度
     *
     * @return
     */
    public static Point getScreenSize() {
        Point point = new Point();
        WindowManager wm = SystemManager.getWindowManager();
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            point.x = display.getWidth(); // 屏幕宽（像素，如：480px）
            point.y = display.getHeight(); // 屏幕高（像素，如：800p）
        } else {
            display.getSize(point);
        }
        return point;
    }

}
