package com.agmbat.imsdk.imevent;


import com.agmbat.imsdk.asmack.roster.ContactGroup;

import java.util.List;

/**
 * 联系人列表有更新事件
 */
public class ContactListUpdateEvent {

    private List<ContactGroup> mList;

    public ContactListUpdateEvent(List<ContactGroup> list) {
        mList = list;
    }

    public List<ContactGroup> getList() {
        return mList;
    }
}
