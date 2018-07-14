package com.agmbat.picker.linkage;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 数据提供接口
 */
public interface Provider<Fst extends LinkageFirst<Snd>, Snd extends LinkageSecond<Trd>, Trd> {

    /**
     * 是否只是二级联动
     */
    boolean isOnlyTwo();

    /**
     * 初始化第一级数据
     */
    @NonNull
    List<Fst> initFirstData();

    /**
     * 根据第一级数据联动第二级数据
     */
    @NonNull
    List<Snd> linkageSecondData(int firstIndex);

    /**
     * 根据第一二级数据联动第三级数据
     */
    @NonNull
    List<Trd> linkageThirdData(int firstIndex, int secondIndex);

}