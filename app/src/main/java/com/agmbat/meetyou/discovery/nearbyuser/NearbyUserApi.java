package com.agmbat.meetyou.discovery.nearbyuser;


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
public class NearbyUserApi {

    private static final boolean ENABLE_MOCK = false;

    /**
     * 每页数据20
     */
    public static final int PAGE_SIZE = 20;

    /**
     * 附近的人(Nearby Users)
     * GET
     * https://{DOMAIN}/egret/v1/discovery/nearby.api?uid=<phone>&ticket=<ticket>&center=<latitude,longtitude>&gender=<>&pageindex=<>&sign=<>
     * <p>
     * 参数名	Required?	格式	意义
     * uid	     Yes	String	phone
     * ticket	 Yes	String	The auth ticket
     * center	 Yes	String	搜索的中心点，一般是当前用户的地理位置，格式为<纬度,经度>，例如”30.5,111.2”
     * gender	 No	    Integer	搜索filter：用户性别过滤
     * pageindex No	    Integer	分页获取，第几页。第一页是0，第二页是1，… 如果不提供，默认为0。
     * sign	     Yes	String	API调用签名
     * <p>
     * <p>
     * <p>
     * -1: 搜索全部用户
     * 0：只搜索女性
     * 1：只搜索男性
     * 默认为-1
     * <p>
     * <p>
     * <p>
     * 返回结果优先按照会员类型排序，然后按distance排序
     * <p>
     * 返回内容如下：
     * <p>
     * {
     * "result":true, // true  API调用成功，否则调用失败，下面有错误原因
     * "error_reason":"……",//错误码
     * "count":150, //总共有多少匹配结果。注意，这是总数，也即所有的分页结果加起来，不是当页的结果数
     * "pages":15, //总共有多少页
     * "resp"://接下来是搜索结果
     * [
     * {
     * creation: 1523202953000,
     * auth_status: 0,
     * avatar_url: "http://p6bt95t1n.bkt.clouddn.com/9974f25e1f43ff01fa3a95dc02032d2179963.jpg",
     * geo: "30.700001,111.300003",
     * nickname: "好名",
     * last_logout: 1524715815000,
     * age: 26,
     * gender: 1,
     * jid: "13437122759@yuan520.com",
     * last_login: 1524712937000,
     * dist: 24.21131081069728
     * },
     * {
     * },
     * ……
     * ]
     * }
     * <p>
     * <p>
     * GET
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param pageIndex 分页获取，第几页。第一页是1，第二页是2，…如果不提供，默认为0
     * @return
     */
    public static NearbyUsersApiResult getNearbyUsers(String uid, String ticket, int pageIndex) {
        String apiName = "nearby";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("center", "30.5,111.2");
        builder.urlParam("gender", "-1");
        // server api是从1开始
        builder.urlParam("pageindex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/nearby.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<NearbyUsersApiResult>() {
        }.getType();
        NearbyUsersApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

}
