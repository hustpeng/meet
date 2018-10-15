package com.agmbat.meetyou.group;

/**
 * EventBus事件：更改群资料
 */
public class EditGroupEvent {

    private String groupJid;

    private String groupName;

    private String headline;

    private String description;

    private String avatar;

    private int categoryId;

    private boolean needVerify;

    public EditGroupEvent(String groupJid) {
        this.groupJid = groupJid;
    }

    public String getGroupJid() {
        return groupJid;
    }

    public void setGroupJid(String groupJid) {
        this.groupJid = groupJid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isNeedVerify() {
        return needVerify;
    }

    public void setNeedVerify(boolean needVerify) {
        this.needVerify = needVerify;
    }
}
