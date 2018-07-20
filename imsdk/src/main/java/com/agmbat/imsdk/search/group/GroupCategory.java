package com.agmbat.imsdk.search.group;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * {
 * "id": 1,
 * "name": "单身"
 * },
 */
@Table(name = "group_category")
public class GroupCategory {

    /**
     * 此id为数据库存储id值
     */
    @Column(name = "id", isId = true, autoGen = false)
    @SerializedName("id")
    private int id;

    @Column(name = "name")
    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
