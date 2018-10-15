package com.agmbat.android.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.agmbat.io.IoUtils;
import com.agmbat.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * 存储设备工具类
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
public class StorageUtils {

    private static final String LOG_TAG = StorageUtils.class.getSimpleName();

    private static final long MIN_AVALIED_SIZE = 1024 * 1024;

    private static final String MOUNT_LINE_VALID_REG =
            "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*r(o|w).*";


    /**
     * 判断sd卡是否有效
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 使用读写的方式来判断sd卡是否有效
     *
     * @return
     */
    public static boolean isExternalMemoryAvailable() {
        boolean isExternalWritable = false;
        File externalRoot = Environment.getExternalStorageDirectory();
        if (null != externalRoot) {
            try {
                File testFile = new File(externalRoot, "test.txt");
                if (testFile.exists()) {
                    testFile.delete();
                }
                if (testFile.createNewFile()) {
                    isExternalWritable = true;
                    testFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isExternalWritable;
    }

    /**
     * Calculates the free memory of the device. This is based on an inspection of the filesystem, which in android
     * devices is stored in RAM.
     *
     * @return Number of bytes available.
     */
    public static long getAvailableInternalMemorySize() {
        final File dir = Environment.getDataDirectory();
        return getDiskFreeSize(dir.getPath());
    }

    /**
     * Calculates the total memory of the device. This is based on an inspection of the filesystem, which in android
     * devices is stored in RAM.
     *
     * @return Total number of bytes.
     */
    public static long getTotalInternalMemorySize() {
        final File dir = Environment.getDataDirectory();
        return getDiskTotalSize(dir.getPath());
    }

    public static long getAvailableExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File dir = Environment.getExternalStorageDirectory();
            return getDiskFreeSize(dir.getPath());
        } else {
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File dir = Environment.getExternalStorageDirectory();
            return getDiskTotalSize(dir.getPath());
        } else {
            return 0;
        }
    }

    public static boolean isExternalMemoryEmulated() {
        boolean isEmulated = false;
        Class<?> envClass = Environment.class;
        try {
            Method method = envClass.getDeclaredMethod("isExternalStorageEmulated");
            isEmulated = (Boolean) method.invoke(envClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isEmulated;
    }

    public static boolean isExternalStorageRemovable() {
        boolean isEmulated = false;
        Class<?> envClass = Environment.class;
        try {
            Method method = envClass.getDeclaredMethod("isExternalStorageRemovable");
            isEmulated = (Boolean) method.invoke(envClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isEmulated;
    }

    public static String validateFilePath(String filePath) {
        // 针对4.2上多用户的存储路径匹配
        if (Build.VERSION.SDK_INT >= 17) {
            if (!TextUtils.isEmpty(filePath)) {
                File externalStorage = Environment.getExternalStorageDirectory();
                filePath = filePath.replace(externalStorage.getAbsolutePath(), "/sdcard");
            }
        }
        return filePath;
    }

    public static File getCameraFolder() {
        if (Build.VERSION.SDK_INT >= 8) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        } else {
            return new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        }
    }

    public static File getValifyCameraFolder() {
        String cameraPath = validateFilePath(getCameraFolder().getAbsolutePath());
        return new File(cameraPath);
    }

    public static boolean isCameraPath(File photoFile) {
        boolean isCameraPath = false;
        if (photoFile.getParentFile() != null) {
            File cameraFolder = getCameraFolder();
            isCameraPath = photoFile.getParentFile().getAbsolutePath().startsWith(cameraFolder.getAbsolutePath());
        }
        return isCameraPath;
    }

    public static File getThumbnailFolder() {
        File thumbnailPath = new File(getCameraFolder() + File.separator + ".thumbnails");
        return thumbnailPath;
    }

    public static File getValifyThumbnailFolder() {
        return new File(getValifyCameraFolder() + File.separator + ".thumbnails");
    }

    /**
     * Retrieve the total size of a disk.
     *
     * @param diskPath the path of the disk.
     * @return the size of the disk in bytes.
     */
    public static long getDiskTotalSize(final String diskPath) {
        try {
            final StatFs stat = new StatFs(diskPath);
            final long blockSize = stat.getBlockSize();
            final long totalBlocks = stat.getBlockCount();
            final long totalSize = blockSize * totalBlocks;
            return totalSize;
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * Retrieve the free size of a disk.
     *
     * @param diskPath the path of the disk.
     * @return the size of the disk in bytes.
     */
    public static long getDiskFreeSize(final String diskPath) {
        try {
            final StatFs stat = new StatFs(diskPath);
            final long blockSize = stat.getBlockSize();
            final long freeBlocks = stat.getAvailableBlocks();
            final long totalSize = blockSize * freeBlocks;
            return totalSize;
        } catch (final Throwable e) {
            return 0;
        }
    }

    public static boolean isDirSizeLargerThan(File f, long size) {
        long total = 0;
        File list[] = f.listFiles();
        if (null != list) {
            for (int i = 0; i < list.length; i++) {
                File child = list[i];
                total += child.length();
                if (total > size) {
                    break;
                }
            }
        }
        return total > size;
    }


    private static String getStorageState() {
        String status = Environment.getExternalStorageState();
        if (isSDCardAvailable()) {
            return status;
        }
        File[] files = getAllStoreRootPathFiles();
        if (files != null && files.length > 0) {
            return Environment.MEDIA_MOUNTED;
        }
        return status;
    }

    private static File getAvaliedStorageDirectory() {
        File defaultDir = Environment.getExternalStorageDirectory();
        if (isSDCardAvailable()) {
            return defaultDir;
        }

        File[] files = getAllStoreRootPathFiles();
        if (files != null) {
            for (File file : files) {
                long size = getDiskFreeSize(file.getPath());
                if (size > MIN_AVALIED_SIZE) {
                    return file;
                }
            }
        }
        if (files != null && files.length > 0) {
            return files[0];
        }

        return defaultDir;
    }

    public static File[] getAllStoreRootPathFiles() {
        return getAllStoreRootPathFiles(MountPointCheckLevel.Normal);
    }

    public static File[] getAllStoreRootPathFiles(MountPointCheckLevel checkLevel) {
        String[] fileStrings = getStorageDirectories(checkLevel);
        File[] files = null;
        if (fileStrings != null) {
            int length = fileStrings.length;
            files = new File[length];
            for (int i = 0; i < length; i++) {
                files[i] = new File(fileStrings[i]);
            }
        }
        return files;
    }

    /**
     * Similar to android.os.Environment.getExternalStorageDirectory(), except that
     * here, we return all possible storage directories. The Environment class only
     * returns one storage directory. If you have an extended SD card, it does not
     * return the directory path. Here we are trying to return all of them.
     *
     * @return
     */
    private static String[] getStorageDirectories(MountPointCheckLevel checkLevel) {
        String[] dirs = null;
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(IoUtils.newUtf8OrDefaultInputStreamReader(
                    new FileInputStream("/proc/mounts")));
            HashSet<String> list = new HashSet<String>();
            String line;
            while ((line = bufReader.readLine()) != null) {
                Log.d(LOG_TAG, line);
                String mountPoint = getValidMountPoint(line, checkLevel);
                if (!TextUtils.isEmpty(mountPoint)) {
                    list.add(mountPoint);
                }
            }

            dirs = new String[list.size()];
            final Iterator<String> iterator = list.iterator();
            if (iterator != null) {
                int i = 0;
                while (iterator.hasNext()) {
                    dirs[i] = iterator.next();
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e);
        } finally {
            IoUtils.closeQuietly(bufReader);
        }

        return dirs;
    }

    private static String getValidMountPoint(String mountLine, MountPointCheckLevel checkLevel) {

        String defaultSdcard = Environment.getExternalStorageDirectory().getPath();
        if (mountLine.contains(defaultSdcard)) {
            String mountPoint = getMountPointFrom(mountLine);
            if (TextUtils.equals(mountPoint, defaultSdcard)) {
                return mountPoint;
            }
        }
        if (checkLevel == MountPointCheckLevel.Low) {
            if (!mountLine.toLowerCase(Locale.US).contains("asec")
                    && mountLine.matches(MOUNT_LINE_VALID_REG)) {
                String mountPoint = getMountPointFrom(mountLine);
                if (mountPoint != null && mountPoint.startsWith("/")
                        && !mountPoint.toLowerCase(Locale.US).contains("vold")) {
                    return mountPoint;
                }
            }
        } else if (checkLevel == MountPointCheckLevel.Normal) {
            if (mountLine.contains("vfat") && mountLine.contains("/dev/block/vold")
                    && !mountLine.contains("/mnt/secure") && !mountLine.contains("/mnt/asec")
                    && !mountLine.contains("/mnt/obb") && !mountLine.contains("/dev/mapper")
                    && !mountLine.contains("tmpfs")) {
                return getMountPointFrom(mountLine);
            }
        }
        return null;
    }

    private static String getMountPointFrom(String mountLine) {
        String result = null;
        try {
            StringTokenizer tokens = new StringTokenizer(mountLine, " ");
            tokens.nextToken();
            result = tokens.nextToken();
        } catch (NoSuchElementException e) {
            Log.e(LOG_TAG, e);
        }
        return result;
    }

    /**
     * 判断外置存储是否可用
     *
     * @return the boolean
     */
    public static boolean externalMounted() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 返回以“/”结尾的内部存储根目录
     */
    public static String getInternalRootPath(Context context, String type) {
        File file;
        if (TextUtils.isEmpty(type)) {
            file = context.getFilesDir();
        } else {
            file = new File(context.getFilesDir().getAbsolutePath(), type);
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        String path = "";
        if (file != null) {
            path = file.getAbsolutePath();
        }
        return path;
    }

    public static String getInternalRootPath(Context context) {
        return getInternalRootPath(context, null);
    }

    /**
     * 返回以“/”结尾的外部存储根目录，外置卡不可用则返回空字符串
     */
    public static String getExternalRootPath(String type) {
        File file = null;
        if (externalMounted()) {
            file = Environment.getExternalStorageDirectory();
        }
        if (file != null && !TextUtils.isEmpty(type)) {
            file = new File(file, type);
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        String path = "";
        if (file != null) {
            path = file.getAbsolutePath();
        }
        return path;
    }

    public static String getExternalRootPath() {
        return getExternalRootPath(null);
    }

    /**
     * 各种类型的文件的专用的保存路径，以“/”结尾
     *
     * @return 诸如：/mnt/sdcard/Android/data/[package]/files/[type]/
     */
    public static String getExternalPrivatePath(Context context, String type) {
        File file = null;
        if (externalMounted()) {
            file = context.getExternalFilesDir(type);
        }
        //高频触发java.lang.NullPointerException，是SD卡不可用或暂时繁忙么？
        String path = "";
        if (file != null) {
            path = file.getAbsolutePath();
        }
        return path;
    }

    public static String getExternalPrivatePath(Context context) {
        return getExternalPrivatePath(context, null);
    }

    /**
     * 下载的文件的保存路径，必须为外部存储，以“/”结尾
     *
     * @return 诸如 ：/mnt/sdcard/Download/
     */
    public static String getDownloadPath() throws RuntimeException {
        File file;
        if (externalMounted()) {
            file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        } else {
            throw new RuntimeException("外置存储不可用！");
        }
        return file.getAbsolutePath();
    }

    /**
     * 各种类型的文件的专用的缓存存储保存路径，优先使用外置存储，以“/”结尾
     */
    public static String getCachePath(Context context, String type) {
        File file;
        if (externalMounted()) {
            file = context.getExternalCacheDir();
        } else {
            file = context.getCacheDir();
        }
        if (!TextUtils.isEmpty(type)) {
            file = new File(file, type);
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        String path = "";
        if (file != null) {
            path = file.getAbsolutePath();
        }
        return path;
    }

    public static String getCachePath(Context context) {
        return getCachePath(context, null);
    }

    /**
     * 返回以“/”结尾的临时存储目录
     */
    public static String getTempDirPath(Context context) {
        return getExternalPrivatePath(context, "temporary");
    }

    /**
     * 返回临时存储文件路径
     */
    public static String getTempFilePath(Context context) {
        try {
            return File.createTempFile("temp_", ".tmp", context.getCacheDir()).getAbsolutePath();
        } catch (IOException e) {
            return getTempDirPath(context) + "temp_.tmp";
        }
    }

    public enum MountPointCheckLevel {
        /***
         * find out all mount points which can be read & write,
         * used by dolphin int.
         */
        Low,
        /***
         * find out all mount points which is vfat format or default sdcard,
         * used by dolphin cn.
         */
        Normal,
    }

}
