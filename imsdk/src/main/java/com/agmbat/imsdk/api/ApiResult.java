package com.agmbat.imsdk.api;

import com.google.gson.annotations.SerializedName;

public class ApiResult<T> {

    /**
     * 请求是否成功
     */
    @SerializedName("result")
    public boolean mResult;

    /**
     * 错误消息
     */
    @SerializedName("msg")
    public String mErrorMsg;

    /**
     * 当前api返回的结果
     */
    @SerializedName("resp")
    public T mData;
}
