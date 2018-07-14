package com.agmbat.picker.helper;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 性别item
 */
public class GenderItem extends OptionItem {

    private static Option<GenderItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static GenderItem valueOf(int value) {
        GenderItem[] items = values();
        for (GenderItem item : items) {
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
    public static Option<GenderItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker/gender.json");
            Type jsonType = new TypeToken<Option<GenderItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static GenderItem[] values() {
        return getOption().mItems;
    }
}
