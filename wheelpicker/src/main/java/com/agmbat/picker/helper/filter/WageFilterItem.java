package com.agmbat.picker.helper.filter;

import com.agmbat.android.AppResources;
import com.agmbat.picker.helper.Option;
import com.agmbat.picker.helper.OptionItem;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 月薪描述
 */
public class WageFilterItem extends OptionItem {

    private static Option<WageFilterItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static WageFilterItem valueOf(int value) {
        WageFilterItem[] items = values();
        for (WageFilterItem item : items) {
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
    public static Option<WageFilterItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/wage.json");
            Type jsonType = new TypeToken<Option<WageFilterItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static WageFilterItem[] values() {
        return getOption().mItems;
    }


}
