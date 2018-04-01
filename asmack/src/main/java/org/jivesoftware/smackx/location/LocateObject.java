
package org.jivesoftware.smackx.location;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

import android.text.TextUtils;

import java.util.Date;

public class LocateObject implements ICacheStoreObject{

    private String jid;
    private String locationStr;
    private double lat;
    private double lon;
    private Date update_date;

    @Override
    public String getKey() {
        if (TextUtils.isEmpty(jid)) {
            return "";
        }

        return jid.toLowerCase();
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getLocationStr() {
        return locationStr;
    }

    public void setLocationStr(String locationStr) {
        this.locationStr = locationStr;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }


    public static String getXmlNode(LocateObject object)
    {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(LocateProvider.elementName());
        buf.append(" xmlns=\"");
        buf.append(LocateProvider.namespace());
        buf.append("\">");

        buf.append("<lat>");
        buf.append(String.valueOf(object.getLat()));
        buf.append("</lat>");

        buf.append("<lon>");
        buf.append(String.valueOf(object.getLon()));
        buf.append("</lon>");

        buf.append("<str>");
        if (!TextUtils.isEmpty(object.getLocationStr())) {
            buf.append(StringUtils.escapeForXML(object.getLocationStr()));
        }
        buf.append("</str>");

        buf.append("</");
        buf.append(LocateProvider.elementName());
        buf.append(">");

        return buf.toString();
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }
}