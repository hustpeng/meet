package com.agmbat.picker.helper.filter;

import com.agmbat.android.AppResources;
import com.agmbat.picker.helper.Option;
import com.agmbat.picker.helper.OptionItem;
import com.agmbat.server.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 学历选择项目
 */
public class EducationFilterItem extends OptionItem {

    private static Option<EducationFilterItem> sOption = null;

    /**
     * value of
     *
     * @param value
     * @return
     */
    public static EducationFilterItem valueOf(int value) {
        EducationFilterItem[] items = values();
        for (EducationFilterItem item : items) {
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
    public static Option<EducationFilterItem> getOption() {
        if (sOption == null) {
            String text = AppResources.readAssetFile("wheelpicker_filter/education.json");
            Type jsonType = new TypeToken<Option<EducationFilterItem>>() {
            }.getType();
            sOption = GsonHelper.fromJson(text, jsonType);
        }
        return sOption;
    }

    public static EducationFilterItem[] values() {
        return getOption().mItems;
    }

}
