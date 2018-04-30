package com.agmbat.meetyou.discovery;


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
public class DiscoveryApi {

    private static final boolean ENABLE_MOCK = false;

    /**
     * 每页数据20
     */
    public static final int PAGE_SIZE = 20;

    /**
     * 附近的人(Nearby Users)
     * GET
     * https://{DOMAIN}/egret/v1/discovery/nearby.api?uid=<phone>&ticket=<ticket>&center=<latitude,longtitude>&gender=<>&pageindex=<>&sign=<>
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
     * ]
     * }
     *
     * @param uid       用户11位手机号码，不含区号
     * @param ticket    The auth ticket
     * @param center    搜索的中心点，一般是当前用户的地理位置，格式为<纬度,经度>，例如”30.5,111.2”
     * @param gender    用户性别过滤, -1: 搜索全部用户, 0：只搜索女性, 1：只搜索男性.默认为-1
     * @param pageIndex 分页获取，第几页。第一页是1，第二页是2，…如果不提供，默认为0
     * @return
     */
    public static DiscoveryApiResult getNearbyUsers(String uid, String ticket, String center, int gender, int pageIndex) {
        String apiName = "nearby";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("center", center);
        builder.urlParam("gender", String.valueOf(gender));
        // server api是从1开始
        builder.urlParam("pageindex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid)); // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/nearby.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<DiscoveryApiResult>() {
        }.getType();
        DiscoveryApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

    /**
     * GET
     * https://{DOMAIN}/egret/v1/discovery/nearby.api?uid=<phone>&ticket=<ticket>&center=<latitude,longtitude>&gender=<>&age=<start,end>&pageindex=<>&sign=<>
     * <p>
     * 返回内容如下：
     * {
     * "result":true, //true  API调用成功，否则调用失败，下面有错误原因
     * "error_reason":"……",//错误码
     * "count":150, //总共有多少匹配结果。注意，这是总数，也即所有的分页结果加起来，不是当页的结果数
     * "pages":15, //总共有多少页
     * "resp"://接下来是搜索结果
     * [
     * {
     * creation: 1523202953000,
     * auth_status: 0, //实名认证状态，1 已认证，0未认证，2 认证未通过
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
     * ]
     * }
     *
     * @param uid       phone
     * @param ticket    The auth ticket
     * @param center    搜索的中心点，一般是当前用户的地理位置，格式为<纬度,经度>，例如”30.5,111.2”
     * @param gender    用户性别过滤。按需求文档，若用户本身为男性，则要搜索女性，即传gender=0，反之则传gender=1
     *                  0：只搜索女性
     *                  1：只搜索男性
     * @param age       搜索年龄范围,例如27,35 按需求文档要求，若用户本身为男，年龄25岁，则搜索比他小的女性，即传age=18,25; 反之若用户本身为女，年龄25岁，则搜索比她大的男性，即传age=25,80
     * @param pageIndex 分页获取，第几页。第一页是0，第二页是1, 如果不提供，默认为0。
     * @return
     */
    public static DiscoveryApiResult getLover(String uid, String ticket, String center, int gender, String age, int pageIndex) {
        String apiName = "nearby";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("center", center);
        builder.urlParam("gender", String.valueOf(gender));
        builder.urlParam("age", age);
        builder.urlParam("pageindex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid)); // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/nearby.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<DiscoveryApiResult>() {
        }.getType();
        DiscoveryApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

    /**
     * 找玩伴
     * GET
     * https://{DOMAIN}/egret/v1/discovery/nearby.api?uid=<>&ticket=<ticket>&center=<latitude,longtitude>&hobby=<hobby>&pageindex=<>&sign=<sign>
     * <p>
     * 返回结果优先按照distance排序
     * 返回的列表将会排除用户自己。
     * 返回内容如下：
     * {
     * "result":true, //true  API调用成功，否则调用失败，下面有错误原因
     * "error_reason":"……",//错误码
     * "count":150, //总共有多少匹配结果。注意，这是总数，也即所有的分页结果加起来，不是当页的结果数
     * "pages":15, //总共有多少页
     * "resp"://接下来是搜索结果
     * [
     * {
     * creation: 1523202953000,
     * auth_status: 0, //实名认证状态，1 已认证，0未认证，2 认证未通过
     * avatar_url: "http://p6bt95t1n.bkt.clouddn.com/9974f25e1f43ff01fa3a95dc02032d2179963.jpg",
     * geo: "30.700001,111.300003",
     * nickname: "好名",
     * last_logout: 1524715815000,
     * age: 26,
     * gender: 1,
     * jid: "13437122759@yuan520.com",
     * last_login: 1524712937000,
     * dist: 24.21131081069728
     * <p>
     * },
     * {
     * },
     * ]
     * }
     * 找玩伴
     *
     * @param uid       phone
     * @param ticket    The auth ticket
     * @param center    搜索的中心点，一般是当前用户的地理位置，格式为<纬度,经度>，例如”30.5,111.2”
     * @param hobby     兴趣关键字,例如”羽毛球”, 每次只能搜索一个兴趣关键字
     * @param pageIndex 分页获取，第几页。zero based，第一页是0，第二页是1,如果不提供，默认为0。
     * @return
     */
    public static DiscoveryApiResult getHobby(String uid, String ticket, String center, String hobby, int pageIndex) {
        String apiName = "nearby";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("center", center);
        builder.urlParam("hobby", hobby);
        builder.urlParam("pageindex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid)); // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/nearby.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<DiscoveryApiResult>() {
        }.getType();
        DiscoveryApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

    /**
     * GET
     * https://{DOMAIN}/egret/v1/discovery/nearby.api?uid=<>&ticket=<ticket>&center=<latitude,longtitude>&birthplace =<>&pageindex=<>&sign=<sign>
     * <p>
     * 返回内容如下：
     * {
     * "result":true, //true  API调用成功，否则调用失败，下面有错误原因
     * "error_reason":"……",//错误码
     * "count":150, //总共有多少匹配结果。注意，这是总数，也即所有的分页结果加起来，不是当页的结果数
     * "pages":15, //总共有多少页
     * "resp"://接下来是搜索结果
     * [
     * {
     * creation: 1523202953000,
     * auth_status: 0, //实名认证状态，1 已认证，0未认证，2 认证未通过
     * avatar_url: "http://p6bt95t1n.bkt.clouddn.com/9974f25e1f43ff01fa3a95dc02032d2179963.jpg",
     * geo: "30.700001,111.300003",
     * nickname: "好名",
     * last_logout: 1524715815000,
     * age: 26,
     * gender: 1,
     * jid: "13437122759@yuan520.com",
     * last_login: 1524712937000,
     * dist: 24.21131081069728
     * }
     * ]
     * }
     * 找老乡
     *
     * @param uid        phone
     * @param ticket     The auth ticket
     * @param center     搜索的中心点，一般是当前用户的地理位置，格式为<纬度,经度>，例如”30.5,111.2”
     * @param birthplace 籍贯。例如”””湖北省武汉市”
     * @param pageIndex  分页获取，第几页。zero based，第一页是0，第二页是1,如果不提供，默认为0。
     * @return
     */
    public static DiscoveryApiResult getBirthplace(String uid, String ticket, String center,
                                                   String birthplace, int pageIndex) {
        String apiName = "nearby";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("center", center);
        builder.urlParam("birthplace", birthplace);
        builder.urlParam("pageindex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid)); // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/nearby.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<DiscoveryApiResult>() {
        }.getType();
        DiscoveryApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }

    /**
     * GET
     * https://{DOMAIN}/egret/v1/discovery/nearby.api?uid=<uid>&ticket=<ticket>&center=<latitude,longtitude>&gender=<gender>&age=<start,end>&height=<start,end>&marriage=<marriage>&birthplace=<>&workarea=<>&education=<>&career=<>&wage=<>&house=<>&car=<>&pageindex=<pageindex>&sign=<sign>
     * <p>
     * 返回内容如下：
     * {
     * "result":true, //true  API调用成功，否则调用失败，下面有错误原因
     * "error_reason":"……",//错误码
     * "count":150, //总共有多少匹配结果。注意，这是总数，也即所有的分页结果加起来，不是当页的结果数
     * "pages":15, //总共有多少页
     * "resp"://接下来是搜索结果
     * [
     * {
     * creation: 1523202953000,
     * auth_status: 0, //实名认证状态，1 已认证，0未认证，2 认证未通过
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
     * 综合搜索会员
     *
     * @param uid        phone
     * @param ticket     The auth ticket
     * @param center     搜索的中心点，一般是当前用户的地理位置，格式为<纬度,经度>，例如”30.5,111.2”
     * @param gender     搜索性别， 0为女，1为男，-1表示不限
     * @param age        搜索年龄范围,例如"27,35"
     * @param height     搜索身高范围，例如165,185
     * @param marriage   单身0，非单身1，-1表示不限
     * @param birthplace 籍贯，为空或”””””表示不限
     * @param workarea   工作地区，为空或””,””表示不限
     * @param education  学历，传对应索引值，-1表示不限
     * @param career     职业，为空或”””””表示不限
     * @param wage       薪水，表示XXX以上
     * @param house      住房，无自住房0，有自住房1，-1表示不限
     * @param car        购车，无车0，有车1，-1表示不限
     * @param pageIndex  分页获取，第几页。第一页是0，第二页是1, 如果不提供，默认为0。
     * @return
     */
    public static DiscoveryApiResult search(String uid, String ticket, String center, int gender,
                                            String age, String height, int marriage,
                                            String birthplace, String workarea, int education,
                                            String career, int wage, int house, int car,
                                            int pageIndex) {
        String apiName = "nearby";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("center", center);
        builder.urlParam("gender", String.valueOf(gender));
        builder.urlParam("age", age);
        builder.urlParam("height", height);
        builder.urlParam("marriage", String.valueOf(marriage));
        builder.urlParam("birthplace", birthplace);
        builder.urlParam("workarea", workarea);
        builder.urlParam("education", String.valueOf(education));
        builder.urlParam("career", career);
        builder.urlParam("wage", String.valueOf(wage));
        builder.urlParam("house", String.valueOf(house));
        builder.urlParam("car", String.valueOf(car));
        builder.urlParam("pageindex", String.valueOf(pageIndex));
        builder.urlParam("sign", Api.getSign(apiName, uid)); // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (ENABLE_MOCK) {
            text = AppResources.readAssetFile("apimock/nearby.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<DiscoveryApiResult>() {
        }.getType();
        DiscoveryApiResult apiResult = GsonHelper.fromJson(text, jsonType);
        apiResult.mPageNum = pageIndex;
        return apiResult;
    }
}
