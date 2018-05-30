package com.agmbat.imsdk.feedback;

import com.agmbat.android.utils.ApkUtils;
import com.agmbat.android.utils.PhoneUtils;
import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FeedbackApi {

    /**
     * 提交意见反馈
     * POST
     * https://{DOMAIN}/egret/v1/user/feedback.api
     * Body:
     * uid=<phone>&ticket=<ticket>&content=<content>&photo_url=<photo_url>& devicetype=<devicetype>&osver=<osver>&appver=<appver>&sign=<sign>
     * <p>
     * 返回内容如下：
     * {
     * "result":true   //true  API调用成功，否则调用失败
     * }
     *
     * @param uid      用户11位手机号码，不含区号
     * @param ticket   The auth ticket
     * @param content  反馈内容
     * @param photoUrl 图片资源URL
     * @return
     */
    public static ApiResult feedback(String uid, String ticket, String content, String photoUrl) {
        String apiName = "feedback";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.method("POST");
        builder.postParam("uid", uid);
        builder.postParam("ticket", ticket);
        builder.postParam("content", content);
        builder.postParam("photo_url", photoUrl);
        // 设备类型，例如aphone，apad
        builder.postParam("devicetype", PhoneUtils.getDeviceModel());
        // OS版本
        builder.postParam("osver", PhoneUtils.getAndroidVersion());
        // Integer	APP的版本号
        builder.postParam("appver", String.valueOf(ApkUtils.getVersionCode()));
        builder.postParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<ApiResult>() {
        }.getType();
        ApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

}
