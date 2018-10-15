package com.agmbat.picker;

import android.content.Context;
import android.text.TextUtils;

/**
 * 数字选择器
 */
public class NumberPicker extends SinglePicker<Number> {

    /**
     * 数字中需要显示的单位文本
     */
    private String mUnit;

    public NumberPicker(Context context) {
        super(context, new Number[]{});
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    @Override
    protected String formatToString(Number item) {
        String text = super.formatToString(item);
        if (!TextUtils.isEmpty(mUnit)) {
            text += mUnit;
        }
        return text;
    }

    /**
     * 设置数字范围，递增量为1
     */
    public void setRange(int startNumber, int endNumber) {
        setRange(startNumber, endNumber, 1);
    }

    /**
     * 设置数字范围及递增量
     */
    public void setRange(int startNumber, int endNumber, int step) {
        for (int i = startNumber; i <= endNumber; i = i + step) {
            addItem(i);
        }
    }

    /**
     * 设置数字范围及递增量
     */
    public void setRange(double startNumber, double endNumber, double step) {
        for (double i = startNumber; i <= endNumber; i = i + step) {
            addItem(i);
        }
    }

    /**
     * 设置默认选中的数字
     */
    public void setSelectedItem(int number) {
        super.setSelectedItem(number);
    }

    /**
     * 设置默认选中的数字
     */
    public void setSelectedItem(double number) {
        super.setSelectedItem(number);
    }

    public void setOnNumberPickListener(OnNumberPickListener listener) {
        setOnItemPickListener(listener);
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        super.setOnWheelListener(onWheelListener);
    }

    public interface OnWheelListener extends SinglePicker.OnWheelListener<Number> {

    }

    public static abstract class OnNumberPickListener implements OnItemPickListener<Number> {

        public abstract void onNumberPicked(int index, Number item);

        @Override
        public final void onItemPicked(int index, Number item) {
            onNumberPicked(index, item);
        }

    }

}

