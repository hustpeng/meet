package com.agmbat.zxing;

/**
 * 扫描回调
 */
public interface OnScanListener {

    /**
     * 当扫描到文本后回调
     *
     * @param text
     */
    public void onScan(String text);
}
