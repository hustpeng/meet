package com.agmbat.meetyou;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.splash.SplashManager;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.account.LoginActivity;

public class MeetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initPhotoError();
        AppResources.init(this);
        ImageManager.initImageLoader(this);
        SplashManager.init(LoginActivity.class.getName());
        UserManager.getInstance();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
