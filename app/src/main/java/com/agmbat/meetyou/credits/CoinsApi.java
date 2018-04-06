package com.agmbat.meetyou.credits;


import com.agmbat.android.AppResources;
import com.agmbat.imsdk.api.Api;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 缘币api
 */
public class CoinsApi {

    private static final boolean ENABLE_MOCK = true;

    /**
     * 我的缘币明细, 每页返回20条记录，按记录发生时间倒序排列
     * <p>
     * GET
     * https://{DOMAIN}/egret/v1/user/coins.api?uid=<phone>&ticket=<ticket>&pageindex=<>&sign=<sign>
     * <p>
     * //    返回内容如下：
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
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param pageIndex 分页获取，第几页。第一页是1，第二页是2，…如果不提供，默认为1
     * @return
     */
    public static CoinsApiResult getCoins(String uid, String ticket, int pageIndex) {
        String apiName = "coins";
        String url = Api.getBaseUserUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("pageIndex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/coins.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<CoinsApiResult>() {
        }.getType();
        CoinsApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

}
