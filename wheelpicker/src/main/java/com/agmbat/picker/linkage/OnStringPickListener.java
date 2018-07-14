package com.agmbat.picker.linkage;

/**
 * 数据选择完成监听器
 */
public abstract class OnStringPickListener implements LinkagePicker.OnPickListener<StringLinkageFirst, StringLinkageSecond, String> {

    public abstract void onPicked(String first, String second, String third);

    @Override
    public void onPicked(StringLinkageFirst first, StringLinkageSecond second, String third) {
        onPicked(first.getName(), second.getName(), third);
    }

}
