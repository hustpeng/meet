package com.agmbat.imsdk.remotefile;

import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.text.JsonUtils;
import com.agmbat.text.StringUtils;

import org.json.JSONObject;

import java.io.File;

/**
 * 文件上传相关api
 */
public class FileApi {

    /**
     * 上传用户/群头像持久化图片
     * POST
     * https://{DOMAIN}/egret/v1/user/upload_avatar.api?uid=<phone>&ticket=<ticket>&format=<>&sign=<sign>
     * <p>
     * 参数名	Required?	格式	意义
     * uid	Yes	String	Phone
     * ticket	YES	String	The auth ticket
     * format	Yes	String	png,jpg,etc…, 不带”.”
     * circle_jid	No	String	群JID，若是上传为群头像，则需提供该值
     * sign	Yes	String	API调用签名
     * POST BODY就是图片内容的字节数组。
     * 如果uid和ticket不匹配，返回403错误码。
     * 其它情况的返回内容如下：
     * <p>
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "error_reason":"uploaded_exceeded",//错误码
     * "url":"http://……"//文件的url，调用者应当存储该url，以后用该url可以获取该文件
     * }
     */
    public static ApiResult<String> uploadAvatar(String phone, String ticket, String format, byte[] imageData) {
        String apiName = "upload_avatar";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("POST");
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("ticket", ticket);
        builder.urlParam("format", format);
        builder.urlParam("sign", Api.getSign(apiName, phone));
        builder.entity(imageData);
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        if (jsonObject == null) {
            return null;
        }
        ApiResult<String> apiResult = new ApiResult<String>();
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mErrorMsg = jsonObject.optString("error_reason");
        apiResult.mData = jsonObject.optString("url");
        return apiResult;
    }

    /**
     * 上传用户/群头像持久化图片
     * POST
     * https://{DOMAIN}/egret/v1/user/upload_avatar.api?uid=<phone>&ticket=<ticket>&format=<>&sign=<sign>
     * <p>
     * 参数名	Required?	格式	意义
     * uid	Yes	String	Phone
     * ticket	YES	String	The auth ticket
     * format	Yes	String	png,jpg,etc…, 不带”.”
     * circle_jid	No	String	群JID，若是上传为群头像，则需提供该值
     * sign	Yes	String	API调用签名
     * POST BODY就是图片内容的字节数组。
     * 如果uid和ticket不匹配，返回403错误码。
     * 其它情况的返回内容如下：
     * <p>
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "error_reason":"uploaded_exceeded",//错误码
     * "url":"http://……"//文件的url，调用者应当存储该url，以后用该url可以获取该文件
     * }
     */
    public static ApiResult<String> uploadAvatar(String phone, String ticket, String format, File imageFile) {
        String apiName = "upload_avatar";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("POST");
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.urlParam("uid", phone);
        builder.urlParam("ticket", ticket);
        builder.urlParam("format", format);
        builder.urlParam("sign", Api.getSign(apiName, phone));
        builder.addFilePart("file", imageFile);
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        JSONObject jsonObject = JsonUtils.asJsonObject(text);
        if (jsonObject == null) {
            return null;
        }
        ApiResult<String> apiResult = new ApiResult<String>();
        apiResult.mResult = jsonObject.optBoolean("result");
        apiResult.mErrorMsg = jsonObject.optString("error_reason");
        apiResult.mData = jsonObject.optString("url");
        return apiResult;
    }
}
