package com.agmbat.picker.address;

import com.agmbat.picker.linkage.LinkageThird;
import com.google.gson.annotations.SerializedName;

/**
 * 区县
 */
public class County extends Area implements LinkageThird {

    @SerializedName("cityId")
    private String cityId;

    public County() {
        super();
    }

    public County(String areaName) {
        super(areaName);
    }

    public County(String areaId, String areaName) {
        super(areaId, areaName);
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

}
