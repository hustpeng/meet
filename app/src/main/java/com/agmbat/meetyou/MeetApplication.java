package com.agmbat.meetyou;

import android.app.Application;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;

public class MeetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppResources.init(this);
        ImageManager.initImageLoader(this);
    }
}
