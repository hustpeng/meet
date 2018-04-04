package com.agmbat.meetyou;

import android.app.Application;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.splash.SplashManager;
import com.agmbat.meetyou.account.LoginActivity;

public class MeetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppResources.init(this);
        ImageManager.initImageLoader(this);
        SplashManager.init(LoginActivity.class.getName());
    }
}
