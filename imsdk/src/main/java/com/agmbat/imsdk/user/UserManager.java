package com.agmbat.imsdk.user;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.imsdk.IUserManager;
import com.agmbat.imsdk.asmack.RosterManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.MeetDatabase;

import java.util.List;

/**
 * 提供给UI层, 用于用户信息管理
 * 用户管理
 */
public class UserManager implements IUserManager {

    private RosterManager mRosterManager;

    private UserManager() {
        mRosterManager = XMPPManager.getInstance().getRosterManager();
    }

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

    @Override
    public void removeFriendRequest(ContactInfo contactInfo) {
        MeetDatabase.getInstance().deleteFriendRequest(contactInfo);
        ContactInfo exist = getFriendRequest(contactInfo.getBareJid());
        if (exist != null) {
            mFriendRequestList.remove(exist);
        }
    }

    /**
     * 对UI层
     *
     * @return
     */
    @Override
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
    @Override
    public ContactInfo getFriendRequest(String jid) {
        List<ContactInfo> list = getFriendRequestList();
        for (ContactInfo contactInfo : list) {
            if (contactInfo.getBareJid().equals(jid)) {
                return contactInfo;
            }
        }
        return null;
    }

    /**
     * 接受申请人请求成为好友
     *
     * @param contactInfo
     */
    @Override
    public void acceptFriend(ContactInfo contactInfo) {
        // 存入数据库（好友表+分组表）
        mRosterManager.acceptFriend(contactInfo);
    }

    @Override
    public boolean requestAddContactToFriend(ContactInfo contactInfo) {
        return mRosterManager.addContactToFriend(contactInfo);
    }

    @Override
    public void loadContactGroup(final OnLoadContactGroupListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, List<ContactGroup>>() {
            @Override
            protected List<ContactGroup> doInBackground(Void... voids) {
                List<ContactInfo> list2=  mRosterManager.getContactList();
                return MeetDatabase.getInstance().getGroupList("");
            }

            @Override
            protected void onPostExecute(List<ContactGroup> result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onLoad(result);
                }
            }
        });
    }

}
