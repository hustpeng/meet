package com.agmbat.meetyou.tab.found;


import com.agmbat.imsdk.data.ContactInfo;

import java.util.List;

/**
 * 发现item
 */
public class FoundGroup {

    /**
     * 标题
     */
    private String mTitle;

    /**
     * 联系人列表
     */
    private List<ContactInfo> mUserList;

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setUserList(List<ContactInfo> list) {
        mUserList = list;
    }

    public List<ContactInfo> getUserList() {
        return mUserList;
    }

}