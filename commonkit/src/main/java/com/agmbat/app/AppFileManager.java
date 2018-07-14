package com.agmbat.app;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.ApkUtils;
import com.agmbat.android.utils.StorageUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.log.Log;

import java.io.File;

public class AppFileManager {

    /**
     * 获取应用在sdcard创建的cache目录
     */
    public static File getExternalCacheDir() {
        Context context = AppResources.getAppContext();
        File cacheDir = context.getExternalCacheDir();
        FileUtils.ensureDir(cacheDir);
        return cacheDir;
    }

    public static File getRecordDir() {
        return getExternalCacheDir("Record");
    }

    /**
     * <pre class="prettyprint">
     * <p>
     * public static File getDataCacheDir() {
     * return getExternalCacheDir("DataCache");
     * }
     *
     * @param name
     * @return
     */
    public static File getExternalCacheDir(String name) {
        File dir = new File(getExternalCacheDir(), name);
        FileUtils.ensureDir(dir);
        return dir;
    }

    public static File getHomeDir() {
        if (!StorageUtils.isSDCardAvailable()) {
            Log.e("SdcardManager", "No Sdcrad");
        }
        String appName = AppResources.getString("app_name");
        if (TextUtils.isEmpty(appName)) {
            appName = ApkUtils.getAppName();
        }
        File homeDir = new File(Environment.getExternalStorageDirectory(), appName);
        FileUtils.ensureDir(homeDir);
        return homeDir;
    }

    /**
     * <pre class="prettyprint">
     * <p>
     * public static File getTempDir() {
     * return getSdcardDir(DIR_TEMP);
     * }
     *
     * @param name
     * @return
     */
    public static File getSdcardDir(String name) {
        File dir = new File(getHomeDir(), name);
        FileUtils.ensureDir(dir);
        return dir;
    }

}
