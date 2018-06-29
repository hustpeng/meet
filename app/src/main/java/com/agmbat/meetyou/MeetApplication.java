package com.agmbat.meetyou;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.app.ActivityStack;
import com.agmbat.appupdate.AppVersionHelper;
import com.agmbat.crashreport.CrashReporter;
import com.agmbat.imsdk.splash.SplashManager;
import com.agmbat.meetyou.account.LoginActivity;
import com.agmbat.meetyou.checkupdate.UpdateApi;
import com.baidu.mapapi.SDKInitializer;

public class MeetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initPhotoError();
        AppResources.init(this);
        ActivityStack.init(this);
        CrashReporter.init(this);
        ImageManager.initImageLoader(this);
        SplashManager.init(LoginActivity.class.getName());
        SDKInitializer.initialize(this);
        AppVersionHelper.setAppVersionInfoRequester(new UpdateApi());

    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }
}
