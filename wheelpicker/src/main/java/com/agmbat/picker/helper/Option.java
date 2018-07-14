package com.agmbat.picker.helper;

import com.agmbat.server.GsonHelper;
import com.agmbat.utils.ArrayUtils;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 选择项
 *
 * @param <T>
 */
public class Option<T> {

    @SerializedName("default")
    public T mDefault;

    @SerializedName("option")
    public T[] mItems;

    private int defaultIndex() {
        int index = ArrayUtils.indexOf(mItems, mDefault);
        if (index < 0) {
            // 如果没找到默认为0
            index = 0;
        }
        return index;
    }

    /**
     * 获取index
     *
     * @param item
     * @return
     */
    public int index(T item) {
        int index = ArrayUtils.indexOf(mItems, item);
        if (index == -1) {
            index = defaultIndex();
        }
        return index;
    }

    /**
     * 通过名字获取index
     *
     * @param name
     * @return
     */
    public int indexName(String name) {
        if (mItems == null) {
            return -1;
        }
        for (int i = 0; i < mItems.length; i++) {
            if (mItems[i].equals(name)) {
                return i;
            }
        }
        return defaultIndex();
    }

    /**
     * 从json中转为对象
     *
     * @param text
     * @param <T>
     * @return
     */
    public static <T> Option<T> fromJson(String text) {
        Type jsonType = new TypeToken<Option<T>>() {
        }.getType();
        return GsonHelper.fromJson(text, jsonType);
    }


}
