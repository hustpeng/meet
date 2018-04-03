package com.agmbat.imsdk.account;

import android.text.TextUtils;

import com.agmbat.net.HttpRequester;
import com.agmbat.security.SecurityUtil;
import com.agmbat.text.JsonUtils;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 与服务端的接口
 */
public class AccountApi {

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
        builder.baseUrl(getBaseUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("sms", verificationCode);
        builder.urlParam("sign", getSign(apiName, phone));
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

    /**

     *
     * @return
     */

    /**
     * 修改密码
     * POST
     * https://https://www.xmpp.org.cn/egret/v1/user/changepwd.api
     * Body:
     * uid=<phone>&ticket=<ticket>&pwd=<password>&newpwd=< newpassword>&sign=<sign>
     *
     * @param phone  用户11位手机号码，不含区号
     * @param ticket The auth ticket
     * @param pwd    原密码
     * @param newpwd 新密码
     * @return
     */
    public static ApiResult changePassword(String phone, String ticket, String pwd, String newpwd) {
        String apiName = "changepwd";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("GET");
        builder.url(getBaseUrl(apiName));
        builder.postParam("uid", phone);
//        builder.postParam("ticket", ticket);
        builder.postParam("ticket", "abcd");
        builder.postParam("pwd", pwd);
        builder.postParam("newpwd", newpwd);
        builder.postParam("sign", getSign(apiName, phone));
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
     * 重围密码
     *
     * @param email
     * @return
     */
    public static boolean resetPassword(String email) {
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl("http://yuan520.com/password-service/forgetpassword.jsp");
        builder.urlParam("email", email);
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (!TextUtils.isEmpty(text)) {
            try {
                JSONObject json = new JSONObject(text);
                return json.optBoolean("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
