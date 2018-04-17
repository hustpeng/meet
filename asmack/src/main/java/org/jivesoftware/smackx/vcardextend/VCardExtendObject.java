package org.jivesoftware.smackx.vcardextend;

import android.text.TextUtils;

import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

import java.util.ArrayList;
import java.util.Date;

public class VCardExtendObject implements ICacheStoreObject {

    /**
     * 身高height
     */
    public static final String KEY_HEIGHT = "height";

    /**
     * 学历education
     */
    public static final String KEY_EDUCATION = "education";

    /**
     * 月薪wage
     */
    public static final String KEY_WAGE = "wage";

    /**
     * 工作地区workarea
     */
    public static final String KEY_WORKAREA = "workarea";

    /**
     * 婚况marriage
     */
    public static final String KEY_MARRIAGE = "marriage";

    /**
     * 体重weight
     */
    public static final String KEY_WEIGHT = "weight";

    /**
     * 籍贯birthplace
     */
    public static final String KEY_BIRTHPLACE = "birthplace";

    /**
     * 户口在地residence
     */
    public static final String KEY_RESIDENCE = "residence";

    /**
     * 行业industry
     */
    public static final String KEY_INDUSTRY = "industry";

    /**
     * 职业career
     */
    public static final String KEY_CAREER = "career";

    /**
     * 住房情况house
     */
    public static final String KEY_HOUSE = "house";

    /**
     * 购车情况car
     */
    public static final String KEY_CAR = "car";

    /**
     * 兴趣爱好hobby
     */
    public static final String KEY_HOBBY = "hobby";

    /**
     * 自我简介introduce
     */
    public static final String KEY_INTRODUCE = "introduce";

    /**
     * 择友要求demand
     */
    public static final String KEY_DEMAND = "demand";

    /**
     * 个人签名status
     */
    public static final String KEY_STATUS = "status";


    /**
     * 身高
     */
    private int height;

    /**
     * 学历
     */
    private String education;

    /**
     * 月薪
     */
    private int wage;

    /**
     * 工作地区
     */
    private String workarea;

    /**
     * 婚况
     */
    private String marriage;

    /**
     * 体重
     */
    private int weight;

    /**
     * 籍贯
     */
    private String birthplace;

    /**
     * 户口在地
     */
    private String residence;

    /**
     * 行业
     */
    private int industry;

    /**
     * 职业
     */
    private int career;

    /**
     * 购车情况
     */
    private int car;

    /**
     * 兴趣爱好
     */
    private int hobby;

    /**
     * 自我简介
     */
    private String introduce;

    /**
     * 择友要求
     */
    private int demand;

    /**
     * 个人签名
     */
    private String status;

    //            //
    private String jid;

    //public info
    private String birthday;
    private String astrological;
    private String bodyType;
    private String ethnicity;
    private String relationship;
    private ArrayList<String> personality;
    private ArrayList<String> lookingFor;
    private ArrayList<String> language;
    private ArrayList<String> publicPhotos;

    //private info
    private boolean hasPrivateInfo;
    private String sexualPosition;
    private String orientation;
    private String HIVStatus;
    private String drugs;
    private String livin;
    private ArrayList<String> sexualInterests;
    private ArrayList<String> privatePhotos;

    //update date
    private Date update_date;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getWage() {
        return wage;
    }

    public void setWage(int wage) {
        this.wage = wage;
    }

    public String getWorkarea() {
        return workarea;
    }

