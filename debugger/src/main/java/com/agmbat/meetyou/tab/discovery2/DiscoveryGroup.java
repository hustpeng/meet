package com.agmbat.meetyou.tab.discovery2;


import com.agmbat.imsdk.asmack.roster.ContactInfo;

import java.util.List;

/**
 * 发现item
 */
public class DiscoveryGroup {

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
