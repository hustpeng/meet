package com.agmbat.meetyou.api;

import com.agmbat.net.HttpRequester;
import com.agmbat.net.HttpUtils;
import com.agmbat.security.SecurityUtil;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;

public class Api {


    private static final String DOMAIN = "https://www.xmpp.org.cn";
    /**
     * App code, 开发代号内部常量”egret”
     */
    private static final String APP_CODE = "egret";

    private static final String SMS_API_NAME = "sms";

    private static final String url = DOMAIN + "/egret/v1/user/sms.api?uid=13437122759&sign=abc";

    /**
     * 获取短信验证码
     *
     * @param phone
     * @return
     */
    public static String getVerificationCode(String phone) {
        HttpRequester.Builder builder = new HttpRequester.Builder();
        // https://www.xmpp.org.cn/egret/v1/user/sms.api
        builder.baseUrl(getBaseUrl(SMS_API_NAME));
        builder.urlParam("uid", phone);
        builder.urlParam("sign", getSign(SMS_API_NAME, phone));
        HttpRequester requester = builder.build();
        return HttpUtils.request(requester);
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
