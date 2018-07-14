package com.agmbat.android.utils;

import java.io.File;
import java.io.IOException;

import com.agmbat.file.FileExtension;
import com.agmbat.log.Log;
import com.agmbat.utils.ReflectionUtils;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * Android文件管理工具, Java常用文件操作需使用FileUtils类
 */
public class AFileUtils {

    public static final int S_IRWXU = 00700; // rwx u
    public static final int S_IRUSR = 00400; // r-- u
    public static final int S_IWUSR = 00200; // -w- u
    public static final int S_IXUSR = 00100; // --x u

    public static final int S_IRWXG = 00070; // rwx g
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007; // rwx o
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    /**
     * Set owner and mode of of given path.
     *
     * @param path file path
     * @param mode to apply through {@code chmod}
     * @return 0 on success, otherwise errno.
     */
    public static int setPermissions(String path, int mode) {
        return setPermissions(path, mode, -1, -1);
    }

    /**
     * Set owner and mode of of given path.
     *
     * @param path file path
     * @param mode to apply through {@code chmod}
     * @param uid  to apply through {@code chown}, or -1 to leave unchanged
     * @param gid  to apply through {@code chown}, or -1 to leave unchanged
     * @return 0 on success, otherwise errno.
     */
    public static int setPermissions(String path, int mode, int uid, int gid) {
        Class<?>[] parameterTypes = new Class<?>[]{String.class, int.class, int.class, int.class};
        Object[] parameters = new Object[]{path, mode, uid, gid};
        return (Integer) ReflectionUtils.invokeStaticMethod("android.os.FileUtils", "setPermissions", parameterTypes,
                parameters);
    }

    /**
     * Deletes a file.
     * <p/>
     * <p>If Java impl fails, we will call linux command to do so.</p>
     *
     * @param file          the file to delete.
     * @param waitUntilDone whether wait until the linux command returns.
     */

    public static void deleteFile(File file, boolean waitUntilDone) {
        boolean success = file.delete();
        if (!success) {
            try {
                String[] args = new String[]{
                        "rm",
                        "-rf",
                        file.toString()
                };
                Process proc = Runtime.getRuntime().exec(args);
                if (waitUntilDone) {
                    int exitCode = proc.waitFor();
                    Log.d("TAG", "Linux rm -rf %s: %d.", file, exitCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMimeTypeFromExtension(String ext) {
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        ext = ext.toLowerCase();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if (mimeType == null) {
            if (ext.equals(FileExtension.MP3) || ext.equals(FileExtension.WAV) || ext.equals(FileExtension.WMA)) {
                mimeType = "audio/*";
            } else if (ext.equals(FileExtension.APK)) {
                mimeType = "application/vnd.android.package-archive";
            } else if (ext.equals(FileExtension.JS)) {
                mimeType = "application/javascript";
            } else if (ext.equals(FileExtension.LRC) || ext.equals(FileExtension.TXT)) {
                mimeType = "text/plain";
            } else if (ext.equals(FileExtension.GZ)) {
                mimeType = "application/x-gzip";
            }
        }
        return mimeType;
    }

    /**
     * 在指定目录中添加.nomedia文件, 保证此目录下的media文件不被扫描到系统数据库中
     *
     * @param dir
     */
    public static void ensureNoMediaFile(File dir) {
        File file = new File(dir, ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
