package org.jivesoftware.smackx.token;

import org.jivesoftware.smackx.db.ICacheStoreObject;

public class TokenObject implements ICacheStoreObject {

    private String token;
    private long expirdTime;

    @Override
    public String getKey() {
        return "";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpirdTime() {
        return expirdTime;
    }

    public void setExpirdTime(long expirdTime) {
        this.expirdTime = expirdTime;
    }

    @Override
    public String toString() {
        return "token:" + token + " expird:" + expirdTime;
    }
}