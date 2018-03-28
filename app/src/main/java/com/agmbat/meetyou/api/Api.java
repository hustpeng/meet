package com.agmbat.meetyou.api;

import com.agmbat.net.HttpRequester;
import com.agmbat.net.HttpUtils;
import com.agmbat.security.SecurityUtil;
import com.agmbat.text.JsonUtils;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;

import org.json.JSONObject;

/**
 * 与服务端的接口
 */
public class Api {

    /**
     * api服务器地址
     */
    private static final String DOMAIN = "https://www.xmpp.org.cn";

    /**
     * App code, 开发代号内部常量”egret”
     */
    private static final String APP_CODE = "egret";

    /**
     * 获取短信验证码
     * <p>
     * https://www.xmpp.org.cn/egret/v1/user/sms.api?uid=13400000000&sign=abc
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "msg":""       //若result为false,则msg返回出错信息，否则为""字符串
     * }
     *
     * @param phone
     * @return
     */
    public static ApiResult getVerificationCode(String phone) {
        String apiName = "sms";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(getBaseUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sign", getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = HttpUtils.request(requester);
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
     * 检测用户是否存在
     * <p>
     * https://www.xmpp.org.cn/egret/v1/user/existed.api?uid=13400000000&sign=abc
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "existed":true  //true：已注册，false：未注册
     * }
     *
     * @param phone
     * @return
     */
    public static ApiResult<Boolean> existedUser(String phone) {
        String apiName = "existed";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(getBaseUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sign", getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = HttpUtils.request(requester);
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
     * @param code
     * @return
     */
    public static ApiResult<Boolean> checkSms(String phone, String code) {
        String apiName = "checksms";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(getBaseUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sms", code);
        builder.urlParam("sign", getSign(apiName, phone));
        HttpRequester requester = builder.build();
        String text = HttpUtils.request(requester);
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
     * 获取BaseUrl
     *
     * @param apiName
     * @return
     */
    private static String getBaseUrl(String apiName) {
        return DOMAIN + "/" + APP_CODE + "/v1/user/" + apiName + ".api";
    }

    /**
     * 获取签名
     *
     * @param apiName
     * @param phone
     * @return
     */
    private static String getSign(String apiName, String phone) {
        String text = APP_CODE + ":" + apiName + ":" + phone;
        String base64Text = Base64.encodeToString(StringUtils.getUtf8Bytes(text), Base64.DEFAULT).trim();
        String md5Text = SecurityUtil.md5Hash(base64Text);
        return md5Text;
    }
}