    public void setWorkarea(String workarea) {
        this.workarea = workarea;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public int getIndustry() {
        return industry;
    }

    public void setIndustry(int industry) {
        this.industry = industry;
    }

    public int getCareer() {
        return career;
    }

    public void setCareer(int career) {
        this.career = career;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getHobby() {
        return hobby;
    }

    public void setHobby(int hobby) {
        this.hobby = hobby;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        if (TextUtils.isEmpty(jid)) {
            return "";
        }

        return jid.toLowerCase();
    }

    public String getJid() {
        if (TextUtils.isEmpty(jid)) {
            return "";
        }

        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAstrological() {
        return astrological;
    }

    public void setAstrological(String astrological) {
        this.astrological = astrological;
    }


    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public ArrayList<String> getPersonality() {
        return personality;
    }

    public void setPersonality(ArrayList<String> personality) {
        this.personality = personality;
    }

    public ArrayList<String> getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(ArrayList<String> lookingFor) {
        this.lookingFor = lookingFor;
    }

    public ArrayList<String> getLanguage() {
        return language;
    }

    public void setLanguage(ArrayList<String> language) {
        this.language = language;
    }

    public ArrayList<String> getPublicPhotos() {
        return publicPhotos;
    }

    public void setPublicPhotos(ArrayList<String> publicPhotos) {
        this.publicPhotos = publicPhotos;
    }

    public boolean isHasPrivateInfo() {
        return hasPrivateInfo;
    }

    public void setHasPrivateInfo(boolean hasPrivateInfo) {
        this.hasPrivateInfo = hasPrivateInfo;
    }

    public String getSexualPosition() {
        return sexualPosition;
    }

    public void setSexualPosition(String sexualPosition) {
        this.sexualPosition = sexualPosition;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getHIVStatus() {
        return HIVStatus;
    }

    public void setHIVStatus(String hIVStatus) {
        HIVStatus = hIVStatus;
    }

    public String getDrugs() {
        return drugs;
    }

    public void setDrugs(String drugs) {
        this.drugs = drugs;
    }

    public String getLivin() {
        return livin;
    }

    public void setLivin(String livin) {
        this.livin = livin;
    }

    public ArrayList<String> getSexualInterests() {
        return sexualInterests;
    }

    public void setSexualInterests(ArrayList<String> sexualInterests) {
        this.sexualInterests = sexualInterests;
    }

    public ArrayList<String> getPrivatePhotos() {
        return privatePhotos;
    }

    public void setPrivatePhotos(ArrayList<String> privatePhotos) {
        this.privatePhotos = privatePhotos;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }


    public static String getXmlNode(VCardExtendObject object) {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(VCardExtendProvider.elementName());
        buf.append(" xmlns=\"");
        buf.append(VCardExtendProvider.namespace());
        buf.append("\">");
//public
        buf.append("<PUBLIC>");
        XmppStringUtils.appendXml(buf, KEY_HEIGHT, String.valueOf(object.getHeight()));
        XmppStringUtils.appendXml(buf, KEY_EDUCATION, String.valueOf(object.getEducation()));
        XmppStringUtils.appendXml(buf, KEY_WAGE, String.valueOf(object.getWage()));
        XmppStringUtils.appendXml(buf, KEY_WORKAREA, object.getWorkarea());
        XmppStringUtils.appendXml(buf, KEY_MARRIAGE, String.valueOf(object.getMarriage()));
        XmppStringUtils.appendXml(buf, KEY_WEIGHT, String.valueOf(object.getWeight()));
        XmppStringUtils.appendXml(buf, KEY_BIRTHPLACE, object.getBirthplace());
        XmppStringUtils.appendXml(buf, KEY_RESIDENCE, object.getResidence());
        XmppStringUtils.appendXml(buf, KEY_INDUSTRY, String.valueOf(object.getIndustry()));
        XmppStringUtils.appendXml(buf, KEY_CAREER, String.valueOf(object.getCareer()));
        XmppStringUtils.appendXml(buf, KEY_HOUSE, String.valueOf(object.getWage()));
        XmppStringUtils.appendXml(buf, KEY_CAR, String.valueOf(object.getCar()));
        XmppStringUtils.appendXml(buf, KEY_HOBBY, String.valueOf(object.getHobby()));
        XmppStringUtils.appendXml(buf, KEY_INTRODUCE, String.valueOf(object.getIntroduce()));
        XmppStringUtils.appendXml(buf, KEY_DEMAND, String.valueOf(object.getDemand()));
        XmppStringUtils.appendXml(buf, KEY_STATUS, object.getStatus());

        buf.append("</PUBLIC>");

//private info
        buf.append("<PRIVATE>");
        buf.append("</PRIVATE>");

        buf.append("</");
        buf.append(VCardExtendProvider.elementName());
        buf.append(">");
        return buf.toString();
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }


}