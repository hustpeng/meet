package com.agmbat.imsdk.mgr;

import android.os.Environment;

import com.agmbat.app.AppFileManager;
import com.agmbat.net.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class XmppFileManager {

    private static final String APPLICATION_DIR = Environment.getExternalStorageDirectory() + File.separator + "Meet";
    public static final String RECEIVE_FILE_PATH = APPLICATION_DIR + File.separator + "ReceiveFiles";
    private static final String AVATAR_DIR = APPLICATION_DIR + File.separator + "Avatar";
    private static final String CROP_DIR = AVATAR_DIR + File.separator + "Crop";
    private static final String CAMERA_DIR = Environment.getExternalStorageDirectory() + "/DCIM/Camera";

    public static File getRecordFile(String url) {
        return new File(AppFileManager.getRecordDir(), HttpUtils.getFileNameFromUrl(url));
    }

    public static File getChatFileDir() {
        return AppFileManager.getExternalCacheDir("chatFile");
    }

    public static File getImageDir() {
        File dir = AppFileManager.getExternalCacheDir("ImageCache");
        File file = new File(dir, ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dir;
    }

    public static File getAvatarFile(String key) {
        File folder = new File(XmppFileManager.AVATAR_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, key);
    }

    public static File getTakePhotoFile() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        String fileName = dateFormat.format(date) + ".jpg";
        File photoFile = new File(CAMERA_DIR, fileName);
        File parentFile = photoFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return photoFile;
    }

    public static File getCropPhotoFile() {
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(CROP_DIR, fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return file;
    }

    public static void deleteCacheFiles() {
        File baseFolder = new File(APPLICATION_DIR);
        LinkedList<File> directories = new LinkedList<File>();
        File[] files = baseFolder.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    directories.add(file);
                } else {
                    file.delete();
                }
            }
        }

        while (!directories.isEmpty()) {
            File currentDir = directories.poll();
            if (currentDir.isDirectory()) {
                File[] subDirs = currentDir.listFiles();
                if (null == subDirs || subDirs.length == 0) {
                    currentDir.delete();
                } else {
                    for (int i = 0; i < subDirs.length; i++) {
                        File subFile = subDirs[i];
                        if (subFile.isDirectory()) {
                            directories.add(subFile);
                        } else {
                            subFile.delete();
                        }
                    }
                }
            } else {
                currentDir.delete();
            }
        }
        baseFolder.delete();
    }

}
