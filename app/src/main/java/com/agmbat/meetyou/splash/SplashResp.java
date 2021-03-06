package com.agmbat.meetyou.splash;

import com.google.gson.annotations.SerializedName;

/**
 * {
 * "version": 8,
 * "expired_time": 1546185600,
 * "policy_id": 1,
 * "global_config": {
 * "sensitive_words": "狗日的,日你妈,操你老母,操你妈,绿茶婊,婊子养的,fuck"
 * },
 * "splash": {
 * "can_skip": true,
 * "display_time": 3,
 * "link": "https://www.xmpp.org.cn",
 * "url": "http://p2hflj6k5.bkt.clouddn.com/egret/splash/ad.jpg"
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

    @SerializedName("global_config")
    public GlobalConfig mGlobalConfig;

    /**
     * 全局配置
     */
    public static class GlobalConfig {

        /**
         * 敏感词
         */
        @SerializedName("sensitive_words")
        public String mSensitiveWords;
    }

}
