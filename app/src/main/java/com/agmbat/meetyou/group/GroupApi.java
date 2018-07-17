package com.agmbat.meetyou.group;

import com.agmbat.imsdk.api.Api;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupApi {

    /**
     * 获取群组分类
     * @param uid
     * @param ticket
     * @return
     */
    public static List<GroupTag> getAllGroupTags(String uid, String ticket){
        String apiName = "circlecategories";
        String url = Api.getBaseDiscoveryUrl(apiName);
        HttpRequester.Builder builder = new HttpRequester.Builder();
        builder.baseUrl(url);
        builder.urlParam("uid", uid);
        builder.urlParam("ticket", ticket);
        builder.urlParam("sign", Api.getSign(apiName, uid));  // API调用签名
        HttpRequester requester = builder.build();
        String text = requester.requestAsString();
        Type jsonType = new TypeToken<GroupTagPage>() {
        }.getType();
        GroupTagPage groupTagPage = GsonHelper.fromJson(text, jsonType);
        if(null != groupTagPage) {
            return groupTagPage.getDataList();
        }
        return new ArrayList<GroupTag>();
    }
}
