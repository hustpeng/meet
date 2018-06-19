package com.agmbat.imsdk.search;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.search.group.GroupCategoryResult;
import com.agmbat.imsdk.search.group.SearchGroupResult;
import com.agmbat.imsdk.search.user.SearchUserResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 搜索相关api
 */
public class SearchApi {

    private static final boolean MOCK_API = false;

    /**
     * 查找指定用户（通过im_uid或手机号查找用户）：
     * GET
     * https://{DOMAIN}/egret/v1/discovery/searchuser.api?uid=<>&ticket=<ticket>&keyword=< >&sign=<sign>
     * <p>
     * 返回内容如SearchUserResult描述：
     *
     * @param uid     用户11位手机号码，不含区号
     * @param ticket  The auth ticket
     * @param keyword 用户的im_uid（例如10061）或手机号（15571767415）
     * @return
     */
    public static SearchUserResult searchUser(String uid, String ticket, String keyword) {
        String apiName = "searchuser";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseDiscoveryUrl(apiName));
        builder.method("GET");
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("keyword", keyword);
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<SearchUserResult>() {
        }.getType();
        SearchUserResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }


    /**
     * 查找指定群：
     * GET
     * https://{DOMAIN}/egret/v1/discovery/searchcircle.api?uid=<>&ticket=<ticket>&im_uid=<>&keyword=<>&sign=<sign>
     * <p>
     * keyword	No	String	群名，但im_uid与keyword不能同时为空
     *
     * @param uid    用户11位手机号码，不含区号
     * @param ticket The auth ticket
     * @param imUid  群ID，例如1002
     * @return
     */
    public static SearchGroupResult searchGroup(String uid, String ticket, String imUid) {
        String apiName = "searchcircle";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseDiscoveryUrl(apiName));
        builder.method("GET");
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("im_uid", imUid);
        builder.urlParam("keyword", "");
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (MOCK_API) {
            text = AppResources.readAssetFile("apimock/searchcircle.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<SearchGroupResult>() {
        }.getType();
        SearchGroupResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

    /**
     * 综合搜索群：
     * GET
     * https://{DOMAIN}/egret/v1/discovery/searchcircle.api?uid=<>&ticket=<ticket>&category_id=<>&keyword=<keyword>&pageindex=<>&sign=<sign>
     *
     * @param uid        用户11位手机号码，不含区号
     * @param ticket     The auth ticket
     * @param categoryId 群分类， 不传该值或传0值，表示所有分类
     * @param keyword    搜索关键字，默认为空，关键字匹配群名
     * @param pageindex  分页获取，第几页。zero based，第一页是0，第二页是1， 如果不提供，默认为0。
     * @return
     */
    public static SearchGroupResult searchGroup(String uid, String ticket, int categoryId, String keyword, int pageindex) {
        String apiName = "searchcircle";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseDiscoveryUrl(apiName));
        builder.method("GET");
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("category_id", String.valueOf(categoryId));
        builder.urlParam("keyword", keyword);
        builder.urlParam("pageindex", String.valueOf(pageindex));
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (MOCK_API) {
            text = AppResources.readAssetFile("apimock/searchcircle.api.json");
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<SearchGroupResult>() {
        }.getType();
        SearchGroupResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }

    /**
     * 查找指定群：
     * 获取所有的群分类：
     * GET
     * https://{DOMAIN}/egret/v1/discovery/circlecategories.api?uid=<>&ticket=<ticket>&sign=<sign>
     *
     * @param uid    用户11位手机号码，不含区号
     * @param ticket The auth ticket
     * @return
     */
    public static GroupCategoryResult getGroupCategory(String uid, String ticket) {
        String apiName = "circlecategories";
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(Api.getBaseDiscoveryUrl(apiName));
        builder.method("GET");
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("sign", Api.getSign(apiName, uid));
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        Type jsonType = new TypeToken<GroupCategoryResult>() {
        }.getType();
        GroupCategoryResult apiResult = GsonHelper.fromJson(text, jsonType);
        return apiResult;
    }


}
