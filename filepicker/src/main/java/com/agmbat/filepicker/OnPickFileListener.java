package com.agmbat.filepicker;

import java.io.File;

/**
 * 文件回调
 */
public interface OnPickFileListener {

    /**
     * 返回位置信息
     *
     * @param file
     */
    public void onPick(File file);
}
