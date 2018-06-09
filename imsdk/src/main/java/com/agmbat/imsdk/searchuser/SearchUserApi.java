package com.agmbat.imsdk.searchuser;

import com.agmbat.imsdk.api.Api;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.net.HttpRequester;
import com.agmbat.server.GsonHelper;
import com.agmbat.text.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SearchUserApi {

    /**
     * 查找指定用户（通过im_uid或手机号查找用户）：
     * GET
     * https://{DOMAIN}/egret/v1/discovery/searchuser.api?uid=<>&ticket=<ticket>&keyword=< >&sign=<sign>
     * <p>
     * 返回内容如下：
     * {
     * "result":true, // true  API调用成功，否则调用失败
     * "resp":  // 若无resp节点，表示未找到该用户
     * {
     * "age": 22,
     * "auth_status": 0,    //1 为认证会员，0为未认证会员
     * "avatar_url": "http://p6bt95t1n.bkt.clouddn.com/9974f25e1f43ff01fa3a95dc02032d2179963.jpg",
     * "birth": 1996,
     * "birthplace": "湖北省宜昌市",
     * "car": 1,
     * "career": "空乘",
     * "create_time": 1521703218000,
     * "demand": "漂亮妹纸",
     * "education": 4,
     * "gender": 0,
     * "height": 169,
     * "hobby": "徒步,自驾游,音乐,书法,西餐",
     * "house": 0,
     * "im_uid": 1061,
     * "industry": "服务业",
     * "introduce": "一个好人",
     * "jid": "15571767415@yuan520.com",
     * "last_login": 1521703240000,
     * "marriage": 0,
     * "nickname": "好名字",
     * "residence": "湖北省宜昌市",
     * "status": "好人一枚",
     * "wage": 20000,
     * "weight": 52,
     * "workarea": "湖北省宜昌市"
     * }
     * }
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

}
