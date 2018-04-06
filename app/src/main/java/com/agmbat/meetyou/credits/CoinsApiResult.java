package com.agmbat.meetyou.credits;

import com.agmbat.imsdk.api.ApiResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoinsApiResult extends ApiResult<List<CoinsRecords>> {

    /**
     * 余额
     */
    @SerializedName("balance")
    public long balance;

    /**
     * 总记录数
     */
    @SerializedName("records")
    public int records;

}
