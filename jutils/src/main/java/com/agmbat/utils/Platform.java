package com.agmbat.utils;

import com.agmbat.text.StringUtils;

/**
 * 用于获取当前运行平台信息
 */
public class Platform {

    /**
     * Return true if we're running on a Mac
     */
    public static boolean isMac() {
        return System.getProperty("os.name").startsWith("Mac OS");
    }

    /**
     * 是否运行在windows上
     *
     * @return
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    /**
     * 是否运行在linux上
     *
     * @return
     */
    public static boolean isLinux() {
        return System.getProperty("os.name").startsWith("Linux");
    }

    /**
     * 是否运行在Android平台上
     *
     * @return
     */
    public static boolean isAndroid() {
        if (isLinux()) {
            try {
                Class.forName("android.app.Application", false, ClassLoader.getSystemClassLoader());
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 判断当前是否运行在idea环境中
     *
     * @return
     */
    public static boolean runOnIdea() {
        String text = System.getenv("OLDPWD");
        return !StringUtils.isEmpty(text) && text.contains("IDEA");
    }
}
