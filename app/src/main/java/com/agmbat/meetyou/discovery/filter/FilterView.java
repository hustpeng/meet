package com.agmbat.meetyou.discovery.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.picker.OptionPicker;
import com.agmbat.picker.SinglePicker;
import com.agmbat.picker.helper.filter.CarFilterItem;
import com.agmbat.picker.helper.filter.EducationFilterItem;
import com.agmbat.picker.helper.filter.FilterHelper;
import com.agmbat.picker.helper.filter.GenderFilterItem;
import com.agmbat.picker.helper.filter.HouseFilterItem;
import com.agmbat.picker.helper.filter.MarriageFilterItem;
import com.agmbat.picker.helper.filter.WageFilterItem;

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

    @BindView(R.id.input_wage)
    TextView mWageView;

    @BindView(R.id.input_career)
    TextView mCareerView;

    @BindView(R.id.input_house)
    TextView mHouseView;

    @BindView(R.id.input_car)
    TextView mCarView;

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
        mCareerView.setText(mFilterInfo.getCareer());
        mWageView.setText(WageFilterItem.valueOf(mFilterInfo.getWage()).mName);
        mHouseView.setText(HouseFilterItem.valueOf(mFilterInfo.getHouse()).mName);
        mCarView.setText(CarFilterItem.valueOf(mFilterInfo.getCar()).mName);
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
        FilterHelper.showMarriageFilterPicker(getContext(), item,
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
        FilterHelper.showEducationFilterPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<EducationFilterItem>() {
                    @Override
                    public void onItemPicked(int index, EducationFilterItem item) {
                        mEducationView.setText(item.mName);
                        mFilterInfo.setEducation(item.mValue);
                    }

                });
    }

    @OnClick(R.id.btn_career)
    void onClickCareer() {
        String value = mFilterInfo.getCareer();
        FilterHelper.showCareerFilterPicker(getContext(), value,
                new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        mCareerView.setText(item);
                        mFilterInfo.setCareer(item);
                    }

                });
    }

    @OnClick(R.id.btn_wage)
    void onClickWage() {
        int value = mFilterInfo.getWage();
        WageFilterItem item = WageFilterItem.valueOf(value);
        FilterHelper.showWageFilterPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<WageFilterItem>() {
                    @Override
                    public void onItemPicked(int index, WageFilterItem item) {
                        mWageView.setText(item.mName);
                        mFilterInfo.setWage(item.mValue);
                    }

                });
    }

    @OnClick(R.id.btn_house)
    void onClickHouse() {
        int value = mFilterInfo.getHouse();
        HouseFilterItem item = HouseFilterItem.valueOf(value);
        FilterHelper.showHouseFilterPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<HouseFilterItem>() {
                    @Override
                    public void onItemPicked(int index, HouseFilterItem item) {
                        mHouseView.setText(item.mName);
                        mFilterInfo.setHouse(item.mValue);
                    }

                });
    }

    @OnClick(R.id.btn_car)
    void onClickCar() {
        int value = mFilterInfo.getCar();
        CarFilterItem item = CarFilterItem.valueOf(value);
        FilterHelper.showCarFilterPicker(getContext(), item,
                new SinglePicker.OnItemPickListener<CarFilterItem>() {
                    @Override
                    public void onItemPicked(int index, CarFilterItem item) {
                        mCarView.setText(item.mName);
                        mFilterInfo.setCar(item.mValue);
                    }

                });
    }
}
