package com.agmbat.picker.helper.filter;

import com.agmbat.android.AppResources;
import com.agmbat.picker.helper.Option;
import com.agmbat.picker.helper.OptionItem;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 性别item, 用于搜索
 */
public class GenderFilterItem extends OptionItem {

    private static Option<GenderFilterItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static GenderFilterItem valueOf(int value) {
        GenderFilterItem[] items = values();
        for (GenderFilterItem item : items) {
            if (item.mValue == value) {
                return item;
            }
        }
        return null;
    }


    /**
     * 获取Option选项
     *
     * @return
     */
    public static Option<GenderFilterItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/gender.json");
            Type jsonType = new TypeToken<Option<GenderFilterItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static GenderFilterItem[] values() {
        return getOption().mItems;
    }
}
