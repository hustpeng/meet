package com.agmbat.io;

/**
 * 进度监听器
 */
public interface OnProgressListener {

    /**
     * 进度通知
     *
     * @param total   总长度
     * @param current 已处理的长度
     */
    public void onProgress(long total, long current);
}
