package com.agmbat.picker.address;

import com.agmbat.picker.linkage.LinkageSecond;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 地市
 */
public class City extends Area implements LinkageSecond<County> {

    @SerializedName("provinceId")
    private String provinceId;

    @SerializedName("counties")
    private List<County> counties = new ArrayList<>();

    public City() {
        super();
    }

    public City(String areaName) {
        super(areaName);
    }

    public City(String areaId, String areaName) {
        super(areaId, areaName);
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public List<County> getCounties() {
        return counties;
    }

    public void setCounties(List<County> counties) {
        this.counties = counties;
    }

    @Override
    public List<County> getThirds() {
        return counties;
    }

}