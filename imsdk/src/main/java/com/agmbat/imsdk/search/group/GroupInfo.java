package com.agmbat.imsdk.search.group;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 群信息
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
 */
public class GroupInfo implements Serializable {

    /**
     * 群头像
     */
    @SerializedName("cover")
    public String cover;

    /**
     * 分类名称
     */
    @SerializedName("category_name")
    public String categoryName;

    /**
     * 分类id
     */
    @SerializedName("category_id")
    public int categoryId;

    /**
     * 群主昵称
     */
    @SerializedName("owner_name")
    public String ownerName;

    /**
     * 群主Id
     */
    @SerializedName("owner_jid")
    public String ownerJid;

    /**
     * 群描述
     */
    @SerializedName("description")
    public String description;

    /**
     * 群id
     */
    @SerializedName("im_uid")
    public int imUid;

    /**
     * 群jid
     */
    @SerializedName("jid")
    public String jid;

    /**
     * 群名称
     */
    @SerializedName("name")
    public String name;

    /**
     * 是否热门
     */
    @SerializedName("is_hot")
    public int isHot;

    /**
     * 群成员数量
     */
    @SerializedName("member_num")
    public int memberNum;

    public boolean isGroupMember;


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
