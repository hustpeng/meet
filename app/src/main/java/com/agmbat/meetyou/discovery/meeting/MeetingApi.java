package com.agmbat.meetyou.discovery.meeting;

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
public class MeetingApi {

    private static final boolean ENABLE_MOCK = false;

    /**
     * 每页数据20
     */
    public static final int PAGE_SIZE = 20;

    /**
     * 获取聚会活动列表
     * GET
     * https://{DOMAIN}/egret/v1/discovery/list.api?uid=<uid>&ticket=<ticket>&city=<city>&pageindex=<pageindex>&sign=<sign>
     * <p>
     * 分页返回指定城市或全部城市的聚会活动列表。
     * 如果uid和ticket不匹配，返回403错误码。
     * 返回内容如下：
     * <p>
     * {
     * "result": true,      // true API调用成功，否则调用失败
     * "records": 2,       // 匹配的总记录数
     * "resp": [
     * {
     * "city": "东莞;深圳",
     * "enable_signup": false,  //true 正在报名中
     * "id": 4,
     * "title": "6.6 莞深单身教师相亲会",
     * "url": "http://www.dglove.com/console/public-events-detail.jsp?id=4"
     * }
     * ]
     * }
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param city      指定城市或全部城市
     * @param pageIndex 分页获取，第几页。第一页是0，第二页是1，…如果不提供，默认为0
     * @return
     */
    public static MeetingApiResult getMeetingList(String uid, String ticket, String city, int pageIndex) {
        String apiName = "eventslist";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("city", city);
        builder.urlParam("pageIndex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid));  // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/meeting_list.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<MeetingApiResult>() {
        }.getType();
        MeetingApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

}
