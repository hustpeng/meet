package com.agmbat.imsdk.Identity;

import com.agmbat.imsdk.api.ApiResult;
import com.google.gson.annotations.SerializedName;

/**
 * 查询验证状态结果
 */
public class AuthStatusResult extends ApiResult {

    @SerializedName("auth")
    public Auth mAuth;

    public static class Auth {

        @SerializedName("name")
        public String mName;

        @SerializedName("identity")
        public String identity;

        @SerializedName("photo_front")
        public String photo_front;

        @SerializedName("photo_back")
        public String photo_back;

        /**
         * 审核状态：0 待审核，1通过， 2未通过
         */
        @SerializedName("status")
        public int status;

        /**
         * 如status值为2，opinion则为未通过的原因
         */
        @SerializedName("opinion")
        public String opinion;

        @SerializedName("creation_time")
        public long creation_time;

        @SerializedName("last_modified_time")
        public long last_modified_time;

    }
}
