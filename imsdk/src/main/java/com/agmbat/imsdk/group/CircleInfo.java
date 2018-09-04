package com.agmbat.imsdk.group;

import java.io.Serializable;

public class CircleInfo implements Serializable {

    private String groupJid;

    private int members;

    private String name;

    private String avatar;

    private String ownerJid;

    public CircleInfo(String groupJid, String name){
        this.groupJid = groupJid;
        this.name = name;
    }

    public String getGroupJid() {
        return groupJid;
    }

    public void setGroupJid(String groupJid) {
        this.groupJid = groupJid;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOwnerJid() {
        return ownerJid;
    }

    public void setOwnerJid(String ownerJid) {
        this.ownerJid = ownerJid;
    }
}
