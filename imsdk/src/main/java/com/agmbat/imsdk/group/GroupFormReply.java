package com.agmbat.imsdk.group;

import com.agmbat.imsdk.search.group.GroupCategory;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

public class GroupFormReply extends IQ {

    private String name;

    private String headline;

    private String description;

    private String avatar;

    private boolean needVerify;

    private int categoryId;

    private List<GroupCategory> categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isNeedVerify() {
        return needVerify;
    }

    public void setNeedVerify(boolean needVerify) {
        this.needVerify = needVerify;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<GroupCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<GroupCategory> categories) {
        this.categories = categories;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
