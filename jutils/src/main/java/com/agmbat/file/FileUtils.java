package com.agmbat.file;

import com.agmbat.io.IoUtils;
import com.agmbat.text.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Tools for managing files.
 */
public final class FileUtils {

    /**
     * Ensure directory exists
     *
     * @param file
     */
    public static void ensureDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
    }

    /**
     * 如果父目录不存在,则创建
     *
     * @param file
     */
    public static void ensureParent(File file) {
        if (file != null) {
            final File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param path 指定文件路径
     * @return
     */
    public static boolean delete(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return delete(new File(path));
    }

    /**
     * 删除文件
     *
     * @param file 指定文件
     * @return true 表示删除成功
     */
    public static boolean delete(File file) {
        if (file == null) {
            return false;
        }
        deleteNotCheck(file);
        return !file.exists();
    }

    /**
     * Open new file out put
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static FileOutputStream openFileOutputStream(File file) throws IOException {
        delete(file);
        ensureParent(file);
        file.createNewFile();
        return new FileOutputStream(file);
    }

    /**
     * 删除文件夹和里面的内容,不关心删除后的结果
     *
     * @param file
     */
    private static void deleteNotCheck(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        // else file is directory
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            file.delete();
            return;
        }
        for (File f : files) {
            deleteNotCheck(f);
            file.delete();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean existsFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return existsFile(new File(path));
    }

    /**
     * 判断文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean existsFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * Read text file
     *
     * @param path
     * @return
     */
    public static String readFileText(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return readFileText(new File(path));
    }

    /**
     * Read text file
     *
     * @param file
     * @return
     */
    public static String readFileText(File file) {
        if (existsFile(file)) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                return IoUtils.loadContent(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(fis);
            }
        }
        return null;
    }

    /**
     * Read text file
     *
     * @param path
     * @param charsetName
     * @return
     */
    public static String readFileText(String path, String charsetName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            return IoUtils.loadContent(fis, charsetName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * 从文件读取byte数组
     *
     * @param file
     * @return
     */
    public static byte[] readFileBytes(File file) {
        if (existsFile(file)) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                return IoUtils.loadBytes(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(fis);
            }
        }
        return null;
    }
}
