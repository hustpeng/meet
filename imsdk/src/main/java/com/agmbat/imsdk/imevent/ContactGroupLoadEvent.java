package com.agmbat.imsdk.imevent;

import com.agmbat.imsdk.asmack.roster.ContactGroup;

import java.util.List;

/**
 * 联系人加载成功的事件
 */
public class ContactGroupLoadEvent {

    private List<ContactGroup> mDataList;

    public ContactGroupLoadEvent(List<ContactGroup> list) {
        mDataList = list;
    }

    public List<ContactGroup> getDataList() {
        return mDataList;
    }
}
