package com.agmbat.tagpicker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryTag {

    /**
     * 类型名称
     */
    @SerializedName("category")
    public String mCategory;

    @SerializedName("option")
    public List<String> mTagList;

    /**
     * 是否包含标签
     *
     * @param tag
     * @return
     */
    public boolean contains(String tag) {
        if (mTagList != null) {
            return mTagList.contains(tag);
        }
        return false;
    }
}
