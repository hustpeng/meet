/**
 * $RCSfile$
 * $Revision$
 * $Date$
 * <p>
 * Copyright 2003-2007 Jive Software.
 * <p>
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.visitor;

import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.ICacheStoreObject;

public class VisitorMeObject implements ICacheStoreObject, Comparable<VisitorMeObject> {
    private String jid;
    private String nickname;
    private String avatar;
    private String status;
    private double lon;
    private double lat;
    private boolean isOnline;
    private String entrance;
    private long visitorTime;
    private boolean isNew;
    /**
     * just let me know "who need go delete from readFlagStorage</br>
     * use in VisitorMeObject.java
     */
    private boolean isRemoveFromReadFlag;

    public static String getXmlNode(VisitorMeObject object) {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("VisitorMeObject: ");

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

        buf.append("[visitorTime = ");
        buf.append(object.visitorTime);
        buf.append("] ");

        buf.append("[entrance = ");
        buf.append(object.entrance);
        buf.append("] ");

        buf.append("[isNew = ");
        buf.append(object.isNew);
        buf.append("] ");

        return buf.toString();
    }

    public String getKey() {
        if (jid != null && entrance != null) {
            return String.format("%s%s", jid.toLowerCase(), entrance.toLowerCase());
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

    public String getBareJid() {
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

    public String getStatus() {
        if (status != null) {
            return status;
        }

        return "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public long getVisitorTime() {
        return visitorTime;
    }

    public void setVisitorTime(long visitorTime) {
        this.visitorTime = visitorTime;
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }

    @Override
    public int compareTo(VisitorMeObject another) {
        if (this.visitorTime < another.visitorTime)
            return 1;
        else if (this.visitorTime > another.visitorTime)
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

    public enum VisitorEntrance {
        nearby,
        promote,
        hunkgame,
        hunk,
        hunkme,
        fan,
        fanme,
        visitor,
        message,
        chat,
        gchat,
        gmember,
        gnews,
        newstuds,
        topstuds,
        unknow;

        public static VisitorEntrance fromString(String name) {
            try {
                return VisitorEntrance.valueOf(name);
            } catch (Exception e) {
                return unknow;
            }
        }
    }
}