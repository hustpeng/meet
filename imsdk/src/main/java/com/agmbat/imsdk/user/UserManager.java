package com.agmbat.imsdk.user;

import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.MeetDatabase;

import java.util.List;

/**
 * 提供给UI层, 用于用户信息管理
 * 用户管理
 */
public class UserManager {

    /**
     * 好友申请列表
     */
    private List<ContactInfo> mFriendRequestList;

    private static final UserManager INSTANCE = new UserManager();

    public static UserManager getInstance() {
        return INSTANCE;
    }

    /**
     * 添加申请用户列表, 此方法不对外暴露
     *
     * @param willToAdd
     */
    public void addFriendRequest(ContactInfo willToAdd) {
        ContactInfo exist = getFriendRequest(willToAdd.getBareJid());
        if (exist == null) {
            mFriendRequestList.add(willToAdd);
        }
    }

    /**
     * 对UI层
     *
     * @return
     */
    public List<ContactInfo> getFriendRequestList() {
        if (mFriendRequestList == null) {
            mFriendRequestList = MeetDatabase.getInstance().getFriendRequestList();
        }
        return mFriendRequestList;
    }

    /**
     * 申请人信息
     *
     * @param jid
     * @return
     */
    public ContactInfo getFriendRequest(String jid) {
        List<ContactInfo> list = getFriendRequestList();
        for (ContactInfo contactInfo : list) {
            if (contactInfo.getBareJid().equals(jid)) {
                return contactInfo;
            }
        }
        return null;
    }
}
