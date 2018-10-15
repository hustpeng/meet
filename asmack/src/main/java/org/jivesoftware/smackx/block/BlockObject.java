package org.jivesoftware.smackx.block;

import org.jivesoftware.smackx.db.ICacheStoreObject;

public class BlockObject implements ICacheStoreObject {

    private String jid;

    private String nickname;
    private String avatar;

    private String status;

    private boolean isSelect;

    @Override
    public String getKey() {
        if (jid != null) {
            return jid.toLowerCase();
        }

        return "";
    }

    public String getJid() {
        if (null == jid) {
            return "";
        }
        return this.jid;
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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

}
