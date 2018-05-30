package com.agmbat.imsdk.remotefile;

import com.agmbat.imsdk.api.Api;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

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
    public static FileApiResult uploadAvatar(String phone, String ticket, String format, File imageFile) {
        String apiName = "upload_avatar";
        return uploadFile(apiName, phone, ticket, format, imageFile);
    }

    /**
     * 上传普通临时文件
     * POST
     * https://{DOMAIN}/egret/v1/user/upload_temp.api?uid=<uid>&ticket=<ticket>&fotmat=<>&sign=<sign>
     * <p>
     * POST BODY就是图片内容的字节数组。
     * 如果uid和ticket不匹配，返回403错误码。
     * 对于临时图片，服务器会在一定时间后删除，但这个时间不保证，一般会超过两周。
     * <p>
     * 其它情况的返回内容如下：
     * <p>
     * {
     * "result":true, //true  API调用成功，否则调用失败，下面有错误原因
     * "error_reason":"……",//错误码
     * "url":"http://……",//文件的url，调用者应当存储该url，以后用该url可以获取文件
     * "thumbnail_url":"http://……"//若是聊天图片（格式为jpg或png），会返回缩略图的url，调用者应当存储该url，以后用该url可以获取图片缩略图}
     *
     * @param uid    Jabber id，no resource part
     * @param ticket The auth ticket
     * @param format png,jpg,zip,etc…, 不带”.”
     * @param file
     * @return
     */
    public static FileApiResult uploadTempFile(String uid, String ticket, String format, File file) {
        String apiName = "upload_temp";
        return uploadFile(apiName, uid, ticket, format, file);
    }


    /**
     * 上传其他持久化文件（包括身份认证图片, 举报的证据截图，反馈建议截图等）
     * POST
     * https://{DOMAIN}/egret/v1/user/upload_common.api?uid=<phone>&ticket=<ticket>&format=<>&sign=<sign>
     * <p>
     * 参数名	Required?	格式	意义
     * POST BODY就是图片内容的字节数组。
     * 如果uid和ticket不匹配，返回403错误码。
     * 其它情况的返回内容如下：
     * <p>
     * {
     * "result":true, //true  API调用成功，否则调用失败
     * "error_reason":"uploaded_exceeded",//错误码
     * "url":"http://……" ,//文件的url，调用者应当存储该url，以后用该url可以获取该文件
     * thumbnail_url":"http://……"//若是图片（格式为jpg或png），会返回缩略图的url，调用者应当存储该url，以后用该url可以获取图片缩略图
     * }
     *
     * @param uid    Phone或群号
     * @param ticket The auth ticket
     * @param format png,jpg,etc…, 不带”.”
     * @param file
     * @return
     */
    public static FileApiResult uploadCommonFile(String uid, String ticket, String format, File file) {
        String apiName = "upload_common";
        return uploadFile(apiName, uid, ticket, format, file);
    }

    /**
     * 上传文件
     *
     * @param apiName api名称
     * @param uid     Jabber id，no resource part
     * @param ticket  The auth ticket
     * @param format  png,jpg,etc…, 不带”.”
     * @param file
     * @return
     */
    private static FileApiResult uploadFile(String apiName, String uid, String ticket, String format, File file) {
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.method("POST");
        builder.baseUrl(Api.getBaseUserUrl(apiName));
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("format", format);
        builder.urlParam("sign", Api.getSign(apiName, uid));
        builder.addFilePart("file", file);
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<FileApiResult>() {
        }.getType();
        FileApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

}
