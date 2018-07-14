package com.agmbat.picker.linkage;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 兼容旧版API
 */
public abstract class DataProvider implements Provider<StringLinkageFirst, StringLinkageSecond, String> {

    @NonNull
    public abstract List<String> provideFirstData();

    @NonNull
    public abstract List<String> provideSecondData(int firstIndex);

    @Nullable
    public abstract List<String> provideThirdData(int firstIndex, int secondIndex);

    @NonNull
    @Override
    public List<StringLinkageFirst> initFirstData() {
        List<StringLinkageFirst> firsts = new ArrayList<>();
        List<String> data = provideFirstData();
        int i = 0;
        for (String str : data) {
            firsts.add(new StringLinkageFirst(str, linkageSecondData(i)));
            i++;
        }
        return firsts;
    }

    @NonNull
    @Override
    public List<StringLinkageSecond> linkageSecondData(int firstIndex) {
        List<StringLinkageSecond> seconds = new ArrayList<>();
        List<String> data = provideSecondData(firstIndex);
        int i = 0;
        for (String str : data) {
            seconds.add(new StringLinkageSecond(str, linkageThirdData(firstIndex, i)));
            i++;
        }
        return seconds;
    }

    @NonNull
    @Override
    public List<String> linkageThirdData(int firstIndex, int secondIndex) {
        List<String> thirdData = provideThirdData(firstIndex, secondIndex);
        if (thirdData == null) {
            thirdData = new ArrayList<>();
        }
        return thirdData;
    }

}
