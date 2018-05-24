package com.agmbat.meetyou.discovery.search;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiscoveryApiResult extends ApiResult<List<ContactInfo>> implements PageData<ContactInfo> {

    /**
     * 每页数据20
     */
    public static final int PAGE_SIZE = 20;

//    "result": true,
//            "count": 4,
//            "pages": 1,
//            "resp": [
//    {
//        "creation": 1523811613000,
//            "auth_status": 0,
//            "avatar_url": "http://p6bt95t1n.bkt.clouddn.com/0d252ea965248e8eaffd37b63fb33d2243520.jpg",
//            "geo": "30.700001,111.300003",
//            "nickname": "接电弧",
//            "last_logout": 1524817815000,
//            "age": 28,
//            "gender": 0,
//            "jid": "15002752759@yuan520.com",
//            "last_login": 1524815658000,
//            "dist": 24.21131081069728
//    },

    /**
     * 错误码
     */
    @SerializedName("error_reason")
    public String mErrorReason;

    /**
     * 总共有多少匹配结果。注意，这是总数，也即所有的分页结果加起来，不是当页的结果数
     */
    @SerializedName("count")
    public int mCount;

    /**
     * "pages":15, //总共有多少页
     */
    @SerializedName("pages")
    public int mPages;

    /**
     * 当前page num
     */
    public int mPageNum;

    @Override
    public boolean isSuccess() {
        return mResult;
    }

    @Override
    public List<ContactInfo> getDataList() {
        return mData;
    }

    @Override
    public int getPageNum() {
        return mPageNum;
    }

    @Override
    public boolean hasNextPageData() {
        return PageDataLoader.hasNextPage(mCount, PAGE_SIZE, mPageNum);
    }
}
