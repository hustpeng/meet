package com.agmbat.picker.linkage;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认的数据提供者
 */
public class DefaultDataProvider<Fst extends LinkageFirst<Snd>, Snd extends LinkageSecond<Trd>, Trd> implements Provider<Fst, Snd, Trd> {

    private List<Fst> firstList = new ArrayList<>();
    private List<List<Snd>> secondList = new ArrayList<>();
    private List<List<List<Trd>>> thirdList = new ArrayList<>();
    private boolean onlyTwo = false;

    public DefaultDataProvider(List<Fst> f, List<List<Snd>> s, List<List<List<Trd>>> t) {
        this.firstList = f;
        this.secondList = s;
        if (t == null || t.size() == 0) {
            this.onlyTwo = true;
        } else {
            this.thirdList = t;
        }
    }

    public boolean isOnlyTwo() {
        return onlyTwo;
    }

    @Override
    @NonNull
    public List<Fst> initFirstData() {
        return firstList;
    }

    @Override
    @NonNull
    public List<Snd> linkageSecondData(int firstIndex) {
        return secondList.get(firstIndex);
    }

    @Override
    @NonNull
    public List<Trd> linkageThirdData(int firstIndex, int secondIndex) {
        if (onlyTwo) {
            return new ArrayList<>();
        } else {
            return thirdList.get(firstIndex).get(secondIndex);
        }
    }

}
