package com.agmbat.meetyou.discovery.filter;

/**
 * 过虑信息
 */
public class FilterInfo {

    private int mGender = -1;
    private int mMarriage = -1;
    private int mEducation = -1;

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

    public String getBirthplace() {
        return "";
    }

    public String getWorkarea() {
        return "";
    }

    public int getEducation() {
        return mEducation;
    }

    public void setEducation(int education) {
        mEducation = education;
    }

    public String getCareer() {
        return "";
    }

    public int getWage() {
        return 0;
    }

    public int getHouse() {
        return 0;
    }

    public int getCar() {
        return 0;
    }


}
