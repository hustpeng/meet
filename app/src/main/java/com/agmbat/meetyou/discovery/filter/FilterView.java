package com.agmbat.meetyou.discovery.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.picker.SinglePicker;
import com.agmbat.picker.helper.filter.FilterHelper;
import com.agmbat.picker.helper.filter.GenderFilterItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 显示过虑器的view
 */
public class FilterView extends FrameLayout {

    /**
     * 过虑器内容变化监听器
     */
    private OnFilterInfoChangeListener mListener;

    private FilterInfo mFilterInfo;


    /**
     * 性别
     */
    @BindView(R.id.input_gender)
    TextView mGenderView;

    public FilterView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FilterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnFilterInfoChangeListener(OnFilterInfoChangeListener l) {
        mListener = l;
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_filter, this);
        ButterKnife.bind(this, this);
        mFilterInfo = new FilterInfo();
        mGenderView.setText(GenderFilterItem.valueOf(mFilterInfo.getGender()).mName);
    }

    public FilterInfo getFilterInfo() {
        return mFilterInfo;
    }

    /**
     * 选择性别
     */
    @OnClick(R.id.btn_gender)
    void onClickGender() {
        int value = mFilterInfo.getGender();
        GenderFilterItem item = GenderFilterItem.valueOf(value);
        FilterHelper.showGenderFilterPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<GenderFilterItem>() {
                    @Override
                    public void onItemPicked(int index, GenderFilterItem item) {
                        mGenderView.setText(item.mName);
                        mFilterInfo.setGender(item.mValue);
                    }

                });
    }
}
