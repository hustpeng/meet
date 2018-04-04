package com.agmbat.imsdk.splash;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.ImageView;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.R;
import com.agmbat.imsdk.api.ApiResult;
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

    private static final String PREF_FILE = "splash";

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
     * 显示闪屏图片
     *
     * @param imageView
     */
    public static void displaySplash(ImageView imageView) {
        String path = getSplashImagePath();
        String uri = null;
        if (StringUtils.isEmpty(path)) {
            uri = Scheme.wrapUri("drawable", String.valueOf(R.drawable.im_default_splash));
        } else {
            uri = Scheme.wrapUri("file", path);
        }
        ImageManager.displayImage(uri, imageView);
        update();
    }

    private static void update() {
        final String phone = "13437122759";
        final int splashVer = getSplashVersion();
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult<SplashResp>>() {
            @Override
            protected ApiResult<SplashResp> doInBackground(Void... voids) {
                ApiResult<SplashResp> result = SplashApi.getSplash(phone, splashVer);
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
        if (result == null) {
            return false;
        }
        if (result.mData == null) {
            return false;
        }
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
        if (result == null) {
            return;
        }
        String url = result.mData.mSplashInfo.mImageUrl;
        File file = getDownloadImageFile(url);
        String path = file.getAbsolutePath();
        setSplashImagePath(path);
        setSplashVersion(result.mData.version);
    }

    private static String getSplashImagePath() {
        return getSplashPrefs().getString("image", "");
    }

    private static void setSplashImagePath(String path) {
        getSplashPrefs().edit().putString("image", path).commit();
    }

    /**
     * 获取splash version
     *
     * @return
     */
    private static int getSplashVersion() {
        return getSplashPrefs().getInt("version", 0);
    }

    /**
     * 保存版本号
     *
     * @param version
     */
    private static void setSplashVersion(int version) {
        getSplashPrefs().edit().putInt("version", version).commit();
    }

    private static SharedPreferences getSplashPrefs() {
        return AppResources.getAppContext().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
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
        File dir = new File(Environment.getExternalStorageDirectory(), "splash");
        FileUtils.ensureDir(dir);
        return dir;
    }
}
