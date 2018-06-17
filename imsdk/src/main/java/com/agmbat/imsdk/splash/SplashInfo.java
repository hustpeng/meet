package com.agmbat.imsdk.splash;

import com.google.gson.annotations.SerializedName;

/**
 * {
 * "can_skip": true,
 * "display_time": 3,
 * "link": "https://www.xmpp.org.cn",
 * "url":"http://p2hflj6k5.bkt.clouddn.com/venus/splash/zoomzoom.jpg"
 * }
 */
public class SplashInfo {

    /**
     * 是否显示跳过
     */
    @SerializedName("can_skip")
    public boolean canSkip;

    /**
     * 显示多长时间
     */
    @SerializedName("display_time")
    public int displayTime;

    @SerializedName("link")
    public String link;

    @SerializedName("url")
    public String mImageUrl;
}
