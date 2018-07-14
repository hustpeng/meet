/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.file;

import com.agmbat.text.StringUtils;
import com.agmbat.io.IoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FileScanner {

    public interface OnScanFileListener extends FileFilter, Comparator<File> {

        /**
         * 扫描文件
         * 
         * @param file
         * @param isDirectory
         */
        public void onScanFile(File file, boolean isDirectory);

        // 判断是否中断，user cancel
        public boolean interrupted();

    }

    public interface OnScanLineListener {
        public void onScanLine(String text);
    }

    public static void scanFolder(String folder, OnScanFileListener l) {
        scanFolder(folder, true, l);
    }

    public static void scanFolder(String path, boolean isScanChildFolder, OnScanFileListener l) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        List<File> list = new ArrayList<File>();
        list.add(file);
        scanFiles(list, isScanChildFolder, l);
    }

    public static boolean scanFiles(List<File> fileList, boolean isScanChildFolder, OnScanFileListener l) {
        if (fileList == null || fileList.size() == 0 || l == null) {
            return false;
        }
        final Queue<File> needScanFiles = new LinkedList<File>();
        Collections.sort(fileList, l);
        needScanFiles.addAll(fileList);
        while (!needScanFiles.isEmpty()) {
            if (l.interrupted()) {
                return false;
            }
            final File current = needScanFiles.poll();
            if (current.isFile()) {
                l.onScanFile(current, false);
                continue;
            }
            // else current is directory
            l.onScanFile(current, true);
            if (isScanChildFolder) {
                final File[] files = current.listFiles(l);
                if (files != null && files.length > 0) {
                    needScanFiles.addAll(Arrays.asList(files));
                }
            }
        }
        return true;
    }

    public static void scanTextFileLine(File file, OnScanLineListener l) {
        BufferedReader bw = null;
        String line = null;
        try {
            bw = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            while ((line = bw.readLine()) != null) {
                l.onScanLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(bw);
        }
    }
}
