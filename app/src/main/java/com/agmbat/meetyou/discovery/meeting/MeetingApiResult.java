package com.agmbat.meetyou.discovery.meeting;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.meetyou.coins.CoinsApi;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingApiResult extends ApiResult<List<MeetingItem>> implements PageData<MeetingItem> {

    /**
     * 余额
     */
    @SerializedName("balance")
    public long mBalance;

    /**
     * 总记录数
     */
    @SerializedName("records")
    public int mRecords;

    /**
     * 当前page num
     */
    public int mPageNum;

    @Override
    public boolean isSuccess() {
        return mResult;
    }

    @Override
    public List<MeetingItem> getDataList() {
        return mData;
    }

    @Override
    public int getPageNum() {
        return mPageNum;
    }

    @Override
    public boolean hasNextPageData() {
        return PageDataLoader.hasNextPage(mRecords, CoinsApi.PAGE_SIZE, mPageNum);
    }
}
