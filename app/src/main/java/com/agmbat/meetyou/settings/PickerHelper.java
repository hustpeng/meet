package com.agmbat.meetyou.settings;


import android.app.Activity;
import android.view.Gravity;

import com.agmbat.picker.wheel.picker.NumberPicker;

/**
 * 选择器辅助类
 */
public class PickerHelper {

    /**
     * 显示选择年的Picker
     */
    public static void showYearPicker(Activity activity, int selectedYear, NumberPicker.OnNumberPickListener l) {
        int startYear = 1970;
        int endYear = 2018;

        NumberPicker picker = new NumberPicker(activity);
        picker.setWidth(picker.getScreenWidthPixels() / 2);
        picker.setCycleDisable(true);
        picker.setDividerVisible(false);
        picker.setOffset(2); // 偏移量
        picker.setGravity(Gravity.CENTER);
        picker.setRange(startYear, endYear, 1); // 数字范围
        picker.setSelectedItem(selectedYear);
        picker.setLabel("年");
        picker.setOnNumberPickListener(l);
        picker.show();
    }

    /**
     * 显示选择身高的Picker
     */
    public static void showHeightPicker(Activity activity, int selected, NumberPicker.OnNumberPickListener l) {
        int start = 100;
        int end = 200;

        NumberPicker picker = new NumberPicker(activity);
        picker.setWidth(picker.getScreenWidthPixels() / 2);
        picker.setCycleDisable(true);
        picker.setDividerVisible(false);
        picker.setOffset(2); // 偏移量
        picker.setGravity(Gravity.CENTER);
        picker.setRange(start, end, 1); // 数字范围
        picker.setSelectedItem(selected);
        picker.setLabel("厘米");
        picker.setOnNumberPickListener(l);
        picker.show();
    }
}
