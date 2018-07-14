package com.agmbat.picker.address;

import android.text.TextUtils;

import com.agmbat.picker.linkage.LinkageItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 省市县抽象
 */
public abstract class Area implements LinkageItem, Serializable {

    @SerializedName("areaId")
    private String areaId;

    @SerializedName("areaName")
    private String areaName;

    public Area() {
        super();
    }

    public Area(String areaName) {
        this.areaId = "";
        this.areaName = areaName == null ? "" : areaName;
    }

    public Area(String areaId, String areaName) {
        this.areaId = areaId;
        this.areaName = areaName == null ? "" : areaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public Object getId() {
        return areaId;
    }

    @Override
    public String getName() {
        return areaName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Area)) {
            return false;
        }
        Area obj1 = (Area) obj;
        if (!TextUtils.isEmpty(areaId)) {
            return areaId.equals(obj1.getAreaId());
        }
        return areaName.equals(obj1.getAreaName());
    }

    @Override
    public String toString() {
        return "areaId=" + areaId + ",areaName=" + areaName;
    }

}