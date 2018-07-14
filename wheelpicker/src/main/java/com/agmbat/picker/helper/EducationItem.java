package com.agmbat.picker.helper;

import com.agmbat.android.AppResources;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 学历选择项目
 */
public class EducationItem extends OptionItem {

    private static Option<EducationItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static EducationItem valueOf(int value) {
        EducationItem[] items = values();
        for (EducationItem item : items) {
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
    public static Option<EducationItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker/education.json");
            Type jsonType = new TypeToken<Option<EducationItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static EducationItem[] values() {
        return getOption().mItems;
    }

}
