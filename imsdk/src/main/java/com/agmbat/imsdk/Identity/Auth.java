package com.agmbat.imsdk.Identity;

import com.google.gson.annotations.SerializedName;

public class Auth {
    /**
     * 未申请认证
     */
    public static final int STATUS_NONE = -1;

    /**
     * 待审核
     */
    public static final int STATUS_PENDING_TRIAL = 0;

    /**
     * 通过
     */
    public static final int STATUS_PASS = 1;

    /**
     * 未通过
     */
    public static final int STATUS_FAIL = 2;


    @SerializedName("name")
    public String mName;

    @SerializedName("identity")
    public String mIdentity;

    @SerializedName("photo_front")
    public String mPhotoFront;

    @SerializedName("photo_back")
    public String mPhotoBack;

    /**
     * 审核状态：0 待审核，1通过， 2未通过
     */
    @SerializedName("status")
    public int mStatus;

    /**
     * 如status值为2，opinion则为未通过的原因
     */
    @SerializedName("opinion")
    public String mOpinion;

    @SerializedName("creation_time")
    public long mCreationTime;

    @SerializedName("last_modified_time")
    public long mLastModifiedTime;

    /**
     * 是否可以申请认证
     *
     * @return
     */
    public boolean hasNeedAuth() {
        return mStatus == Auth.STATUS_NONE || mStatus == Auth.STATUS_FAIL || mStatus == Auth.STATUS_PENDING_TRIAL;
    }

}