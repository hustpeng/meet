package com.agmbat.imsdk.splash;

import com.google.gson.annotations.SerializedName;

/**
 * {
 * "version": 8
 * "expired_time": 1546185600,
 * "policy_id": 1,
 * "splash": {
 * "can_skip": true,
 * "display_time": 3,
 * "link": "https://www.xmpp.org.cn",
 * "url":"http://p2hflj6k5.bkt.clouddn.com/venus/splash/zoomzoom.jpg"
 * }
 * }
 */
public class SplashResp {

    @SerializedName("version")
    public int version;

    @SerializedName("expired_time")
    public long expired_time;

    @SerializedName("policy_id")
    public int policy_id;

    @SerializedName("splash")
    public SplashInfo mSplashInfo;

    /**
     * {
     * "can_skip": true,
     * "display_time": 3,
     * "link": "https://www.xmpp.org.cn",
     * "url":"http://p2hflj6k5.bkt.clouddn.com/venus/splash/zoomzoom.jpg"
     * }
     */
    public static class SplashInfo {
        @SerializedName("can_skip")
        public boolean can_skip;

        @SerializedName("display_time")
        public int display_time;

        @SerializedName("link")
        public String link;

        @SerializedName("url")
        public String mImageUrl;
    }

}
