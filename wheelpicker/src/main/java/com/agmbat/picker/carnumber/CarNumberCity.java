package com.agmbat.picker.carnumber;

import com.agmbat.picker.linkage.LinkageSecond;

import java.util.List;

/**
 * 车牌号码城市代号字母
 */
public class CarNumberCity implements LinkageSecond<Void> {

    private String name;

    public CarNumberCity(String name) {
        this.name = name;
    }

    @Override
    public Object getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Void> getThirds() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CarNumberCity)) {
            return false;
        }
        CarNumberCity obj1 = (CarNumberCity) obj;
        return name.equals(obj1.getName());
    }

    @Override
    public String toString() {
        return "name=" + name;
    }

}