package com.agmbat.meetyou.group;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.pagedataloader.PageData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupTagPage extends ApiResult<List<GroupTag>> implements PageData<GroupTag> {

    /**
     * 总记录数
     */
    @SerializedName("records")
    public int records;

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    @Override
    public boolean isSuccess() {
        return mResult;
    }

    @Override
    public List<GroupTag> getDataList() {
        return mData;
    }

    @Override
    public int getPageNum() {
        return 0;
    }

    @Override
    public boolean hasNextPageData() {
        return false;
    }
}
