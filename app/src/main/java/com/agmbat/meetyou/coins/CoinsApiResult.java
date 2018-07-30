package com.agmbat.meetyou.coins;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.roster.AuthStatus;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * //    {
 * //        "result":true, //true  API调用成功，否则调用失败
 * //            "balance":300,     //余额
 * //            "records": 3,      //总记录数
 * //            "resp":
 * //      [
 * //        {
 * //            "summary":"邀请好友",
 * //                "coins"：100,
 * //                "time": 1517884200
 * //        }
 * //                     …
 * //        {
 * //            "summary":"报名成功",
 * //                "coins"：100,
 * //                "time": 1517459400
 * //        }
 * //       ]
 * //    }
 */
public class CoinsApiResult extends ApiResult<List<CoinsRecords>> implements PageData<CoinsRecords>, AuthStatus {

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

    /**
     * 用户认证状态，详见AuthStatus
     */
    @SerializedName("auth")
    public int mAuthStatus;

    /**
     * 会员等级（范围从0-5）
     */
    @SerializedName("grade")
    public int mGrade;

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
