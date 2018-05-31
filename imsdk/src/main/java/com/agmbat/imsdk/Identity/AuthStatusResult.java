package com.agmbat.imsdk.Identity;

import com.agmbat.imsdk.api.ApiResult;
import com.google.gson.annotations.SerializedName;

/**
 * 查询验证状态结果
 */
public class AuthStatusResult extends ApiResult {

    @SerializedName("auth")
    public Auth mAuth;


}
