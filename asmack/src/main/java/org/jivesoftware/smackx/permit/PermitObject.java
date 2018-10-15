package org.jivesoftware.smackx.permit;

import org.jivesoftware.smackx.db.ICacheStoreObject;

public class PermitObject implements ICacheStoreObject {
    private String jid;
    private String nickname;
    private String avatar;

    private String status;

    private boolean isSectct;
    private String action;

    public String getJid() {
        if (null != jid) {
            return jid;
        }
        return "";
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNickname() {
        if (null != nickname) {
            return nickname;
        }
        return "";
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        if (null != avatar) {
            return avatar;
        }
        return "";
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        if (null != status) {
            return status;
        }
        return "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getKey() {
        if (jid != null) {
            return jid.toLowerCase();
        }

        return "";
    }

    public String getAction() {
        if (null != action) {
            return action;
        }
        return "";
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isSectct() {
        return isSectct;
    }

    public void setSectct(boolean isSectct) {
        this.isSectct = isSectct;
    }

}
