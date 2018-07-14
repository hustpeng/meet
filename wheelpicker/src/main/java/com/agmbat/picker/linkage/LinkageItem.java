package com.agmbat.picker.linkage;

import com.agmbat.picker.wheelview.WheelItem;

/**
 * 用于联动选择器展示的条目
 *
 * @see LinkagePicker
 */
public interface LinkageItem extends WheelItem {

    /**
     * 唯一标识，用于判断两个条目是否相同
     */
    Object getId();

}
