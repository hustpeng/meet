package com.agmbat.picker.carnumber;

import android.support.annotation.NonNull;

import com.agmbat.picker.linkage.Provider;

import java.util.ArrayList;
import java.util.List;

public class CarNumberDataProvider implements Provider<CarNumberProvince, CarNumberCity, Void> {

    private static final String[] ABBREVIATIONS = {
            "京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪",
            "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘",
            "粤", "桂", "琼", "渝", "川", "贵", "云", "藏", "陕",
            "甘", "青", "宁", "新"};

    private List<CarNumberProvince> provinces = new ArrayList<>();

    CarNumberDataProvider() {
        for (String abbreviation : ABBREVIATIONS) {
            this.provinces.add(new CarNumberProvince(abbreviation));
        }
    }

    @Override
    public boolean isOnlyTwo() {
        return true;
    }

    @Override
    @NonNull
    public List<CarNumberProvince> initFirstData() {
        return provinces;
    }

    @Override
    @NonNull
    public List<CarNumberCity> linkageSecondData(int firstIndex) {
        return provinces.get(firstIndex).getSeconds();
    }

    @Override
    @NonNull
    public List<Void> linkageThirdData(int firstIndex, int secondIndex) {
        return new ArrayList<>();
    }

}