
package org.jivesoftware.smackx.vcardextend;

import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;

public class VCardExtendObject implements ICacheStoreObject{

    private String jid;

//public info
    private String birthday;
    private String astrological;
    private String height;
    private String weight;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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

    public static String getXmlNode(VCardExtendObject object)
    {
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
        buf.append("<BIRTHDAY>");
        if (!TextUtils.isEmpty(object.getBirthday())) {
            buf.append(XmppStringUtils.escapeForXML(object.getBirthday()));
        }
        buf.append("</BIRTHDAY>");

        buf.append("<ASTROLOGICAL>");
        if (!TextUtils.isEmpty(object.getAstrological())) {
            buf.append(XmppStringUtils.escapeForXML(object.getAstrological()));
        }
        buf.append("</ASTROLOGICAL>");

        buf.append("<ETHNICITY>");
        if (!TextUtils.isEmpty(object.getEthnicity())) {
            buf.append(XmppStringUtils.escapeForXML(object.getEthnicity()));
        }
        buf.append("</ETHNICITY>");

        buf.append("<HEIGHT>");
        if (!TextUtils.isEmpty(object.getHeight())) {
            buf.append(XmppStringUtils.escapeForXML(object.getHeight()));
        }
        buf.append("</HEIGHT>");

        buf.append("<WEIGHT>");
        if (!TextUtils.isEmpty(object.getWeight())) {
            buf.append(XmppStringUtils.escapeForXML(object.getWeight()));
        }
        buf.append("</WEIGHT>");

        buf.append("<BODYTYPE>");
        if (!TextUtils.isEmpty(object.getBodyType())) {
            buf.append(XmppStringUtils.escapeForXML(object.getBodyType()));
        }
        buf.append("</BODYTYPE>");

        buf.append("<RELATIONSHIP>");
        if (!TextUtils.isEmpty(object.getRelationship())) {
            buf.append(XmppStringUtils.escapeForXML(object.getRelationship()));
        }
        buf.append("</RELATIONSHIP>");

        buf.append("<PERSONALITY>");
        if (object.getPersonality() != null) {
            for (String item:object.getPersonality()) {
                buf.append("<V>");
                buf.append(XmppStringUtils.escapeForXML(item));
                buf.append("</V>");
             }
        }
        buf.append("</PERSONALITY>");

        buf.append("<LOOKINGFOR>");
        if (object.getLookingFor() != null) {
            for (String item:object.getLookingFor()) {
                buf.append("<V>");
                buf.append(XmppStringUtils.escapeForXML(item));
                buf.append("</V>");
             }
        }
        buf.append("</LOOKINGFOR>");

        buf.append("<LANGUAGE>");
        if (object.getLanguage() != null) {
            for (String item:object.getLanguage()) {
                buf.append("<V>");
                buf.append(XmppStringUtils.escapeForXML(item));
                buf.append("</V>");
             }
        }
        buf.append("</LANGUAGE>");

        buf.append("<PUBLICPHOTOS>");
        if (object.getPublicPhotos() != null) {
            for (String item:object.getPublicPhotos()) {
                buf.append("<V>");
                buf.append(XmppStringUtils.escapeForXML(item));
                buf.append("</V>");
             }
        }
        buf.append("</PUBLICPHOTOS>");
        buf.append("</PUBLIC>");
//private info
        buf.append("<PRIVATE>");
        buf.append("<SEXUALPOSITION>");
        if (!TextUtils.isEmpty(object.getSexualPosition())) {
            buf.append(XmppStringUtils.escapeForXML(object.getSexualPosition()));
        }
        buf.append("</SEXUALPOSITION>");

        buf.append("<ORIENTATION>");
        if (!TextUtils.isEmpty(object.getOrientation())) {
            buf.append(XmppStringUtils.escapeForXML(object.getOrientation()));
        }
        buf.append("</ORIENTATION>");

        buf.append("<HIV>");
        if (!TextUtils.isEmpty(object.getHIVStatus())) {
            buf.append(XmppStringUtils.escapeForXML(object.getHIVStatus()));
        }
        buf.append("</HIV>");

//        buf.append("<DRUGS>");
//        if (!TextUtils.isEmpty(getDrugs())) {
//            buf.append(XmppStringUtils.escapeForXML(getDrugs()));
//        }
//        buf.append("</DRUGS>");
//
//        buf.append("<LIVEIN>");
//        if (!TextUtils.isEmpty(getLivin())) {
//            buf.append(XmppStringUtils.escapeForXML(getLivin()));
//        }
//        buf.append("</LIVEIN>");
//
//        buf.append("<SEXUALINTERESTS>");
//        for (String item:getSexualInterests()) {
//           buf.append("<V>");
//           buf.append(XmppStringUtils.escapeForXML(item));
//           buf.append("</V>");
//        }
//        buf.append("</SEXUALINTERESTS>");
//
//        buf.append("<PRIVATEPHOTOS>");
//        for (String item:getPrivatePhotos()) {
//           buf.append("<V>");
//           buf.append(XmppStringUtils.escapeForXML(item));
//           buf.append("</V>");
//        }
//        buf.append("</PRIVATEPHOTOS>");
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