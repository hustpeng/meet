package org.jivesoftware.smackx.vcard;

import android.text.TextUtils;

import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

import java.util.Date;

/**
 * 描述VCardObject
 */
public class VCardObject implements ICacheStoreObject {

    /**
     * 昵称
     */
    public static final String KEY_NICKNAME = "nickname";

    /**
     * 性别
     */
    public static final String KEY_GENDER = "gender";

    /**
     * 出生年份
     */
    public static final String KEY_BIRTH = "birth";

    /**
     * 头像地址
     */
    public static final String KEY_AVATAR = "avatar";

    /**
     * 系统用户ID（Meet项目特殊字段）
     */
    public static final String KEY_IM_UID = "im_uid";

    /**
     * 是否认证用户（Meet项目特殊字段）
     */
    public static final String KEY_AUTH = "auth";

    /**
     * 用户等级（Meet项目特殊字段）
     */
    public static final String KEY_GRADE = "grade";


    /**
     * 用户id
     */
    private String jid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private int gender;

    /**
     * 出生年份
     */
    private int birthYear;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 系统用户ID
     */
    private int imUid;

    /**
     * 用户认证状态
     */
    private int auth;

    private int grade;

    private String status;
    private Date update_date;

    public String getNickname() {
        if (TextUtils.isEmpty(nickname)) {
            return "";
        }
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
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

    public int getImUid() {
        return imUid;
    }

    public void setImUid(int imUid) {
        this.imUid = imUid;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserName() {
        return XmppStringUtils.parseName(jid);
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public String getStatus() {
        if (TextUtils.isEmpty(status)) {
            return "";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




    @Override
    public String toString() {
        return getXmlNode(this);
    }

    /**
     * 将VCardObject转为xml字符串
     *
     * @param object
     * @return
     */
    public static String getXmlNode(VCardObject object) {
        if (object == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(VCardProvider.elementName());
        buf.append(" xmlns=\"");
        buf.append(VCardProvider.namespace());
        buf.append("\">");

        XmppStringUtils.appendXml(buf, KEY_NICKNAME, object.getNickname());
        XmppStringUtils.appendXml(buf, KEY_GENDER, String.valueOf(object.getGender()));
        XmppStringUtils.appendXml(buf, KEY_BIRTH, String.valueOf(object.getBirthYear()));
        XmppStringUtils.appendXml(buf, KEY_AVATAR, object.getAvatar());

        buf.append("</");
        buf.append(VCardProvider.elementName());
        buf.append(">");
        return buf.toString();
    }

}