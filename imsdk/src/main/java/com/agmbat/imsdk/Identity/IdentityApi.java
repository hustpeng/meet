package com.agmbat.imsdk.Identity;

import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 身份认证api
 */
public class IdentityApi {

    /**
     * 提交身份实名认证
     * POST
     * https://{DOMAIN}/egret/v1/user/auth.api
     * Body:
     * uid=<phone>&ticket=<ticket>&name=<>&identity=<>&photo_front=<>& photo_back=<>&sign=<sign>
     * <p>
     * 参数名	Required?	格式	意义
     * <p>
     * 返回内容如下：
     * {
     * "result":true //true  API调用成功，否则调用失败
     * }
     *
     * @param uid        用户11位手机号码，不含区号
     * @param ticket     The auth ticket
     * @param name       用户真实姓名
     * @param identity   身份证号码
     * @param photoFront 手持身份证正面上半身照片URL
     * @param photoBack  身份证背面照片URL
     * @return
     */
    public static ApiResult auth(String uid, String ticket, String name, String identity, String photoFront,
                                 String photoBack) {
        String apiName = "auth";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("POST");
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.postParam("uid", uid);
        builder.postParam("ticket", ticket);
        builder.postParam("name", name);
        builder.postParam("identity", identity);
        builder.postParam("photo_front", photoFront);
        builder.postParam("photo_back", photoBack);
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
