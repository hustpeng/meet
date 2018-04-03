/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.favoritedme;

import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

public class FavoritedMeObject implements ICacheStoreObject, Comparable<FavoritedMeObject>{

    private String jid;
    private String nickname;
    private String avatar;
    private String status;
    private double lon;
    private double lat;
    private boolean isOnline;
    private String subscription;
    private long create_date;
    private boolean isNew;
    /**
     * just let me know "who need go delete from readFlagStorage</br>
     * use in VisitorMeObject.java
     */
    private boolean isRemoveFromReadFlag;
    public String getKey() {
        if (jid != null) {
            return jid.toLowerCase();
        }

        return "";
    }

    public String getJid() {
        if (jid != null) {
            return jid;
        }

        return "";
    }
    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getBareJid()
    {
        if (jid != null) {
            return XmppStringUtils.parseBareAddress(jid);
        }

        return "";
    }

    public String getNickname() {
        if (nickname != null) {
            return nickname;
        }

        return "";
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        if (avatar != null) {
            return avatar;
        }

        return "";
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public boolean isOnline() {
        return isOnline;
    }
    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getSubscription() {
        if (subscription != null) {
            return subscription;
        }

        return "";
    }
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getStatus() {
        if (status != null) {
            return status;
        }

        return "";
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public static String getXmlNode(FavoritedMeObject object)
    {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("FavoritedMeObject: ");

        buf.append("[jid = ");
        buf.append(object.jid);
        buf.append("] ");

        buf.append("[nickname = ");
        buf.append(object.nickname);
        buf.append("] ");

        buf.append("[avatar = ");
        buf.append(object.avatar);
        buf.append("] ");

        buf.append("[status = ");
        buf.append(object.status);
        buf.append("] ");

        buf.append("[lat = ");
        buf.append(object.lat);
        buf.append("] ");

        buf.append("[lon = ");
        buf.append(object.lon);
        buf.append("] ");

        buf.append("[isOnline = ");
        buf.append(object.isOnline);
        buf.append("] ");

        buf.append("[subscription = ");
        buf.append(object.subscription);
        buf.append("] ");

        buf.append("[create_date = ");
        buf.append(object.create_date);
        buf.append("] ");

        buf.append("[isNew = ");
        buf.append(object.isNew);
        buf.append("] ");

        return buf.toString();
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }

    @Override
    public int compareTo(FavoritedMeObject another) {
        if (this.create_date < another.create_date)
            return 1;
        else if (this.create_date > another.create_date)
            return -1;
        else
            return 0;
    }
    public boolean isRemoveFromReadFlag() {
        return isRemoveFromReadFlag;
    }

    public void setRemoveFromReadFlag(boolean isRemoveFromReadFlag) {
        this.isRemoveFromReadFlag = isRemoveFromReadFlag;
    }
}