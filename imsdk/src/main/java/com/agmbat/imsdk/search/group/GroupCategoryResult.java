package com.agmbat.imsdk.search.group;

import com.agmbat.imsdk.api.ApiResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * * {
 * "result": true,
 * "records": 13,
 * "resp": [
 * {
 * "id": 1,
 * "name": "单身"
 * },
 * {
 * "id": 2,
 * "name": "已婚"
 * },
 * {
 * "id": 3,
 * "name": "兴趣"
 * },
 * {
 * "id": 4,
 * "name": "娱乐"
 * },
 * {
 * "id": 5,
 * "name": "职场"
 * },
 * {
 * "id": 6,
 * "name": "商务"
 * },
 * {
 * "id": 7,
 * "name": "学习"
 * },
 * {
 * "id": 8,
 * "name": "生活"
 * },
 * {
 * "id": 9,
 * "name": "同城"
 * },
 * {
 * "id": 10,
 * "name": "同乡"
 * },
 * {
 * "id": 11,
 * "name": "同学"
 * },
 * {
 * "id": 12,
 * "name": "战友"
 * },
 * {
 * "id": 13,
 * "name": "其它"
 * }
 * ]
 * }
 */
public class GroupCategoryResult extends ApiResult<List<GroupCategory>> {

    @SerializedName("records")
    public int records;
}
