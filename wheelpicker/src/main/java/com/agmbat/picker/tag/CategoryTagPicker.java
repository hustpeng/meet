package com.agmbat.picker.tag;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.picker.R;
import com.agmbat.tagpicker.CategoryTag;
import com.agmbat.tagpicker.CategoryTagPickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 仿支宝
 */
public class CategoryTagPicker extends Dialog {

    private CategoryTagPickerView mPickerView;

    private List<CategoryTag> mList;

    private List<String> mCheckedList = new ArrayList<>();

    private int mMaxSelectedCount = Integer.MAX_VALUE;

    public CategoryTagPicker(@NonNull Context context) {
        super(context, android.R.style.Theme_Light_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.picker_category_tag);
        mPickerView = (CategoryTagPickerView) findViewById(R.id.picker);
        mPickerView.setCategoryTagList(mList);
        mPickerView.setMaxSelectCount(mMaxSelectedCount);
        mPickerView.setCheckedList(mCheckedList);
        mPickerView.update();
    }

    public void setCategoryTagList(List<CategoryTag> list) {
        mList = list;
    }

    public void setMaxSelectCount(int count) {
        mMaxSelectedCount = count;
        if (mCheckedList.size() > count) {
            mCheckedList.clear();
        }
    }

    /**
     * 设置选中的item项
     *
     * @param list
     */
    public void setCheckedList(List<String> list) {
        if (list == null) {
            return;
        }
        if (list.size() > mMaxSelectedCount) {
            return;
        }
        mCheckedList.clear();
        mCheckedList.addAll(list);
    }
}
