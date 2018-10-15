package com.agmbat.picker.carnumber;

import android.content.Context;

import com.agmbat.picker.linkage.LinkagePicker;

/**
 * 车牌号码选择器。数据参见http://www.360doc.com/content/12/0602/07/3899427_215339300.shtml
 */
public class CarNumberPicker extends LinkagePicker<CarNumberProvince, CarNumberCity, Void> {

    public CarNumberPicker(Context context) {
        super(context, new CarNumberDataProvider());
    }


}
