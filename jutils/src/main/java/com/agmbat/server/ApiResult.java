package com.agmbat.server;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class ApiResult<T> {

    /**
     * 请求成功
     */
    public static final int OK = 0;

    /**
     * 该请求不合法
     */
    public static final int BAD_REQUEST = 501;

    /**
     * 登陆失效
     */
    public static final int LOGIN_INVALID = 505;

    /**
     * 没有权限
     */
    public static final int NO_PERMISSION = 506;

    /**
     * 手势登录失败，与注册的手势不符
     */
    public static final int VALIDATE_GESTURE_FAILED = 510;

    @SerializedName("data")
    private T mData;

    @SerializedName("status")
    private int mStatus;

    @SerializedName("statusInfo")
    private String mStatusInfo;

    /**
     * 将json序列化成对象
     *
     * @param json
     * @param typeOfT
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Type typeOfT) {
        return GsonHelper.fromJson(json, typeOfT);
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getStatusInfo() {
        return mStatusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        mStatusInfo = statusInfo;
    }

    /**
     * 判断请求是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return mStatus == OK;
    }

    /**
     * 判断登录状态是否失效，需要重新登陆
     *
     * @return
     */
    public boolean isLoginInvalid() {
        return mStatus == LOGIN_INVALID;
    }

    /**
     * 是否有访问权限
     *
     * @return
     */
    public boolean isNoPermission() {
        return mStatus == NO_PERMISSION;
    }

    /**
     * 手势验证是否失败
     *
     * @return
     */
    public boolean isValidateGestureFailed() {
        return mStatus == VALIDATE_GESTURE_FAILED;
    }
}
