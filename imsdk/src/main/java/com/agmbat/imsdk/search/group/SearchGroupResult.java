package com.agmbat.imsdk.search.group;

import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.search.group.GroupInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * {
 * "result": true,
 * "count": 1,
 * "pages": 1,
 * "resp": [
 * {
 * "cover": "",
 * "category_name": "娱乐",
 * "owner_name": "特工007",
 * "description": "",
 * "im_uid": 1002,
 * "name": "android技术交流",
 * "is_hot": 0,
 * "category_id": 4,
 * "owner_jid": "16671001488@yuan520.com",
 * "jid": "1002@circle.yuan520.com",
 * "member_num": 1
 * }
 * ]
 * }
 */
public class SearchGroupResult extends ApiResult<List<GroupInfo>> {

    @SerializedName("pages")
    public int pages;

    @SerializedName("count")
    public int count;
}
