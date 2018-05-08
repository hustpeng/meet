package com.agmbat.meetyou.discovery.filter;

import com.agmbat.picker.address.Address;

/**
 * 过虑信息
 */
public class FilterInfo {

    private int mGender = -1;
    private int mMarriage = -1;
    private int mEducation = -1;
    private String mCareer = "不限";
    private int mWage = -1;
    private int mHouse = -1;
    private int mCar = -1;

    private Address mBirthplace = Address.fromProvinceCityText("不限,不限");

    private Address mWorkarea = Address.fromProvinceCityText("不限,不限");

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public int getStartAge() {
        return 0;
    }

    public int getEndAge() {
        return 0;
    }

    public int getStartHeight() {
        return 0;
    }

    public int getEndHeight() {
        return 0;
    }

    public int getMarriage() {
        return mMarriage;
    }

    public void setMarriage(int marriage) {
        mMarriage = marriage;
    }

    public Address getBirthplace() {
        return mBirthplace;
    }

    public void setBirthplace(Address birthplace) {
        mBirthplace = birthplace;
    }

    public Address getWorkarea() {
        return mWorkarea;
    }

    public void setWorkarea(Address workarea) {
        mWorkarea = workarea;
    }

    public int getEducation() {
        return mEducation;
    }

    public void setEducation(int education) {
        mEducation = education;
    }

    public String getCareer() {
        return mCareer;
    }

    public void setCareer(String career) {
        mCareer = career;
    }

    public int getWage() {
        return mWage;
    }

    public void setWage(int wage) {
        mWage = wage;
    }

    public int getHouse() {
        return mHouse;
    }

    public void setHouse(int house) {
        mHouse = house;
    }

    public int getCar() {
        return mCar;
    }

    public void setCar(int car) {
        mCar = car;
    }

    public String getBirthplaceText() {
        String text = mBirthplace.getProvince() + mBirthplace.getCity();
        if ("不限不限".equals(text)) {
            text = "不限";
        }
        return text;
    }

    public String getWorkareaText() {
        String text = mWorkarea.getProvince() + mWorkarea.getCity();
        if ("不限不限".equals(text)) {
            text = "不限";
        }
        return text;
    }
}
