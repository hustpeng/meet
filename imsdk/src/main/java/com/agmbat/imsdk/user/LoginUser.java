package com.agmbat.imsdk.user;

import org.jivesoftware.smackx.vcard.VCardObject;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

import java.util.Calendar;

/**
 * 当前已登陆的用户信息, 可读也可以修改上传到服务器
 */
public class LoginUser {

    /**
     * 当前用户信息
     */
    private VCardObject mVCardObject;

    /**
     * 用户扩展信息
     */
    private VCardExtendObject mVCardExtendObject;

    /**
     * 判断用户信息是否有效
     *
     * @return
     */
    public boolean isValid() {
        return mVCardObject != null && mVCardExtendObject != null;
    }

    public VCardObject getVCardObject() {
        return mVCardObject;
    }

    public void setVCardObject(VCardObject vCardObject) {
        mVCardObject = vCardObject;
    }

    public VCardExtendObject getVCardExtendObject() {
        return mVCardExtendObject;
    }

    public void setVCardExtendObject(VCardExtendObject vCardExtendObject) {
        mVCardExtendObject = vCardExtendObject;
    }

    public int getGender() {
        return mVCardObject.getGender();
    }

    public int getAuth(){
       return mVCardObject.getAuth();
    }

    public void setGender(int gender) {
        mVCardObject.setGender(gender);
    }

    public int getAge() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        return thisYear - mVCardObject.getBirthYear();
    }

    /**
     * 设置头像url
     *
     * @param url
     */
    public void setAvatar(String url) {
        mVCardObject.setAvatar(url);
    }

    public String getAvatar() {
        return mVCardObject.getAvatar();
    }


    public int getImUid() {
        return mVCardObject.getImUid();
    }


    public String getDemand() {
        return mVCardExtendObject.getDemand();
    }

    public void setDemand(String text) {
        mVCardExtendObject.setDemand(text);
    }

    public String getHobby() {
        return mVCardExtendObject.getHobby();
    }

    public void setHobby(String hobby) {
        mVCardExtendObject.setHobby(hobby);
    }

    public String getIntroduce() {
        return mVCardExtendObject.getIntroduce();
    }

    public void setIntroduce(String introduce) {
        mVCardExtendObject.setIntroduce(introduce);
    }

    public String getNickname() {
        return mVCardObject.getNickname();
    }

    public void setNickname(String nickName) {
        mVCardObject.setNickname(nickName);
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserName() {
        return mVCardObject.getUserName();
    }

    public String getJid() {
        return mVCardObject.getJid();
    }

    public String getStatus() {
        return mVCardExtendObject.getStatus();
    }

    public void setStatus(String status) {
        mVCardExtendObject.setStatus(status);
    }

    public int getBirthYear() {
        return mVCardObject.getBirthYear();
    }

    public void setBirthYear(int birthYear) {
        mVCardObject.setBirthYear(birthYear);
    }

    public int getHeight() {
        return mVCardExtendObject.getHeight();
    }

    public void setHeight(int height) {
        mVCardExtendObject.setHeight(height);
    }

    public int getWeight() {
        return mVCardExtendObject.getWeight();
    }

    public void setWeight(int weight) {
        mVCardExtendObject.setWeight(weight);
    }

    public int getWage() {
        return mVCardExtendObject.getWage();
    }

    public void setWage(int wage) {
        mVCardExtendObject.setWage(wage);
    }

    public String getResidence() {
        return mVCardExtendObject.getResidence();
    }

    public void setResidence(String residence) {
        mVCardExtendObject.setResidence(residence);
    }

    public int getEducation() {
        return mVCardExtendObject.getEducation();
    }

    public void setEducation(int education) {
        mVCardExtendObject.setEducation(education);
    }

    public int getMarriage() {
        return mVCardExtendObject.getMarriage();
    }

    public void setMarriage(int marriage) {
        mVCardExtendObject.setMarriage(marriage);
    }

    public String getIndustry() {
        return mVCardExtendObject.getIndustry();
    }

    public void setIndustry(String industry) {
        mVCardExtendObject.setIndustry(industry);
    }

    public String getCareer() {
        return mVCardExtendObject.getCareer();
    }

    public void setCareer(String career) {
        mVCardExtendObject.setCareer(career);
    }

    public int getCar() {
        return mVCardExtendObject.getCar();
    }

    public void setCar(int car) {
        mVCardExtendObject.setCar(car);
    }

    public int getHouse() {
        return mVCardExtendObject.getHouse();
    }

    public void setHouse(int house) {
        mVCardExtendObject.setHouse(house);
    }

    public String getWorkarea() {
        return mVCardExtendObject.getWorkarea();
    }

    public void setWorkarea(String workarea) {
        mVCardExtendObject.setWorkarea(workarea);
    }

    public String getBirthplace() {
        return mVCardExtendObject.getBirthplace();
    }

    public void setBirthplace(String birthplace) {
        mVCardExtendObject.setBirthplace(birthplace);
    }


}
