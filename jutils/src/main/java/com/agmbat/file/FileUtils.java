/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.file;

import com.agmbat.io.IoUtils;
import com.agmbat.log.Log;
import com.agmbat.text.StringUtils;
import com.agmbat.time.TimeUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tools for managing files.
 */
public final class FileUtils {

    /**
     * Illegal file name chars.
     */
    public static final Pattern ILLEGAL_FILE_NAME_CHARS = Pattern.compile("[\\\\/:*?<>|]+");
    /**
     * The max length for file name.
     */
    public static final int MAX_PATH = 256;
    /**
     * Regular expression for safe filenames: no spaces or metacharacters
     */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
    /**
     * 用于生成文件
     */
    private static final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
    private static final String TAG = "FileUtils";


    private FileUtils() {
    }

    /**
     * 获取文件创建时间
     *
     * @param f
     * @return
     * @throws IOException
     */
    public static long getCreateTime(File f) {
        if (f == null) {
            return -1;
        }
        try {
            Path path = FileSystems.getDefault().getPath(f.getParent(), f.getName());
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return attrs.creationTime().toMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 判断是否为空目录
     *
     * @param file
     * @return
     */
    public static boolean isEmptyDir(File file) {
        if (file == null || file.isFile()) {
            return false;
        }
        File[] list = file.listFiles();
        return list != null && list.length == 0;
    }

    /**
     * Ensure parent
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
     * 将文件或文件夹复制到指定目录
     *
     * @param source
     * @param destDir
     */
    public static boolean copyToDir(File source, File destDir) {
        if (!source.exists()) {
            Log.e(TAG, "[copyFileToDir] source not exist:" + source);
            return false;
        }
        final String name = source.getName();
        final File destFile = new File(destDir, name);
        if (destFile.exists()) {
            Log.e(TAG, "[copyFileToDir] dest file exist:" + destFile);
            return false;
        }
        ensureParent(destFile);
        if (source.isFile()) {
            copyFileNio(source, destFile);
        } else {
            // source is dir
            final boolean isCreated = destFile.mkdir();
            if (!isCreated) {
                Log.e(TAG, "destFileDir cant create");
                return false;
            }
            final File[] fileList = source.listFiles();
            if (fileList == null || fileList.length == 0) {
                return true;
            }

            boolean result = true;
            for (File f : fileList) {
                File df = new File(destFile, f.getName());
                if (f.isFile()) {
                    copyFileNio(f, df);
                } else {
                    df.mkdir();
                    result = copyToDir(f, df.getParentFile());
                    if (!result) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dest
     */
    public static boolean copyFile(String src, String dest) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            IoUtils.copyStream(fis, new File(dest));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }
        return false;
    }

    /**
     * 复制文件,使用nio以提高性能
     *
     * @param src  源文件
     * @param dest 目标文件
     */
    public static boolean copyFileNio(File src, File dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);
            in = fis.getChannel(); // 得到对应的文件通道
            out = fos.getChannel(); // 得到对应的文件通道
            in.transferTo(0, in.size(), out); // 连接两个通道，并且从in通道读取，然后写入out通道
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
            IoUtils.closeQuietly(in);
            IoUtils.closeQuietly(fos);
            IoUtils.closeQuietly(out);
        }
        return false;
    }

    /**
     * 文件移动操作, 支持文件或目录, 如果目标文件不存在则创建文件
     *
     * @param source
     * @param dest
     */
    public static boolean move(File source, File dest) {
        if (source == null || !source.exists()) {
            return false;
        }
        if (dest == null) {
            return false;
        }
        if (source.isFile()) {
            // 如果是单个文件
            if (dest.exists()) {
                // 如果目标文件存在且是文件夹, 则移到文件中
                if (dest.isDirectory()) {
                    Log.d("move file to dir:" + dest);
                    File destFile = new File(dest, source.getName());
                    return moveFile(source, destFile);
                } else {
                    Log.w("dest file exist:" + dest);
                }
            } else {
                // 如果目标文件不存在, 则将文件到该位置
                return moveFile(source, dest);
            }
        } else {
            // 移动目录
            if (dest.exists()) {
                if (dest.isDirectory()) {
                    moveToDir(source, dest);
                } else {
                    Log.e("exist file:" + dest);
                }
            } else {
                moveDir(source, dest);
            }
        }
        return true;
    }


    /**
     * 移动文件
     *
     * @param source
     * @param destFile
     */
    private static boolean moveFile(File source, File destFile) {
        if (destFile.exists()) {
            Log.w("dest file exist:" + destFile);
            return false;
        }
        boolean renameSuccess = source.renameTo(destFile);
        if (!renameSuccess) {
            boolean copySuccess = copyFileNio(source, destFile);
            if (copySuccess) {
                delete(source);
            } else {
                Log.e("copy file failed:" + source + "->" + destFile);
            }
        }
        return true;
    }

    /**
     * 移动文件
     *
     * @param source
     * @param destFile
     */
    private static void moveDir(File source, File destFile) {
        if (destFile.exists()) {
            Log.w("dest file exist:" + destFile);
            return;
        }
        boolean renameSuccess = source.renameTo(destFile);
        if (!renameSuccess) {
            boolean copySuccess = copyToDir(source, destFile.getParentFile());
            if (copySuccess) {
                renameSuccess = new File(destFile.getParentFile(), source.getName()).renameTo(destFile);
                if (renameSuccess) {
                    delete(source);
                }
            } else {
                Log.e("copy file failed:" + source + "->" + destFile);
            }
        }
    }

    /**
     * 将文件或文件夹移动到指定目录
     *
     * @param source
     * @param destDir
     */
    @Deprecated
    private static void moveToDir(File source, File destDir) {
        copyToDir(source, destDir);
        deleteNotCheck(source);
    }

    /**
     * 删除文件
     *
     * @param path
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
     * @param file
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
     * 根据时间生成一个文件名，不含后缀
     */
    public static String genFileNameByDate() {
        return TimeUtils.formatDate(System.currentTimeMillis(), DEFAULT_FILE_PATTERN);
    }

    /**
     * 判断文件名是否有效，检测是否包含非法字符,文件名不能包含 \/:*?"<>|
     *
     * @param name
     * @return
     */
    public static boolean isFilenameValid(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return !ILLEGAL_FILE_NAME_CHARS.matcher(name).find();
    }

    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     *
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe. Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    /**
     * Tiny a given string, return a valid file name.
     *
     * @param fileName the file name to clean.
     * @return the valid file name.
     */
    public static String safeFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        if (fileName.length() > MAX_PATH) {
            fileName = fileName.substring(0, MAX_PATH);
        }
        final Matcher matcher = ILLEGAL_FILE_NAME_CHARS.matcher(fileName);
        fileName = matcher.replaceAll("_");
        return fileName;
    }


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
     * Ensure make directory, 如果存在同名文件夹，则添加上数字后缀
     *
     * @param dir
     */
    public static boolean ensureMkdir(File dir) {
        if (dir == null) {
            return false;
        }
        File tempDir = dir;
        int i = 1;
        while (tempDir.exists()) {
            tempDir = new File(dir.getParent(), dir.getName() + "(" + i + ")");
            i++;
        }
        return tempDir.mkdir();
    }


    /**
     * 删除单个文件, 建议使用delete
     *
     * @param file
     * @return
     */
    @Deprecated
    public static boolean deleteFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Delete file if exist path, 建议使用delete
     *
     * @param path the path
     * @return true if this file was deleted, false otherwise.
     */
    @Deprecated
    public static boolean deleteFileIfExist(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return deleteFileIfExist(new File(path));
    }

    /**
     * Delete file if exist file, 建议使用delete
     *
     * @param file the file
     * @return true if this file was deleted, false otherwise.
     */
    @Deprecated
    public static boolean deleteFileIfExist(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Delete a specified directory. 建议使用delete
     *
     * @param dir the path of directory to clean
     */
    @Deprecated
    public static void deleteDir(final String dir) {
        deleteDir(new File(dir));
    }

    /**
     * Delete a specified directory. 建议使用delete
     *
     * @param dir the directory to clean.
     */
    @Deprecated
    public static void deleteDir(final File dir) {
        deleteDir(dir, true);
    }

    /**
     * Delete a specified directory.
     *
     * @param dir       the directory to clean.
     * @param removeDir true to remove the directory.
     */
    public static void deleteDir(final File dir, final boolean removeDir) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Delete a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final FileFilter filter) {
        deleteDir(dir, true, filter);
    }

    /**
     * Delete a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final FilenameFilter filter) {
        deleteDir(dir, true, filter);
    }

    /**
     * Delete a specified directory.
     *
     * @param dir       the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     * @param filter    the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final boolean removeDir, final FileFilter filter) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(filter);
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Delete a specified directory.
     *
     * @param dir       the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     * @param filter    the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final boolean removeDir, final FilenameFilter filter) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(filter);
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Clear content of a file. If the file doesn't exist, it will return false.
     *
     * @param file the file to clear.
     * @return true if file becomes empty, false otherwise.
     */
    public static boolean clearFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.length() == 0) {
            return true;
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write("".getBytes(IoUtils.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(out);
        }
        return false;
    }

    /**
     * Clean a specified directory.
     *
     * @param dir the directory to clean.
     */
    public static void cleanDir(final File dir) {
        deleteDir(dir, false);
    }

    /**
     * Clean a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void cleanDir(final File dir, final FilenameFilter filter) {
        deleteDir(dir, false, filter);
    }

    /**
     * Clean a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void cleanDir(final File dir, final FileFilter filter) {
        deleteDir(dir, false, filter);
    }

    /**
     * compute the size of one folder
     *
     * @param dir
     * @return the byte length for the folder
     */
    public static long computeFolderSize(final File dir) {
        if (dir == null) {
            return 0;
        }
        long dirSize = 0;
        final File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += computeFolderSize(file);
                }
            }
        }
        return dirSize;
    }

    /**
     * Retrieve the main file name.
     *
     * @param path the file name.
     * @return the main file name without the extension.
     */
    public static String getFileNameWithoutExtensionByPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return getFileNameWithoutExtension(new File(path));
    }

    /**
     * Helper method to get a filename without its extension
     *
     * @param fileName String
     * @return String
     */
    public static String getFileNameWithoutExtension(String fileName) {
        final int index = fileName.lastIndexOf('.');
        if (index >= 0) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     * Retrieve the main file name.
     *
     * @param file the file.
     * @return the main file name without the extension.
     */
    public static String getFileNameWithoutExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileNameWithoutExtension(file.getName());
    }

    /**
     * Retrieve the main file name.
     *
     * @param path the file name.
     * @return the extension of the file.
     */
    public static String getExtension(final String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return getExtension(new File(path));
    }

    /**
     * Retrieve the extension of the file.
     *
     * @param file the file.
     * @return the extension of the file.
     */
    public static String getExtension(final File file) {
        if (file == null) {
            return null;
        }
        final String fileName = file.getName();
        final int index = fileName.lastIndexOf('.');
        if (index >= 0) {
            return fileName.substring(index + 1);
        }
        return "";
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
     * 重命名文件
     */
    public static boolean rename(File file, String name) {
        File destFile = new File(file.getParent(), name);
        return file.renameTo(destFile);
    }

    /**
     * 重命名文件
     *
     * @param oldPath
     * @param newPath
     */
    public static boolean renameFile(String oldPath, String newPath) {
        if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath)) {
            return false;
        }
        File oldFile = new File(oldPath);
        boolean existOldFile = existsFile(oldFile);
        if (!existOldFile) {
            return false;
        }
        File newFile = new File(newPath);
        FileUtils.delete(newFile);
        try {
            return new File(oldPath).renameTo(new File(newPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     */
    public static void writeToFile(File file, String content) {
        writeToFile(file, content, false);
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     * @param append
     */
    public static void writeToFile(File file, String content, boolean append) {
        writeToFile(file, content, append, IoUtils.UTF_8);
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     * @param encoding
     */
    public static void writeToFile(File file, String content, String encoding) {
        writeToFile(file, content, false, encoding);
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     * @param append
     * @param encoding
     */
    public static void writeToFile(File file, String content, boolean append, String encoding) {
        if (file == null || StringUtils.isEmpty(content)) {
            return;
        }
        FileUtils.ensureParent(file);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file, append), encoding);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(writer);
        }
    }

    /**
     * Write the specified data to an specified file.
     *
     * @param file The file to write into.
     * @param data The data to write. May be null.
     */
    public static final void writeToFile(File file, byte[] data) {
        if (file == null || data == null) {
            return;
        }
        FileUtils.ensureParent(file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fos);
        }
    }

    /**
     * Write the specified input stream to an specified file. Use NIO
     *
     * @param is
     * @param target
     */
    public static void writeToFileNio(InputStream is, File target) {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;
        try {
            int len = is.available();
            src = Channels.newChannel(is);
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fo);
            IoUtils.closeQuietly(src);
            IoUtils.closeQuietly(out);
        }
    }

    /**
     * Write the specified data to an specified file.
     *
     * @param target
     * @param data
     */
    public static void writeToFileNio(File target, byte[] data) {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;
        try {
            src = Channels.newChannel(new ByteArrayInputStream(data));
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fo);
            IoUtils.closeQuietly(src);
            IoUtils.closeQuietly(out);
        }
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

    /**
     * Read config file
     *
     * @param file
     * @return
     */
    public static Map<String, String> readConfig(File file) {
        Map<String, String> map = new HashMap<String, String>();
        String text = readFileText(file);
        if (StringUtils.isEmpty(text)) {
            return map;
        }
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (StringUtils.isEmpty(line)) {
                continue;
            } else if (line.startsWith("#")) {
                continue;
            }
            String[] array = line.split("=", 2);
            map.put(array[0].trim(), array[1].trim());
        }
        return map;
    }


    /**
     * Get user directory
     *
     * @return
     */
    public static File getUserDir() {
        String path = System.getProperty("user.dir");
        return new File(path);
    }

    /**
     * Get user home directory
     *
     * @return
     */
    public static File getUserHome() {
        String path = System.getProperty("user.home");
        return new File(path);
    }

}
