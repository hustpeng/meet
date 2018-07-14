package com.agmbat.picker.helper;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 月薪描述
 */
public class WageItem extends OptionItem {

    private static Option<WageItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static WageItem valueOf(int value) {
        WageItem[] items = values();
        for (WageItem item : items) {
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
    public static Option<WageItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker/wage.json");
            Type jsonType = new TypeToken<Option<WageItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static WageItem[] values() {
        return getOption().mItems;
    }


}
