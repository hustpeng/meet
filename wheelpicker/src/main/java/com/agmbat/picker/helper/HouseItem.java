package com.agmbat.picker.helper;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 婚况选择项目
 */
public class HouseItem extends OptionItem {

    private static Option<HouseItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static HouseItem valueOf(int value) {
        HouseItem[] items = values();
        for (HouseItem item : items) {
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
    public static Option<HouseItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker/house.json");
            Type jsonType = new TypeToken<Option<HouseItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static HouseItem[] values() {
        return getOption().mItems;
    }

}
