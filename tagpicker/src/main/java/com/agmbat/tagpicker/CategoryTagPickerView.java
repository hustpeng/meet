package com.agmbat.tagpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.agmbat.android.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择控件
 */
public class CategoryTagPickerView extends ScrollView {

    private LinearLayout mTagContent;

    private List<CategoryTag> mList;

    /**
     * 当前选中的List
     */
    private List<String> mCheckedList = new ArrayList<>();

    /**
     * 可选中的最大值
     */
    private int mMaxSelectedCount = Integer.MAX_VALUE;

    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(TagView view, boolean checked) {
            String text = view.getText().toString();
            if (checked) {
                if (mCheckedList.size() == mMaxSelectedCount) {
                    view.setChecked(false);
                    String msg = "最多选择" + mMaxSelectedCount + "项";
                    ToastUtil.showToastLong(msg);
                    return;
                }
                if (!mCheckedList.contains(text)) {
                    mCheckedList.add(text);
                }
            } else {
                mCheckedList.remove(text);
            }
        }
    };

    public CategoryTagPickerView(Context context) {
        super(context);
        init(context);
    }

    public CategoryTagPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mTagContent = new LinearLayout(context);
        mTagContent.setOrientation(LinearLayout.VERTICAL);
        addView(mTagContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
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
     * 更新view显示
     */
    public void update() {
        mTagContent.removeAllViews();
        if (mList == null) {
            return;
        }
        for (CategoryTag categoryTag : mList) {
            TagCategoryView view = new TagCategoryView(getContext());
            view.setOnCheckedChangeListener(mOnCheckedChangeListener);
            view.setCategoryTag(categoryTag);
            view.setCheckedList(mCheckedList);
            mTagContent.addView(view);
        }
    }

    public List<String> getCheckedList() {
        return mCheckedList;
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
