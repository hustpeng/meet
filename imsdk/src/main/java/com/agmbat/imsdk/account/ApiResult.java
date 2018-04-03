package com.agmbat.imsdk.account;

public class ApiResult<T> {

    /**
     * 请求是否成功
     */
    public boolean mResult;

    /**
     * 错误消息
     */
    public String mErrorMsg;


    /**
     * 当前api返回的结果
     */
    public T mData;
}
