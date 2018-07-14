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
public class CarFilterItem extends OptionItem {

    private static Option<CarFilterItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static CarFilterItem valueOf(int value) {
        CarFilterItem[] items = values();
        for (CarFilterItem item : items) {
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
    public static Option<CarFilterItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/car.json");
            Type jsonType = new TypeToken<Option<CarFilterItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static CarFilterItem[] values() {
        return getOption().mItems;
    }

}
