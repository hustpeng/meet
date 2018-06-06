package com.agmbat.imsdk.splash;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.ApkUtils;
import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 闪屏页api
 */
public class SplashApi {

    private static String sPackageName = null;
    private static int sAppVer = 0;

    /**
     * 是否使用模拟api返回的结果
     */
    private static final boolean ENABLE_MOCK = false;

    public static ApiResult<SplashResp> getSplash(String uid, int ver) {
        if (StringUtils.isEmpty(sPackageName)) {
            sPackageName = ApkUtils.getPackageName();
        }
        if (sAppVer == 0) {
            sAppVer = ApkUtils.getVersionCode();
        }
        return getSplash(uid, ver, sAppVer, sPackageName);
    }

    /**
     * 获取Splash信息
     * <p>
     * GET
     * https://{DOMAIN}/egret/v1/promotion/splash.api?uid=<phone>&ver=<ver>&appver=<appver>&package=<package>&sign=<sign>
     * <p>
     * {
     * "result": true,     //true  API调用成功，否则调用失败
     * "resp": {
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
     * }
     *
     * @param uid         用户11位手机号码，不含区号，可选
     * @param ver         本地SPLASH策略版本号，默认为0，可选
     * @param appver      int APP的版本号, 默认为0,可选
     * @param packageName 包名或APP标识, 例如cn.org.xmpp.egret, 必选
     * @return
     */
    public static ApiResult<SplashResp> getSplash(String uid, int ver, int appver, String packageName) {
        String url = Api.getDomain() + "/egret/v1/promotion/splash.api";
        String apiName = "splash";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ver", String.valueOf(ver));
        builder.urlParam("appver", String.valueOf(appver));
        builder.urlParam("package", packageName);
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/splash.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<ApiResult<SplashResp>>() {
        }.getType();
        ApiResult<SplashResp> apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

}
