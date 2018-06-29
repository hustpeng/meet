package com.agmbat.meetyou.coins;

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

    private static final boolean ENABLE_MOCK = false;

    /**
     * 每页数据20
     */
    public static final int PAGE_SIZE = 20;

    /**
     * 我的缘币明细, 每页返回20条记录，按记录发生时间倒序排列
     * <p>
     * GET
     * https://{DOMAIN}/egret/v1/user/coins.api?uid=<phone>&ticket=<ticket>&pageindex=<>&sign=<sign>
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param pageIndex 分页获取，第几页。第一页是0，第二页是1，…如果不提供，默认为0
     * @return
     */
    public static CoinsApiResult getCoins(String uid, String ticket, int pageIndex) {
        String apiName = "coins";
        String url = Api.getBaseUserUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        // server api是从1开始
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
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

}
