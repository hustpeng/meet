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
        //return mVCardObject != null && mVCardExtendObject != null;
        return mVCardObject != null;
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
        return null == mVCardObject ? 0 : mVCardObject.getGender();
    }

    public int getAuth() {
        return null == mVCardObject ? 0 : mVCardObject.getAuth();
    }

    public void setGender(int gender) {
        if (null == mVCardObject) {
            mVCardObject = new VCardObject();
        }
        mVCardObject.setGender(gender);
    }

    public int getAge() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        return thisYear - getBirthYear();
    }

    /**
     * 设置头像url
     *
     * @param url
     */
    public void setAvatar(String url) {
        if (null == mVCardObject) {
            mVCardObject = new VCardObject();
        }
        mVCardObject.setAvatar(url);
    }

    public String getAvatar() {
        return null == mVCardObject ? "" : mVCardObject.getAvatar();
    }


    public int getImUid() {
        return null == mVCardObject ? 0 : mVCardObject.getImUid();
    }


    public String getDemand() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getDemand();
    }

    public void setDemand(String text) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setDemand(text);
    }

    public String getHobby() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getHobby();
    }

    public void setHobby(String hobby) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setHobby(hobby);
    }

    public String getIntroduce() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getIntroduce();
    }

    public void setIntroduce(String introduce) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setIntroduce(introduce);
    }

    public String getNickname() {
        return null == mVCardObject ? "" : mVCardObject.getNickname();
    }

    public void setNickname(String nickName) {
        if (null == mVCardObject) {
            mVCardObject = new VCardObject();
        }
        mVCardObject.setNickname(nickName);
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserName() {
        return null == mVCardObject ? "" : mVCardObject.getUserName();
    }

    public String getJid() {
        return null == mVCardObject ? "" : mVCardObject.getJid();
    }

    public String getStatus() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getStatus();
    }

    public void setStatus(String status) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setStatus(status);
    }

    public int getBirthYear() {
        return null == mVCardObject ? 0 : mVCardObject.getBirthYear();
    }

    public void setBirthYear(int birthYear) {
        if (null == mVCardObject) {
            mVCardObject = new VCardObject();
        }
        mVCardObject.setBirthYear(birthYear);
    }

    public int getHeight() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getHeight();
    }

    public void setHeight(int height) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setHeight(height);
    }

    public int getWeight() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getWeight();
    }

    public void setWeight(int weight) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setWeight(weight);
    }

    public int getWage() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getWage();
    }

    public void setWage(int wage) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setWage(wage);
    }

    public String getResidence() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getResidence();
    }

    public void setResidence(String residence) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setResidence(residence);
    }

    public int getEducation() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getEducation();
    }

    public void setEducation(int education) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setEducation(education);
    }

    public int getMarriage() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getMarriage();
    }

    public void setMarriage(int marriage) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setMarriage(marriage);
    }

    public String getIndustry() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getIndustry();
    }

    public void setIndustry(String industry) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setIndustry(industry);
    }

    public String getCareer() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getCareer();
    }

    public void setCareer(String career) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setCareer(career);
    }

    public int getCar() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getCar();
    }

    public void setCar(int car) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setCar(car);
    }

    public int getHouse() {
        return null == mVCardExtendObject ? 0 : mVCardExtendObject.getHouse();
    }

    public void setHouse(int house) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setHouse(house);
    }

    public String getWorkarea() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getWorkarea();
    }

    public void setWorkarea(String workarea) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setWorkarea(workarea);
    }

    public String getBirthplace() {
        return null == mVCardExtendObject ? "" : mVCardExtendObject.getBirthplace();
    }

    public void setBirthplace(String birthplace) {
        if (null == mVCardExtendObject) {
            mVCardExtendObject = new VCardExtendObject();
        }
        mVCardExtendObject.setBirthplace(birthplace);
    }


}
