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
import com.agmbat.picker.helper.filter.EducationFilterItem;
import com.agmbat.picker.helper.filter.FilterHelper;
import com.agmbat.picker.helper.filter.GenderFilterItem;
import com.agmbat.picker.helper.filter.MarriageFilterItem;

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

    @BindView(R.id.input_marriage)
    TextView mMarriageView;

    @BindView(R.id.input_education)
    TextView mEducationView;

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
        mMarriageView.setText(MarriageFilterItem.valueOf(mFilterInfo.getMarriage()).mName);
        mEducationView.setText(EducationFilterItem.valueOf(mFilterInfo.getEducation()).mName);
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

    /**
     * 选择婚况
     */
    @OnClick(R.id.btn_marriage)
    void onClickMarriage() {
        int value = mFilterInfo.getMarriage();
        MarriageFilterItem item = MarriageFilterItem.valueOf(value);
        FilterHelper.showMarriageFilterItemPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<MarriageFilterItem>() {
                    @Override
                    public void onItemPicked(int index, MarriageFilterItem item) {
                        mMarriageView.setText(item.mName);
                        mFilterInfo.setMarriage(item.mValue);
                    }

                });
    }

    @OnClick(R.id.btn_education)
    void onClickEducation() {
        int value = mFilterInfo.getEducation();
        EducationFilterItem item = EducationFilterItem.valueOf(value);
        FilterHelper.showEducationPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<EducationFilterItem>() {
                    @Override
                    public void onItemPicked(int index, EducationFilterItem item) {
                        mEducationView.setText(item.mName);
                        mFilterInfo.setEducation(item.mValue);
                    }

                });
    }
}
