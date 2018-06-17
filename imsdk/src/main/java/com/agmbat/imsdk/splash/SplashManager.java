package com.agmbat.imsdk.splash;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.R;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.mgr.UserFileManager;
import com.agmbat.net.HttpUtils;
import com.agmbat.security.SecurityUtil;
import com.agmbat.text.StringUtils;
import com.nostra13.universalimageloader.core.download.Scheme;

import java.io.File;

/**
 * 闪屏页面管理
 */
public class SplashManager {

    /**
     * 需要进入的类
     */
    private static String sMainClassName;

    /**
     * 初始化
     *
     * @param mainClass
     */
    public static void init(String mainClass) {
        sMainClassName = mainClass;
    }

    public static String getMainClassName() {
        return sMainClassName;
    }

    /**
     * 获取当前用于显示的Splash信息
     *
     * @return
     */
    public static SplashInfo getDisplaySplash() {
        SplashInfo display = new SplashInfo();
        SplashInfo splashInfo = SplashStore.getSplashInfo();
        if (splashInfo != null) {
            String url = splashInfo.mImageUrl;
            File file = getDownloadImageFile(url);
            String path = file.getAbsolutePath();
            String uri = null;
            if (FileUtils.existsFile(path)) {
                uri = Scheme.wrapUri("file", path);
            } else {
                uri = Scheme.wrapUri("drawable", String.valueOf(R.drawable.im_default_splash));
            }
            display.canSkip = splashInfo.canSkip;
            if (splashInfo.displayTime > 0) {
                display.displayTime = splashInfo.displayTime;
            } else {
                display.displayTime = 3000;
            }
            display.mImageUrl = uri;
        } else {
            display.canSkip = false;
            display.displayTime = 3000;
            display.mImageUrl = Scheme.wrapUri("drawable", String.valueOf(R.drawable.im_default_splash));
        }
        return display;
    }

    public static void update() {
        final String phone = "13437122759";
        final int splashVer = SplashStore.getSplashVersion();
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult<SplashResp>>() {
            @Override
            protected ApiResult<SplashResp> doInBackground(Void... voids) {
                ApiResult<SplashResp> result = SplashApi.getSplash(phone, splashVer);
                if (result == null || !result.mResult) {
                    return null;
                }
                if (result.mData == null) {
                    return null;
                }

                if (result.mData.mGlobalConfig != null) {
                    SplashStore.saveSensitiveWords(result.mData.mGlobalConfig.mSensitiveWords);
                }

                boolean success = downloadImage(result);
                if (success) {
                    return result;
                }
                return null;
            }

            @Override
            protected void onPostExecute(ApiResult<SplashResp> result) {
                super.onPostExecute(result);
                saveSplash(result);
            }
        });
    }

    /**
     * 下载图片
     *
     * @param result
     * @return
     */
    private static boolean downloadImage(ApiResult<SplashResp> result) {
        if (result.mData.mSplashInfo == null) {
            return false;
        }
        String url = result.mData.mSplashInfo.mImageUrl;
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        File file = getDownloadImageFile(url);
        if (file.exists()) {
            return true;
        }
        boolean success = HttpUtils.downloadFile(url, file);
        return success;
    }

    /**
     * 保存splash信息
     *
     * @param result
     */
    private static void saveSplash(ApiResult<SplashResp> result) {
        if (result == null || !result.mResult || result.mData == null) {
            return;
        }
        SplashStore.saveSplashInfo(result.mData.mSplashInfo);
        SplashStore.setSplashVersion(result.mData.version);
    }

    /**
     * 获取下载文件地址
     *
     * @param url
     * @return
     */
    private static File getDownloadImageFile(String url) {
        String name = SecurityUtil.md5Hash(url) + ".jpg";
        File file = new File(getSplashDir(), name);
        return file;
    }

    /**
     * splash存放在目录
     *
     * @return
     */
    private static File getSplashDir() {
        File appDir = UserFileManager.getAppDir();
        File dir = new File(appDir, "splash");
        FileUtils.ensureDir(dir);
        return dir;
    }
}
