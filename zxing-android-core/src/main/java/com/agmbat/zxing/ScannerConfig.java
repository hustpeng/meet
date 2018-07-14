package com.agmbat.zxing;

/**
 * 二纬码扫描配置
 */
public class ScannerConfig {

    public static boolean sIsPortrait = false;

    public static void setIsPortrait(boolean isPortrait) {
        sIsPortrait = isPortrait;
    }

    public static boolean isPortrait() {
        return sIsPortrait;
    }
}
