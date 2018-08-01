package com.agmbat.meetyou;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ThreadUtil;
import com.agmbat.app.ActivityStack;
import com.agmbat.appupdate.AppVersionHelper;
import com.agmbat.crashreport.CrashReporter;
import com.agmbat.imsdk.util.VLog;
import com.agmbat.meetyou.splash.SplashManager;
import com.agmbat.meetyou.account.LoginActivity;
import com.agmbat.meetyou.checkupdate.UpdateApi;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;

public class MeetApplication extends Application {

    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        VLog.setDebug(BuildConfig.DEBUG);
        VLog.setTag("Meet");
        initPhotoError();
        AppResources.init(this);
        ActivityStack.init(this);
        CrashReporter.init(this);
        ImageManager.initImageLoader(this);
        SplashManager.init(LoginActivity.class.getName());
        SDKInitializer.initialize(this);
        AppVersionHelper.setAppVersionInfoRequester(new UpdateApi());
        if (ThreadUtil.isOnMainProcess(this)) {
            ConnectionReceiver.register(this);
        }
        initDatabaseBrowser();
    }

    public static Application getInstance(){
        return sInstance;
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    /**
     * 这是一个可在chrome查看app数据库的开发工具
     * 使用方法：
     * 步骤一：引入依赖库 compile 'com.facebook.stetho:stetho:1.3.1'
     * 步骤二：运行App, 打开Chrome输入chrome://inspect/#devices（推荐只在debug状态下初始化）
     */
    private void initDatabaseBrowser() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
