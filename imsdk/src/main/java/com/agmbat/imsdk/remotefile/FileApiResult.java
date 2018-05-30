package com.agmbat.imsdk.remotefile;

import com.agmbat.imsdk.api.ApiResult;
import com.google.gson.annotations.SerializedName;

public class FileApiResult extends ApiResult<String> {

    /**
     * 错误码
     */
    @SerializedName("error_reason")
    public String errorReason;

    /**
     * 文件的url，调用者应当存储该url，以后用该url可以获取文件
     */
    @SerializedName("url")
    public String url;

    /**
     * 若是聊天图片（格式为jpg或png），会返回缩略图的url，调用者应当存储该url，以后用该url可以获取图片缩略图}
     */
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

}
