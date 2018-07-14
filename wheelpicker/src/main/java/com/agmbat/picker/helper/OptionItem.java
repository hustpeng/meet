package com.agmbat.picker.helper;

import com.google.gson.annotations.SerializedName;

/**
 * 选项
 */
public class OptionItem {

    @SerializedName("name")
    public String mName;

    @SerializedName("value")
    public int mValue;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return mName.equals(obj);
        }
        if (obj instanceof OptionItem) {
            OptionItem item = (OptionItem) obj;
            return mValue == item.mValue;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return mName;
    }
}
