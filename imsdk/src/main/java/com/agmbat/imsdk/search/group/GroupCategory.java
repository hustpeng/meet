package com.agmbat.imsdk.search.group;

import com.google.gson.annotations.SerializedName;

/**
 * {
 * "id": 1,
 * "name": "单身"
 * },
 */
public class GroupCategory {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;
}
