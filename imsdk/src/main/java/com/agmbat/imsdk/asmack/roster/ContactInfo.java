package com.agmbat.imsdk.asmack.roster;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

import org.jivesoftware.smack.util.XmppStringUtils;

import java.io.Serializable;

/**
 * 联系人
 */
@Table(name = "contact")
public class ContactInfo implements Serializable, AuthStatus {

    /**
     * 对应roster信息
     */
    public static final int ROSTER_TYPE_NONE = 1;
    public static final int ROSTER_TYPE_TO = 2;
    public static final int ROSTER_TYPE_FROM = 3;
    public static final int ROSTER_TYPE_BOTH = 4;
    public static final int ROSTER_TYPE_REMOVE = 5;


    /**
     * 别人申请加我为好友, 但未未处理
     */
    public static final int ROSTER_SUBSCRIBE_ME = 6;

    /**
     * 我申请别人加好友, 对方未处理
     */
    public static final int ROSTER_SUBSCRIBE_OTHER = 7;


    /**
     * 此id为数据库存储id值
     */
    @Column(name = "id", isId = true)
    private long mId;

    /**
     * 当前联系人属于哪个分组
     */
    @Column(name = "group_name")
    private String mGroupName;

    /**
     * 联系人id标识
     */
    @Column(name = "jid")
    @SerializedName("jid")
    private String mBareJid = "";

    /**
     * 联系人昵称
     */
    @Column(name = "nickname")
    @SerializedName("nickname")
    private String mNickname;

    /**
     * 用户给好友的备注
     */
    @Column(name = "remark")
    private String mRemark;

    /**
     * 用户头像url
     */
    @Column(name = "avatar")
    @SerializedName("avatar_url")
    private String mAvatar;

    /**
     * 性别
     */
    @Column(name = "gender")
    @SerializedName("gender")
    private int mGender;

    /**
     * 用户号（Meet项目特殊字段）
     */
    @Column(name = "im_uid")
    @SerializedName("im_uid")
    private int mImUid;

    /**
     * 与当前好友的关系
     */
    private int mRosterType;

    /**
     * 本地更新时间, 用户缓存当前缓存是否失效
     */
    @Column(name = "local_update_time")
    private long mLocalUpdateTime;


    /****** 附近的人需要的字段 start **************/

    @SerializedName("creation")
    private long creation;

    /**
     * 实名认证状态，1 已认证，0未认证，2 认证未通过
     */
    @SerializedName("auth_status")
    private int mAuthStatus;

    @SerializedName("geo")
    private String geo;

    @SerializedName("last_logout")
    private long last_logout;

    @SerializedName("last_login")
    private long last_login;

    /**
     * 用户年龄
     */
    @SerializedName("age")
    private int mAge;

    @SerializedName("dist")
    private double dist;

    /**
     * 出生年份
     */
    @SerializedName("birth")
    private int birth;

    @SerializedName("birthplace")
    private String birthplace;

    @SerializedName("car")
    private int car;

    @SerializedName("career")
    private String career;

    @SerializedName("demand")
    private String demand;

    @SerializedName("education")
    private int education;

    @SerializedName("height")
    private int height;

    @SerializedName("hobby")
    private String hobby;

    @SerializedName("house")
    private int house;

    @SerializedName("industry")
    private String industry;

    @SerializedName("introduce")
    private String introduce;

    @SerializedName("marriage")
    private int marriage;

    @SerializedName("residence")
    private String residence;

    @SerializedName("status")
    private String status;

    @SerializedName("wage")
    private int wage;

    @SerializedName("weight")
    private int weight;

    @SerializedName("workarea")
    private String workarea;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getNickName() {
        return mNickname;
    }

    public String getPersonalMsg() {
        return "getPersonalMsg";
    }

    public void setBareJid(String jid) {
        mBareJid = jid;
    }

    public String getBareJid() {
        return mBareJid;
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public int getGender() {
        return mGender;
    }

    public int getImUid() {
        return mImUid;
    }

    public void setImUid(int imUid) {
        mImUid = imUid;
    }

    public int getAuthStatus() {
        return mAuthStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.mAuthStatus = authStatus;
    }

    /**
     * 获取用户名称,登录帐号名称
     *
     * @return
     */
    public String getUserName() {
        return XmppStringUtils.parseName(mBareJid);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    /**
     * 更新联系人信息
     *
     * @param contactInfo
     */
    public void apply(ContactInfo contactInfo) {
        mId = contactInfo.mId;
        mBareJid = contactInfo.mBareJid;
        mNickname = contactInfo.mNickname;
        mRemark = contactInfo.mRemark;
        mAvatar = contactInfo.mAvatar;
        mGender = contactInfo.mGender;
        birth = contactInfo.getBirth();
        mAuthStatus = contactInfo.getAuthStatus();
        mImUid = contactInfo.getImUid();
    }

    /**
     * 获取距离
     *
     * @return
     */
    public double getDist() {
        return dist;
    }

    /**
     * 返回当前用户年龄
     *
     * @return
     */
    public int getAge() {
        return mAge;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String name) {
        mGroupName = name;
    }

    public long getLocalUpdateTime() {
        return mLocalUpdateTime;
    }

    public void setLocalUpdateTime(long time) {
        mLocalUpdateTime = time;
    }

    public int getRosterType() {
        return mRosterType;
    }

    public void setRosterType(int rosterType) {
        mRosterType = rosterType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ContactInfo)) {
            return false;
        }
        ContactInfo other = (ContactInfo) obj;
        return mBareJid.equals(other.mBareJid);
    }

    public int getWage() {
        return wage;
    }

    public void setWage(int wage) {
        this.wage = wage;
    }

    public int getEducation() {
        return education;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public int getMarriage() {
        return marriage;
    }

    public void setMarriage(int marriage) {
        this.marriage = marriage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public int getHouse() {
        return house;
    }

    public void setHouse(int house) {
        this.house = house;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getWorkarea() {
        return workarea;
    }

    public void setWorkarea(String workarea) {
        this.workarea = workarea;
    }
}
