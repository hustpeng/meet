package com.agmbat.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.agmbat.io.IoUtils;
import com.agmbat.io.OnProgressListener;
import com.agmbat.log.Log;
import com.agmbat.text.StringUtils;

/**
 * zip文件操作
 */
public class ZipUtils {

    /**
     * zip或unzip文件进度监听器
     */
    public interface OnZipProgressListener {

        /**
         * 压缩或解压过程中回调进度
         *
         * @param total        解压zip文件后的总大小
         * @param current      解压到当前的进度
         * @param entryName    当前解压的名称
         * @param entryTotal   当前解压的entry的大小
         * @param entryCurrent 当前解压的entry的进度
         */
        public void onZipProgress(long total, long current, String entryName, long entryTotal, long entryCurrent);
    }

    /**
     * 读取Zip文件中指定文件的内容
     *
     * @param zipFilePath
     * @param name
     * @return
     */
    public static String readZipFileText(String zipFilePath, String name) {
        String content = null;
        ZipFile zipfile = null;
        InputStream in = null;
        try {
            zipfile = new ZipFile(zipFilePath);
            in = openZipFile(zipfile, name);
            if (in != null) {
                content = IoUtils.loadContent(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(in);
            IoUtils.closeQuietly(zipfile);
        }
        return content;
    }

    /**
     * 打开zip文件中子文件流
     *
     * @param zipfile
     * @param name
     * @return
     */
    public static InputStream openZipFile(ZipFile zipfile, String name) {
        try {
            ZipEntry entry = zipfile.getEntry(name);
            if (entry != null) {
                long size = entry.getSize();
                if (size > 0) {
                    return zipfile.getInputStream(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭zip文件
     *
     * @param zipFile
     */
    public static void closeQuietly(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将给定的文件路径压缩到一个zip文件中
     *
     * @param paths
     * @return
     */
    public static boolean zipUp(ArrayList<String> paths, String zipFilePath) {
        ZipOutputStream zos = null;
        try {
            OutputStream os = new FileOutputStream(zipFilePath);
            zos = new ZipOutputStream(new BufferedOutputStream(os));
            for (String filename : paths) {
                putNextEntry(new File(filename), filename, zos);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(zos);
        }
        return false;
    }

    /**
     * 将文件或文件夹用zip压缩
     *
     * @param file
     * @param outFile
     * @return
     */
    public static boolean zipFile(File file, File outFile) {
        if (file == null || !file.exists() || outFile == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outFile));
            zip(file, zos, "");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.delete(outFile);
        } finally {
            IoUtils.closeQuietly(zos);
        }
        return false;
    }

    /**
     * 压缩
     *
     * @param srcFile  源路径
     * @param destFile 目标路径
     */
    public static void zipCrc32(File srcFile, File destFile) {
        // 对输出文件做CRC32校验
        ZipOutputStream zos = null;
        CheckedOutputStream cos = null;
        try {
            cos = new CheckedOutputStream(new FileOutputStream(destFile), new CRC32());
            zos = new ZipOutputStream(cos);
            zip(srcFile, zos, "");
            zos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(zos);
            IoUtils.closeQuietly(cos);
        }
    }

    /**
     * 压缩
     *
     * @param srcFile  源路径
     * @param zos      ZipOutputStream
     * @param basePath 压缩包内相对路径
     * @throws IOException
     */
    private static void zip(File srcFile, ZipOutputStream zos, String basePath) throws IOException {
        if (srcFile.isDirectory()) {
            zipDir(srcFile, zos, basePath);
        } else {
            String entryName = getEntryName(basePath, srcFile);
            putNextEntry(srcFile, entryName, zos);
        }
    }

    /**
     * 压缩目录及子文件
     *
     * @param dir
     * @param zos
     * @param basePath
     * @throws IOException
     */
    private static void zipDir(File dir, ZipOutputStream zos, String basePath) throws IOException {
        String entryName = getEntryName(basePath, dir);
        // 构建当前目录
        putDirEntry(zos, entryName);
        File[] files = dir.listFiles();
        // 压缩子文件或目录
        for (File file : files) {
            zip(file, zos, entryName);
        }
    }

    /**
     * 将目录添加到zip文件中
     *
     * @param zos
     * @param entryName
     * @throws IOException
     */
    private static void putDirEntry(ZipOutputStream zos, String entryName) throws IOException {
        ZipEntry entry = new ZipEntry(entryName + "/");
        zos.putNextEntry(entry);
        zos.closeEntry();
    }

    /**
     * 获取entry name
     *
     * @param basePath
     * @param file
     * @return
     */
    private static String getEntryName(String basePath, File file) {
        String entryName = file.getName();
        if (!StringUtils.isEmpty(basePath)) {
            entryName = basePath + "/" + entryName;
        }
        return entryName;
    }

    /**
     * 将文件添加到ZipOutputStream中
     *
     * @param file      被压缩的文件
     * @param entryName
     * @param zos
     * @throws IOException
     */
    private static void putNextEntry(File file, String entryName, ZipOutputStream zos) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            IoUtils.copyStream(is, zos);
            zos.closeEntry();
        } finally {
            IoUtils.closeQuietly(is);
        }
    }


    /**
     * 文件解压缩
     *
     * @param zis    ZipInputStream
     * @param outDir 目标文件夹
     * @throws IOException
     */
    public static void unzip(ZipInputStream zis, File outDir) throws IOException {
        unzip(zis, outDir, 0, null, null);
    }

    /**
     * 解决zip文件中指定目录下的所有文件到输出目录中
     *
     * @param zipFile
     * @param outDir
     * @param filter
     */
    public static void unzip(File zipFile, File outDir, FilenameFilter filter) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            unzip(zis, outDir, 0, filter, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(zis);
        }
    }


    /**
     * 解压zip文件到同级目录
     *
     * @param srcFile
     */
    public static boolean unzip(File srcFile) {
        File parent = srcFile.getParentFile();
        return unzip(srcFile, parent);
    }


    /**
     * 解压文件到指定目录
     *
     * @param srcFile
     * @param outDir
     * @return
     */
    public static boolean unzip(File srcFile, File outDir) {
        return unzip(srcFile, outDir, (OnZipProgressListener) null);
    }


    /**
     * 解压文件到指定目录(使用ZipFile, 解压速度快)
     *
     * @param srcFile 需要解压的zip文件
     * @param outDir  需要保存目录
     * @param l       进度回调
     * @return
     */
    public static boolean unzip(File srcFile, File outDir, final OnZipProgressListener l) {
        final long totalSize = (l == null) ? 0 : getSize(srcFile);
        // 已解压的大小
        long unzipSize = 0;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                final String name = entry.getName();
                File childFile = new File(outDir, name);
                FileUtils.ensureParent(childFile);
                if (entry.isDirectory()) {
                    childFile.mkdirs();
                } else {
                    InputStream in = zipFile.getInputStream(entry);
                    unzipChild(in, entry, totalSize, unzipSize, childFile, l);
                    IoUtils.closeQuietly(in);
                    unzipSize += entry.getSize();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(zipFile);
        }
        return false;
    }

    /**
     * 解压文件到指定目录 (使用ZipInputStream效率低) 建议使用unzip
     *
     * @param srcFile 需要解压的zip文件
     * @param outDir  需要保存目录
     * @param l       进度回调
     * @return
     */
    @Deprecated
    private static boolean unzipUseZis(File srcFile, File outDir, OnZipProgressListener l) {
        ZipInputStream zis = null;
        try {
            CheckedInputStream cis = new CheckedInputStream(new FileInputStream(srcFile), new CRC32());
            zis = new ZipInputStream(cis);
            long total = (l == null) ? 0 : getSize(srcFile);
            unzip(zis, outDir, total, null, l);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(zis);
        }
        return false;
    }

    /**
     * 文件解压缩
     *
     * @param zis    ZipInputStream
     * @param outDir 目标文件夹
     * @param filter 指定可以解压的规则
     * @throws IOException
     */
    private static void unzip(ZipInputStream zis, File outDir, final long totalSize, FilenameFilter filter,
                              final OnZipProgressListener l) throws IOException {
        ZipEntry entry = null;
        // 已解压的大小
        long unzipSize = 0;
        while ((entry = zis.getNextEntry()) != null) {
            // 文件名称
            final String name = entry.getName();
            boolean accept = accept(filter, name);
            if (accept) {
                File childFile = new File(outDir, name);
                FileUtils.ensureParent(childFile);
                if (entry.isDirectory()) {
                    childFile.mkdirs();
                } else {
                    // 解压单个元素文件
                    unzipChild(zis, entry, totalSize, unzipSize, childFile, l);
                    unzipSize += entry.getSize();
                }
            }
            zis.closeEntry();
        }
    }

    /**
     * 解压zip文件中指定目录下的所有文件到输出目录中
     *
     * @param zipFile
     * @param outDir
     * @param dirName 指定目录
     */
    public static void unzipChild(File zipFile, File outDir, final String dirName) {
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(dirName);
            }
        };
        unzip(zipFile, outDir, filter);
    }

    /**
     * 解析zipEntry中文件到指定目录
     *
     * @param zipFile
     * @param outDir
     * @param zipEntry
     */
    public static boolean unzipChild(ZipFile zipFile, String outDir, ZipEntry zipEntry) {
        InputStream is = null;
        try {
            is = zipFile.getInputStream(zipEntry);
            String name = new File(zipEntry.getName()).getName();
            IoUtils.copyStream(is, new File(outDir, name));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
        }
        return false;
    }

    /**
     * 解压zipEntry到指定文件
     *
     * @param zipFile
     * @param outFile
     * @param zipEntry
     * @return
     */
    public static boolean unzipChild(ZipFile zipFile, File outFile, ZipEntry zipEntry) {
        InputStream in = null;
        try {
            in = zipFile.getInputStream(zipEntry);
            IoUtils.copyStream(in, outFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(in);
        }
        return false;
    }

    /**
     * 解压单个元素文件
     *
     * @param in
     * @param zipEntry
     * @param totalSize
     * @param unzipSize
     * @param childFile
     * @param l
     * @throws IOException
     */
    private static void unzipChild(InputStream in, ZipEntry zipEntry, final long totalSize, final long unzipSize,
                                   File childFile, final OnZipProgressListener l) throws IOException {
        if (l != null) {
            final long entrySize = zipEntry.getSize();
            final String name = zipEntry.getName();
            OnProgressListener progressListener = new OnProgressListener() {
                @Override
                public void onProgress(long total, long current) {
                    l.onZipProgress(totalSize, unzipSize + current, name, entrySize, current);
                }
            };
            IoUtils.copyStream(in, childFile, entrySize, progressListener);
        } else {
            IoUtils.copyStream(in, childFile);
        }
    }


    /**
     * 删除zip文件中指定路径文件
     *
     * @param zipFile
     * @param path
     */
    public static void delete(File zipFile, String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        List<String> pathList = new ArrayList<String>();
        pathList.add(path);
        delete(zipFile, pathList);
    }

    /**
     * 删除文件
     *
     * @param zipFile
     * @param pathList
     */
    public static void delete(File zipFile, List<String> pathList) {
        List<String> deleteList = findDeleteEntry(zipFile, pathList);
        if (deleteList.size() == 0) {
            // 没有找到需要删除的文件
            return;
        }
        ZipInputStream zin = null;
        ZipOutputStream zout = null;
        File tempFile = null;
        try {
            // get a temp file
            tempFile = File.createTempFile(zipFile.getName(), null);
            // delete it, otherwise you cannot rename your existing zip to it.
            tempFile.delete();
            tempFile.deleteOnExit();

            boolean renameOk = FileUtils.move(zipFile, tempFile);
            if (!renameOk) {
                throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to "
                        + tempFile.getAbsolutePath());
            }
            zin = new ZipInputStream(new FileInputStream(tempFile));
            zout = new ZipOutputStream(new FileOutputStream(zipFile));

            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                boolean toBeDeleted = deleteList.contains(name);
                if (!toBeDeleted) {
                    // Add ZIP entry to output stream.
                    zout.putNextEntry(new ZipEntry(name));
                    // Transfer bytes from the ZIP file to the output file
                    IoUtils.copyStream(zin, zout);
                }
                entry = zin.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(zin);
            IoUtils.closeQuietly(zout);
            FileUtils.delete(tempFile);
        }
    }

    /**
     * List content of zip file
     *
     * @param file
     * @return
     */
    public static List<String> list(File file) {
        List<String> list = new ArrayList<String>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                list.add(entry.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(zipFile);
        }
        return list;
    }

    /**
     * 获取zip文件解压后的大小
     *
     * @param file zip文件
     * @return
     */
    public static long getSize(File file) {
        long size = 0;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                size += entry.getSize();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(zipFile);
        }
        return size;
    }

    /**
     * 是否接受指定的文件名
     *
     * @param filter
     * @param name
     * @return
     */
    private static boolean accept(FilenameFilter filter, String name) {
        if (filter == null) {
            return true;
        }
        return filter.accept(null, name);
    }

    /**
     * 查找需要删除的文件列表
     *
     * @param zipFile
     * @param pathList
     * @return
     */
    private static List<String> findDeleteEntry(File zipFile, List<String> pathList) {
        List<String> list = list(zipFile);
        List<String> deleteList = new ArrayList<String>();
        for (String path : list) {
            boolean toBeDeleted = toBeDeleted(pathList, path);
            if (toBeDeleted) {
                deleteList.add(path);
            }
        }
        return deleteList;
    }

    /**
     * 是否需要删除, 需要保证给定的目录全都被删除
     *
     * @param pathList
     * @param name
     * @return
     */
    private static boolean toBeDeleted(List<String> pathList, String name) {
        if (name.endsWith("/")) {
            // 如果是目录,需要删除'/'
            name = name.substring(0, name.length() - 1);
        }
        for (String path : pathList) {
            if (path.equals(name) || name.startsWith(path + "/")) {
                return true;
            }
        }
        return false;
    }

}
