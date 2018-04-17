package com.agmbat.imsdk.account;

import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.text.JsonUtils;
import com.agmbat.text.StringUtils;

import org.json.JSONObject;

/**
 * 与服务端的接口
 */
public class AccountApi {

    /**
     * 获取短信验证码, 针对找回密码
     * <p>
     * https://www.xmpp.org.cn/egret/v1/user/sms.api?uid=13400000000&sign=abc
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "existed":true,  //true：该手机号存在并发送短信验证码，false：号码未注册
     * "msg":""       //若result为false,则msg返回出错信息，否则为""字符串
     * }
     *
     * @param phone
     * @return
     */
    public static ApiResult<Boolean> getVerificationCode(String phone) {
        String apiName = "sms";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sign", Api.getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        ApiResult<Boolean> apiResult = new ApiResult<Boolean>();
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mData = jsonObject.optBoolean("existed");
        apiResult.mErrorMsg = jsonObject.optString("msg");
        return apiResult;
    }

    /**
     * 检测用户是否存在
     * <p>
     * https://www.xmpp.org.cn/egret/v1/user/existed.api?uid=13400000000&sign=abc
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "existed":true  true：已注册，false：未注册并发送短信验证码
     * }
     *
     * @param phone
     * @return
     */
    public static ApiResult<Boolean> existedUser(String phone) {
        String apiName = "existed";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sign", Api.getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        ApiResult<Boolean> apiResult = new ApiResult<Boolean>();
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mData = jsonObject.optBoolean("existed");
        return apiResult;
    }

    /**
     * 验证短信验证码合法性
     *
     * @param phone
     * @param verificationCode
     * @return
     */
    public static ApiResult<Boolean> checkSms(String phone, String verificationCode) {
        String apiName = "checksms";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sms", verificationCode);
        builder.urlParam("sign", Api.getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        ApiResult<Boolean> apiResult = new ApiResult<Boolean>();
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mData = jsonObject.optBoolean("existed");
        return apiResult;
    }

    /**
     * @param phone       用户11位手机号码，不含区号
     * @param ticket      The auth ticket
     * @param pwd         原密码
     * @param newPassword 新密码
     * @return
     */
    public static ApiResult changePassword(String phone, String ticket, String pwd, String newPassword) {
        String apiName = "changepwd";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("POST");
        builder.url(Api.getBaseUserUrl(apiName));
        builder.postParam("uid", phone);
        builder.postParam("ticket", ticket);
        builder.postParam("pwd", pwd);
        builder.postParam("newpwd", newPassword);
        builder.postParam("sign", Api.getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        ApiResult apiResult = new ApiResult();
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mErrorMsg = jsonObject.optString("msg");
        return apiResult;
    }

    /**
     * 重置密码
     * <p>
     * POST
     * https://{DOMAIN}/egret/v1/user/resetpwd.api
     * Body:
     * uid=<phone>&sms=<sms code>&newpwd=<newpassword>&sign=<sign>
     *
     * @param phone            用户11位手机号码，不含区号
     * @param newPassword      新密码
     * @param verificationCode 短信验证码
     * @return
     */
    public static ApiResult resetPassword(String phone, String newPassword, String verificationCode) {
        String apiName = "resetpwd";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("POST");
        builder.url(Api.getBaseUserUrl(apiName));
        builder.postParam("uid", phone);
        builder.postParam("sms", verificationCode);
        builder.postParam("newpwd", newPassword);
        builder.postParam("sign", Api.getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        ApiResult apiResult = new ApiResult();
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mErrorMsg = jsonObject.optString("msg");
        return apiResult;
    }
}
