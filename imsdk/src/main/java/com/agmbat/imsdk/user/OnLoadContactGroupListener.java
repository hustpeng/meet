package com.agmbat.imsdk.user;

import com.agmbat.imsdk.data.ContactGroup;

import java.util.List;

/**
 * 获取好友组
 */
public interface OnLoadContactGroupListener {

    public void onLoad(List<ContactGroup> list);
}
