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
public class HouseFilterItem extends OptionItem {

    private static Option<HouseFilterItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static HouseFilterItem valueOf(int value) {
        HouseFilterItem[] items = values();
        for (HouseFilterItem item : items) {
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
    public static Option<HouseFilterItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/house.json");
            Type jsonType = new TypeToken<Option<HouseFilterItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static HouseFilterItem[] values() {
        return getOption().mItems;
    }

}
