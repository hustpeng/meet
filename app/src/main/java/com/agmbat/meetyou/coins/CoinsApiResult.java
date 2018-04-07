package com.agmbat.meetyou.coins;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoinsApiResult extends ApiResult<List<CoinsRecords>> implements PageData<CoinsRecords> {

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
    public List<CoinsRecords> getDataList() {
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
