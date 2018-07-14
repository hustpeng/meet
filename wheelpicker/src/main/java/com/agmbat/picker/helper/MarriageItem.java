package com.agmbat.picker.helper;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 婚况选择项目
 */
public class MarriageItem extends OptionItem {

    private static Option<MarriageItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static MarriageItem valueOf(int value) {
        MarriageItem[] items = values();
        for (MarriageItem item : items) {
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
    public static Option<MarriageItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker/marriage.json");
            Type jsonType = new TypeToken<Option<MarriageItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static MarriageItem[] values() {
        return getOption().mItems;
    }

}
