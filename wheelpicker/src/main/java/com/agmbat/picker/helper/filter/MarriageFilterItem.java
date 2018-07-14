package com.agmbat.picker.helper.filter;

import com.agmbat.android.AppResources;
import com.agmbat.picker.helper.Option;
import com.agmbat.picker.helper.OptionItem;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 婚况选择项目
 */
public class MarriageFilterItem extends OptionItem {

    private static Option<MarriageFilterItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static MarriageFilterItem valueOf(int value) {
        MarriageFilterItem[] items = values();
        for (MarriageFilterItem item : items) {
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
    public static Option<MarriageFilterItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/marriage.json");
            Type jsonType = new TypeToken<Option<MarriageFilterItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static MarriageFilterItem[] values() {
        return getOption().mItems;
    }

}
