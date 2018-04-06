package com.agmbat.imsdk.api;

import com.agmbat.security.SecurityUtil;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;

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
     * 获取域名
     *
     * @return
     */
    public static String getDomain() {
        return DOMAIN;
    }

    /**
     * 获取BaseUrl
     *
     * @param apiName
     * @return
     */
    public static String getBaseUserUrl(String apiName) {
        return DOMAIN + "/" + APP_CODE + "/v1/user/" + apiName + ".api";
    }

    /**
     * 获取签名
     *
     * @param apiName
     * @param phone
     * @return
     */
    public static String getSign(String apiName, String phone) {
        String text = APP_CODE + ":" + apiName + ":" + phone;
        String base64Text = Base64.encodeToString(StringUtils.getUtf8Bytes(text), Base64.DEFAULT).trim();
        String md5Text = SecurityUtil.md5Hash(base64Text);
        return md5Text;
    }

}
