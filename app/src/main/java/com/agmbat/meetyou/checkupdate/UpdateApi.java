package com.agmbat.meetyou.checkupdate;

import com.agmbat.android.utils.ApkUtils;
import com.agmbat.appupdate.AppVersionInfo;
import com.agmbat.appupdate.AppVersionInfoRequester;
import com.agmbat.imsdk.api.Api;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 13.	 <p>
 * 参数名	Required?	格式	意义
 * <p>
 * sign	Yes	String	API调用签名
 * <p>
 * 返回内容如下：
 * <p>
 * {
 * "result": true,  //true  API调用成功，否则调用失败
 * "resp": {
 * "app_version":2,
 * "title": "版本紧急更新",
 * "changelog": "美化程序界面;修复已知Bugs",
 * "url": " http://p1lplw7q9.bkt.clouddn.com/apk/new.apk",
 * "url_backup": "",
 * "can_skip": true
 * }
 * }
 */
public class UpdateApi implements AppVersionInfoRequester {

    /**
     * 获取APP更新策略
     * GET
     * https://{DOMAIN}/egret/v1/promotion/update.api?uid=<phone>&appver=< appver>&package=<package>&sign=<sign>
     *
     * @param uid         用户11位手机号码，不含区号
     * @param appver      APP的版本号,, 默认为0
     * @param packageName 包名或APP标识, 例如cn.org.xmpp.egret
     * @return
     */
    public static UpdateApiResult requestCheckUpdate(String uid, int appver, String packageName) {
        String apiName = "update";
        String url = Api.getDomain() + "/egret/v1/promotion/" + apiName + ".api";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("appver", String.valueOf(appver));
        builder.urlParam("package", packageName);
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
//        if (ENABLE_MOCK) {
//            text = AppResources.readAssetFile("apimock/coins.api.json");
//        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<UpdateApiResult>() {
        }.getType();
        UpdateApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

    @Override
    public AppVersionInfo request(String packageName, int versionCode) {
        String uid = "13437122759";
        UpdateApiResult apiResult = requestCheckUpdate(uid, versionCode, packageName);
        if (apiResult == null || !apiResult.mResult) {
            return null;
        }
        UpdateInfo info = apiResult.mData;

        AppVersionInfo appVersionInfo = new AppVersionInfo();
        appVersionInfo.setUrl(info.url);
        appVersionInfo.setDescription(info.changelog);

        boolean hasUpdate = false;
        if (ApkUtils.getVersionCode() < info.app_version) {
            hasUpdate = true;
        }
        if (hasUpdate) {
            if (info.can_skip) {
                appVersionInfo.setUpgradeStrategy(0);
            } else {
                appVersionInfo.setUpgradeStrategy(1);
            }
        } else {
            appVersionInfo.setUpgradeStrategy(2);
        }
        return appVersionInfo;
    }
}
