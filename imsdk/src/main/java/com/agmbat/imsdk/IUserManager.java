package com.agmbat.imsdk;

import com.agmbat.imsdk.data.ContactInfo;

import java.util.List;

public interface IUserManager {

    /**
     * 获取好友申请列表
     *
     * @return
     */
    public List<ContactInfo> getFriendRequestList();

    /**
     * 获取申请人信息
     *
     * @param jid
     * @return
     */
    public ContactInfo getFriendRequest(String jid);

    /**
     * 同意添加自己为好友
     *
     * @param contactInfo
     */
    public void acceptFriend(ContactInfo contactInfo);
}
