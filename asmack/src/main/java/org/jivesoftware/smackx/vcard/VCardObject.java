
package org.jivesoftware.smackx.vcard;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

import android.text.TextUtils;

import java.util.Date;

public class VCardObject implements ICacheStoreObject{

    private String jid;
    private String avatar;
    private String nickname;
    private String status;
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

    public String getAvatar() {
        if (TextUtils.isEmpty(avatar)) {
            return "";
        }

        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        if (TextUtils.isEmpty(nickname)) {
            return "";
        }
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public static String getXmlNode(VCardObject object)
    {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(VCardProvider.elementName());
        buf.append(" xmlns=\"");
        buf.append(VCardProvider.namespace());
        buf.append("\">");

        buf.append("<NICKNAME>");
        if (!TextUtils.isEmpty(object.getNickname())) {
            buf.append(StringUtils.escapeForXML(object.getNickname()));
        }
        buf.append("</NICKNAME>");

        buf.append("<AVATAR>");
        if (!TextUtils.isEmpty(object.getAvatar())) {
            buf.append(StringUtils.escapeForXML(object.getAvatar()));
        }
        buf.append("</AVATAR>");

        buf.append("<STATUS>");
        if (!TextUtils.isEmpty(object.getStatus())) {
            buf.append(StringUtils.escapeForXML(object.getStatus()));
        }
        buf.append("</STATUS>");

        buf.append("</");
        buf.append(VCardProvider.elementName());
        buf.append(">");
        return buf.toString();
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }
}