package com.agmbat.imsdk.mgr;

import android.os.Environment;

import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.asmack.XMPPManager;

import java.io.File;

/**
 * 登陆用户文件管理
 */
public class UserFileManager {

    private static final String IMAGE = "image";

    /**
     * 获取当前用户图片目录
     *
     * @return
     */
    public static File getCurImageDir() {
        String user = XMPPManager.getInstance().getConnectionUserName();
        return getImageDir(user);
    }

    /**
     * 获取用户的图片目录
     *
     * @return
     */
    public static File getImageDir(String user) {
        File dir = new File(getUserRootDir(user), IMAGE);
        FileUtils.ensureDir(dir);
        return dir;
    }

    /**
     * 获取指定用户的目录
     *
     * @param user
     * @return
     */
    public static File getUserRootDir(String user) {
        File dir = new File(getUserDir(), user);
        FileUtils.ensureDir(dir);
        return dir;
    }

    /**
     * 获取用户目录
     * /zhenyue/users
     *
     * @return
     */
    public static File getUserDir() {
        File dir = new File(getAppDir(), "users");
        FileUtils.ensureDir(dir);
        return dir;
    }

    /**
     * 获取 app 目录
     *
     * @return
     */
    public static File getAppDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), "zhenyue");
        FileUtils.ensureDir(dir);
        return dir;
    }
}
