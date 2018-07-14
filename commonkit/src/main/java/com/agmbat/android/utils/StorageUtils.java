package com.agmbat.android.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 存储设备工具类
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
public class StorageUtils {

    /**
     * 判断sd卡是否有效
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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

}
