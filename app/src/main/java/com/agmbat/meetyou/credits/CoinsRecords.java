package com.agmbat.meetyou.credits;

import com.google.gson.annotations.SerializedName;

/**
 * 第一条记录
 */
public class CoinsRecords {

    @SerializedName("summary")
    public String summary;

    @SerializedName("coins")
    public long coins;

    @SerializedName("time")
    public long time;

}
