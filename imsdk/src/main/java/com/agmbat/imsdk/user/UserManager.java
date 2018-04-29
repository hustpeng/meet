package com.agmbat.imsdk.user;

import android.util.Log;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.imsdk.IUserManager;
import com.agmbat.imsdk.asmack.RosterManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.api.OnFetchLoginUserListener;
import com.agmbat.imsdk.asmack.api.OnSaveUserInfoListener;
import com.agmbat.imsdk.asmack.api.XMPPApi;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.ContactGroupBelong;
import com.agmbat.imsdk.db.ContactGroupBelongTableManager;
import com.agmbat.imsdk.db.ContactGroupTableManager;
import com.agmbat.imsdk.db.ContactTableManager;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供给UI层, 用于用户信息管理
 * 用户管理
 */
public class UserManager implements IUserManager {

    private RosterManager mRosterManager;

    /**
     * 当前登陆用户
     */
    private LoginUser mLoginUser;

    /**
     * 好友申请列表
     */
    private List<ContactInfo> mFriendRequestList;

    /**
     * 所有的好友
     */
    private List<ContactInfo> mContactList;

    /**
     * 所有好友分组列表
     */
    private List<ContactGroup> mGroupList;

    private static final UserManager INSTANCE = new UserManager();

    public static UserManager getInstance() {
        return INSTANCE;
    }

    private UserManager() {
        mRosterManager = XMPPManager.getInstance().getRosterManager();
        mContactList = new ArrayList<>();
        mGroupList = new ArrayList<>();

        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void loginSuccessful() {
                        // 登陆成功后刷新登陆用户信息
                        refreshLoginUserInfo();
                    }

                    @Override
                    public void connectionClosed() {

                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {

                    }
                });
            }
        });
    }

    /**
     * 刷新登陆用户信息
     */
    private void refreshLoginUserInfo() {
        String loginUserJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        XMPPApi.fetchLoginUser(loginUserJid, new OnFetchLoginUserListener() {
            @Override
            public void onFetchLoginUser(LoginUser user) {
                mLoginUser = user;
                EventBus.getDefault().post(new LoginUserUpdateEvent(mLoginUser));
            }

        });
    }

    /**
     * 添加申请用户列表, 此方法不对外暴露
     *
     * @param willToAdd
     */
    public void addFriendRequest(ContactInfo willToAdd) {
        MeetDatabase.getInstance().saveFriendRequest(willToAdd);
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
                return MeetDatabase.getInstance().getGroupList("");
            }

            @Override
            protected void onPostExecute(List<ContactGroup> result) {
                super.onPostExecute(result);
                mergeGroupList(result);
                if (l != null) {
                    l.onLoad(mGroupList);
                }
            }
        });
    }


    /**
     * 合并处理用户分组列表
     *
     * @param result
     */
    private void mergeGroupList(List<ContactGroup> result) {
        mGroupList.addAll(result);
    }

    /**
     * @param contactInfo
     */
    public void saveOrUpdateContact(ContactInfo contactInfo) {
        ContactTableManager.saveOrUpdateContact(contactInfo);
        ContactInfo exist = findExistContactInfo(contactInfo.getBareJid());
        if (exist != null) {
            exist.apply(contactInfo);
        } else {
            mContactList.add(contactInfo);
        }
    }

    /**
     * 更新组信息
     */
    public void updateGroupList() {
        List<RosterGroup> list = mRosterManager.getGroupList();
        for (RosterGroup rosterGroup : list) {
            ContactGroup exist = findExitContactGroup(rosterGroup.getName());
            if (exist == null) {
                ContactGroup item = new ContactGroup(rosterGroup.getName());
                ContactGroupTableManager.save(item);
                for (RosterEntry entry : rosterGroup.getEntries()) {
                    ContactInfo contactInfo = findExistContactInfo(entry.getUser());
                    if (contactInfo == null) {
                        Log.e("updateGroupList", "updateGroupList error");
                    } else {
                        ContactGroupBelong belong = new ContactGroupBelong();
                        belong.mGroupId = item.getGroupId();
                        belong.mUserJid = contactInfo.getBareJid();
                        ContactGroupBelongTableManager.save(belong);

                        // 更新list
                        item.addContact(contactInfo);
                    }
                }
                mGroupList.add(item);
            } else {
                // 更新组信息
                // TODO

            }
        }
    }

    private ContactGroup findExitContactGroup(String groupName) {
        for (ContactGroup exist : mGroupList) {
            if (exist.getGroupName().equals(groupName)) {
                return exist;
            }
        }
        return null;
    }

    /**
     * 查找已存在的联系人
     *
     * @param jid
     * @return
     */
    private ContactInfo findExistContactInfo(String jid) {
        for (ContactInfo exist : mContactList) {
            if (exist.getBareJid().equals(jid)) {
                return exist;
            }
        }
        return null;
    }


    @Override
    public LoginUser getLoginUser() {
        return mLoginUser;
    }


    @Override
    public void saveLoginUser(LoginUser user) {
        // 先通知UI变化
        EventBus.getDefault().post(new LoginUserUpdateEvent(user));
        XMPPApi.saveUserInfo(user, new OnSaveUserInfoListener() {
            @Override
            public void onSaveUserInfo(LoginUser user) {
                EventBus.getDefault().post(new LoginUserUpdateEvent(user));
            }
        });
    }
}
