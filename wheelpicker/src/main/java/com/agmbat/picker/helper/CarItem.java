package com.agmbat.picker.helper;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 婚况选择项目
 */
public class CarItem extends OptionItem {

    private static Option<CarItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static CarItem valueOf(int value) {
        CarItem[] items = values();
        for (CarItem item : items) {
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
    public static Option<CarItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker/car.json");
            Type jsonType = new TypeToken<Option<CarItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static CarItem[] values() {
        return getOption().mItems;
    }

}
