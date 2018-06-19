package com.agmbat.imsdk.reportuser;

import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ReportUserApi {

    /**
     * 举报用户
     * POST
     * https://{DOMAIN}/egret/v1/user/reportuser.api
     * Body:
     * uid=<phone>&ticket=<ticket>&target_uid=<>&content=<>&photo_url=<>&sign=<sign>
     * <p>
     * 返回内容如下：
     * {
     * "result":true   //true  API调用成功，否则调用失败
     * }
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param targetUid 举报对象的uid
     * @param content   举报内容
     * @param photoUrl  证据截图URL
     * @return
     */
    public static ApiResult reportUser(String uid, String ticket, String targetUid, String content, String photoUrl) {
        String apiName = "reportuser";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.method("POST");
        builder.postParam("uid", uid);
        builder.postParam("ticket", ticket);
        builder.postParam("target_uid", targetUid);
        builder.postParam("content", content);
        builder.postParam("photo_url", photoUrl);
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


    /**
     * 举报群
     * POST
     * https://{DOMAIN}/egret/v1/user/reportgroup.api
     * Body:
     * uid=<phone>&ticket=<ticket>&target_uid=<>&content=<>&photo_url=<>&sign=<sign>
     * 返回内容如下：
     * <p>
     * {
     * "result":true  //true  API调用成功，否则调用失败
     * }
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param targetUid 举报群id
     * @param content   举报内容
     * @param photoUrl  证据截图URL
     * @return
     */
    public static ApiResult reportGroup(String uid, String ticket, String targetUid, String content, String photoUrl) {
        String apiName = "reportgroup";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.method("POST");
        builder.postParam("uid", uid);
        builder.postParam("ticket", ticket);
        builder.postParam("target_uid", targetUid);
        builder.postParam("content", content);
        builder.postParam("photo_url", photoUrl);
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
