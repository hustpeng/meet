package com.agmbat.picker.address;

import android.support.annotation.NonNull;

import com.agmbat.picker.linkage.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址提供者
 */
public class AddressProvider implements Provider<Province, City, County> {

    private List<Province> firstList = new ArrayList<>();
    private List<List<City>> secondList = new ArrayList<>();
    private List<List<List<County>>> thirdList = new ArrayList<>();

    AddressProvider(List<Province> provinces) {
        parseData(provinces);
    }

    @Override
    public boolean isOnlyTwo() {
        return false;
    }

    @Override
    @NonNull
    public List<Province> initFirstData() {
        return firstList;
    }

    @Override
    @NonNull
    public List<City> linkageSecondData(int firstIndex) {
        if (secondList.size() <= firstIndex) {
            return new ArrayList<>();
        }
        return secondList.get(firstIndex);
    }

    @Override
    @NonNull
    public List<County> linkageThirdData(int firstIndex, int secondIndex) {
        if (thirdList.size() <= firstIndex) {
            return new ArrayList<>();
        }
        List<List<County>> lists = thirdList.get(firstIndex);
        if (lists.size() <= secondIndex) {
            return new ArrayList<>();
        }
        return lists.get(secondIndex);
    }

    private void parseData(List<Province> data) {
        int provinceSize = data.size();
        //添加省
        for (int x = 0; x < provinceSize; x++) {
            Province pro = data.get(x);
            firstList.add(pro);
            List<City> cities = pro.getCities();
            List<City> xCities = new ArrayList<>();
            List<List<County>> xCounties = new ArrayList<>();
            int citySize = cities.size();
            //添加地市
            for (int y = 0; y < citySize; y++) {
                City cit = cities.get(y);
                cit.setProvinceId(pro.getAreaId());
                xCities.add(cit);
                List<County> counties = cit.getCounties();
                ArrayList<County> yCounties = new ArrayList<>();
                int countySize = counties.size();
                //添加区县
                for (int z = 0; z < countySize; z++) {
                    County cou = counties.get(z);
                    cou.setCityId(cit.getAreaId());
                    yCounties.add(cou);
                }
                xCounties.add(yCounties);
            }
            secondList.add(xCities);
            thirdList.add(xCounties);
        }
    }

}