package com.agmbat.meetyou.coins;

import com.google.gson.annotations.SerializedName;

/**
 * 每一条记录
 */
public class CoinsRecords {

    @SerializedName("summary")
    public String summary;

    @SerializedName("coins")
    public long coins;

    @SerializedName("creation")
    public long time;

    /**
     * 获取毫秒数
     *
     * @return
     */
    public long getTimeInMillis() {
        return time;
    }

}
