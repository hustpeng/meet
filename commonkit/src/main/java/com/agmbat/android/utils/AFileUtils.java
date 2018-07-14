package com.agmbat.android.utils;

import android.media.MediaMetadataRetriever;

/**
 * Android文件管理工具, Java常用文件操作需使用FileUtils类
 */
public class AFileUtils {

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param filePath
     * @return
     */
    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (Exception e) {
                return mime;
            }
        }
        return mime;
    }
}
