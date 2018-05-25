package com.agmbat.imsdk.asmack.roster;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.imsdk.asmack.MessageManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.api.OnFetchContactListener;
import com.agmbat.imsdk.asmack.api.OnFetchLoginUserListener;
import com.agmbat.imsdk.asmack.api.OnSaveUserInfoListener;
import com.agmbat.imsdk.asmack.api.XMPPApi;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.imsdk.imevent.ContactGroupLoadEvent;
import com.agmbat.imsdk.imevent.ContactListUpdateEvent;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.imevent.PresenceSubscribeEvent;
import com.agmbat.imsdk.imevent.PresenceSubscribedEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.log.Debug;
import com.agmbat.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人管理, 将所有联系加载到内存中
 */
public class RosterManager {


    private static final String TAG = RosterManager.class.getSimpleName();

    /**
     * 好友分组
     */
    public static final String GROUP_FRIENDS = "Hotlist";

    /**
     * 管理好友列表
     */
    private final Roster mRoster;
    private final RosterListenerAdapter mRosterListener = new RosterListenerAdapter();


    private final Connection mConnection;


    /**
     * 所有好友分组列表
     */
    private List<ContactGroup> mGroupList;

    /**
     * 缓存, 此map可以不用更新
     */
    private Map<String, ContactInfo> CONTACT_MAP = new HashMap<>();


    /**
     * 是否已从缓存中加载
     */
    private boolean mCacheLoaded = false;

    /**
     * 是否已人服务器加载
     */
    private boolean mNetworkLoaded = false;

    /**
     * 当前登陆用户
     */
    private LoginUser mLoginUser;

    /**
     * 好友申请列表
     */
    private List<ContactInfo> mFriendRequestList;


    public RosterManager(Connection connection, Roster roster) {
        mRoster = roster;
        roster.setSubscriptionMode(SubscriptionMode.manual);
        Log.i("Delete", "Subscription mode change to : " + roster.getSubscriptionMode());
        roster.addRosterListener(mRosterListener);
        mConnection = connection;
        mGroupList = new ArrayList<>();
        registerLoginEvent();
    }

    /**
     * TODO 修改函数名，加上缓存信息
     * 从内存缓存中获取联系人信息
     *
     * @param jid
     * @return
     */
    public ContactInfo getContactInfo(String jid) {
        return CONTACT_MAP.get(jid);
    }

    public void addContactInfo(ContactInfo contactInfo) {
        addContactInfo(contactInfo.getBareJid(), contactInfo);
    }

    public void addContactInfo(String jid, ContactInfo contactInfo) {
        CONTACT_MAP.put(jid, contactInfo);
    }

    /**
     * 我的好友分组, 默认存在一个分组
     *
     * @return
     */
    private ContactGroup getFriendGroup() {
        return RosterHelper.findContactGroup(GROUP_FRIENDS, mGroupList);
    }

    /**
     * 注册登陆成功事件
     */
    private void registerLoginEvent() {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void loginSuccessful() {
                        // 登陆成功后刷新登陆用户信息
                        refreshLoginUserInfo();
                        // 登录成功后重新刷新一次Roster
                        mRoster.reload();
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
     * 获取所有分组
     *
     * @return
     */
    public List<ContactGroup> getContactGroupList() {
        return mGroupList;
    }

    /**
     * 请求添加好友
     *
     * @param contact
     * @return
     */
    public boolean requestAddContactToFriend(ContactInfo contact) {
        mRoster.sendSubscribe(contact.getBareJid());
        return true;
    }

    /**
     * 添加好友到朋友分组中
     *
     * @param contact
     * @return
     */
    public boolean addContactToFriend(ContactInfo contact) {
        return addContact(contact, getFriendGroup().getGroupName());
    }


    /**
     * 添加好友
     * 一个用户只能添加到一个组里
     *
     * @param contact
     * @param group
     * @return
     */
    public boolean addContact(ContactInfo contact, String group) {
        try {
            createGroup(group);
            mRoster.addContact(contact.getBareJid(), contact.getNickName(),
                    contact.getRemark(), "", "", 0,
                    0, false, new String[]{group});
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除联系人
     *
     * @param contact
     * @return
     */
    public boolean deleteContact(ContactInfo contact) {
        boolean success = false;
        try {
            RosterEntry entry = mRoster.getEntry(contact.getBareJid());
            mRoster.removeEntry(entry);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 创建组,确保组存在
     *
     * @param groups
     */
    private void createGroups(String[] groups) {
        for (String groupName : groups) {
            createGroup(groupName);
        }
    }

    /**
     * 创建分组
     *
     * @param groupname
     */
    public void createGroup(String groupname) {
        if (null == mRoster.getGroup(groupname)) {
            mRoster.createGroup(groupname);
        }
    }


    @Deprecated
    public List<ContactInfo> getContactList() {
        Collection<RosterEntry> list = mRoster.getEntries();
        List<ContactInfo> contactList = new ArrayList<ContactInfo>(list.size());
        for (RosterEntry entry : list) {
            contactList.add(getContactFromRosterEntry(entry));
        }
        return contactList;
    }

    @Deprecated
    public List<String> getGroupsNames() {
        Collection<RosterGroup> groups = mRoster.getGroups();
        List<String> result = new ArrayList<String>(groups.size());
        for (RosterGroup rosterGroup : groups) {
            result.add(rosterGroup.getName());
        }
        return result;
    }

    /**
     * 更新某个好友在本列表中的昵称，该更改会同步到服务器。 需注意，该方法不是更改好友的真实名字。
     */
    public void changeNickName(String jid, String nickName) {
        RosterEntry rosterEntry = mRoster.getEntry(jid);
        if (null != rosterEntry) {
            rosterEntry.changeNickName(nickName);
        }
    }

    @Deprecated
    public void addContactToGroup(String groupName, String jid) {
        createGroup(groupName);
        RosterGroup group = mRoster.getGroup(groupName);
        try {
            group.addEntry(mRoster.getEntry(jid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void removeContactFromGroup(String groupName, String jid) {
        RosterGroup group = mRoster.getGroup(groupName);
        try {
            if (null != group) {
                RosterEntry rosterEntry = mRoster.getEntry(jid);
                if (null != rosterEntry) {
                    group.removeEntry(rosterEntry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//
//    public boolean isContactExit(String jid) {
//        return mRoster.contains(jid);
//    }
//
//    public boolean isGroupExist(String groupName) {
//        boolean exist = false;
//        if (!TextUtils.isEmpty(groupName)) {
//            exist = null != mRoster.getGroup(groupName);
//        }
//        return exist;
//    }
//
//    public int getContactCount() {
//        return mRoster.getEntryCount();
//    }
//
//    public int getGroupCount() {
//        return mRoster.getGroupCount();
//    }
//
//    public ContactInfo ensureContactInformation(String loginUserName, ContactInfo contactInfo) {
//        if (isFriend(contactInfo)) {// 在好友列表中存在，证明是好友
//            contactInfo = getContact(contactInfo.getBareJid());
//        } else {// 在好友列表中不存在，证明不是好友
//            if (mStrangersCache.containsKey(contactInfo.getBareJid())) {
//                Log.d(TAG,
//                        "===> Ensure Information from memory, cache size:" + mStrangersCache.size());
//                contactInfo = mStrangersCache.get(contactInfo.getBareJid());
//            } else {
//                Profile profile = getContactProfile(contactInfo.getBareJid());
//                if (null != profile) {
//                    contactInfo = new ContactInfo(profile);
//                    contactInfo.setAccount(loginUserName);
//                    mStrangersCache.put(contactInfo.getBareJid(), contactInfo);
//                }
//            }
//        }
//        contactInfo.setAccount(loginUserName);
//        RobotsManager.getInstance().ensureRobotsLocation(contactInfo);
//        Log.d(TAG, "===> Ensure Information :: jid=" + contactInfo.getBareJid() + ",avatarId="
//                + contactInfo.getAvatarId() + ",account=" + contactInfo.getAccount() + ",name="
//                + contactInfo.getName() + ",nickName=" + contactInfo.getNickName());
//        return contactInfo;
//    }
//
//    private final HashMap<String, ContactInfo> mStrangersCache = new HashMap<String, ContactInfo>();
//
//    private boolean isFriend(ContactInfo contactInfo) {
//        boolean isFriend = false;
//        if (isContactExit(contactInfo.getBareJid())) {
//            isFriend = true;
//        }
//        return isFriend;
//    }
//
//    public Profile getContactProfile(String jid) {
//        Profile profile = null;
//        if (mConnection.isAuthenticated() && !TextUtils.isEmpty(jid)) {
//            try {
//                DiscoverInfo profileRequest = new DiscoverInfo();
//                profileRequest.setTo(jid);
//                profileRequest.setType(Type.GET);
//                PacketCollector collector = mConnection.createPacketCollector(new PacketIDFilter(
//                        profileRequest.getPacketID()));
//                mConnection.sendPacket(profileRequest);
//                IQ response = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
//                // Cancel the collector.
//                collector.cancel();
//                if (response != null && response.getError() == null) {
//                    DiscoverInfo discoverInfo = (DiscoverInfo) response;
//                    profile = (Profile) discoverInfo.getExtension(Profile.ELEMENT,
//                            Profile.NAMESPACE);
//                    if (null != profile) {
//                        profile.setAccount(mLoginUserName);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return profile;
//    }


    /**
     * 接受申请人请求成为好友,同意加对话为好友
     *
     * @param contactInfo
     */
    public void acceptFriend(ContactInfo contactInfo) {
        // 存入数据库（好友表+分组表）
        addContactToFriend(contactInfo);
        mRoster.sendSubscribed(contactInfo.getBareJid());
    }


    /**
     * Listener for the roster events. It will call the remote listeners registered.
     */
    private class RosterListenerAdapter implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            Log.d(TAG, "[RosterListenerAdapter][entriesAdded]:" + addresses);
            // 登陆成功后刷新一次服务器数据

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Collection<RosterGroup> entryCollection = mRoster.getGroups();
                    refreshContactList(entryCollection);
                }
            }).start();


//            for (final String address : addresses) {
//                com.agmbat.log.Log.d(TAG, "==>onEntriesAdded(Thread): [address=" + address);
//                String bareAddress = XmppStringUtils.parseBareAddress(address);
//                String userName = XmppStringUtils.parseName(address);
//                XMPPApi.fetchContactInfo(address, new OnFetchContactListener() {
//                    @Override
//                    public void onFetchContactInfo(ContactInfo contactInfo) {
//                        if (contactInfo != null) {
//                            com.agmbat.log.Log.d("contact:" + contactInfo.toString());
//                            saveOrUpdateContact(contactInfo);
//                        }
//                        if (last.equals(address)) {
//                            // 最后一个需要发送消息到UI
//                            updateGroupList();
//                            EventBus.getDefault().post(new ContactUpdateEvent());
//                        }
//                    }
//                });
//            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
//            for (String address : addresses) {
//                Log.d(TAG, "==>onEntriesDeleted(Thread):" + address);
//                deleteContactFromTable(XmppStringUtils.parseName(address));
//            }
            List<String> tab = new ArrayList<String>(addresses);
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
//            for (String address : addresses) {
//                ContactInfo contact = getContact(address);
//                if (null != contact) {
//                    Log.d(TAG,
//                            "==>onEntriesUpdated(Thread): [UserName=" + contact.getUserName()
//                                    + ",PersonalMsg=" + contact.getPersonalMsg() + ",Name="
//                                    + contact.getName() + ",NickName=" + contact.getNickName()
//                                    + ",AvatarId=" + contact.getAvatarId() + ",Lat="
//                                    + contact.getLatitude() + ",Lon=" + contact.getLongitude()
//                                    + ",isRobot=" + contact.isRobot() + "]");
//                }
//            }
            List<String> tab = new ArrayList<String>(addresses);
        }

        @Override
        public void presenceChanged(Presence presence) {
//            PresenceAdapter presenceAdapter = new PresenceAdapter(presence);
//            ContactInfo contactInfo = getContact(XmppStringUtils.parseBareAddress(presenceAdapter
//                    .getFrom()));
//            Log.i(TAG, "==>onPresenceChanged(Thread):" + contactInfo.getBareJid() + " ==> status="
//                    + contactInfo.getStatus() + ";personalMsg=" + contactInfo.getPersonalMsg()
//                    + ";name=" + contactInfo.getName() + ";avatarId=" + contactInfo.getAvatarId());
//            if (null != contactInfo) {
//                updateContactPresence(contactInfo);
//            }
//            for (IRosterListener l : mRemoteRosListeners) {
//                l.onPresenceChanged(presenceAdapter);
//            }
        }

        @Override
        public void presenceSubscribe(Presence presence) {
            // 收到添加好友请求
            OnFetchContactListener listener = new OnFetchContactListener() {
                @Override
                public void onFetchContactInfo(final ContactInfo contactInfo) {

                    // 需要用本地数据库存为列表
                    UiUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            XMPPManager.getInstance().getRosterManager().addContactInfo(contactInfo);

                            addFriendRequest(contactInfo);
                            EventBus.getDefault().post(new PresenceSubscribeEvent(contactInfo));
                        }
                    });

                }
            };
            loadContactFromPresence(presence, listener);
        }

        @Override
        public void presenceSubscribed(Presence presence) {
            Log.d(TAG, "presenceSubscribed:" + presence);
            // 收到对方同意加我为好友
            OnFetchContactListener listener = new OnFetchContactListener() {
                @Override
                public void onFetchContactInfo(final ContactInfo contactInfo) {
                    // 需要用本地数据库存为列表
                    final boolean success = addContactToFriend(contactInfo);
                    if (success) {
                        // 保存到数据库缓存中
                        contactInfo.setGroupName(getFriendGroup().getGroupName());
                        ContactDBCache.saveOrUpdateContact(contactInfo);
                    }
                    UiUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            // 添加到内存中
                            XMPPManager.getInstance().getRosterManager().addContactInfo(contactInfo);

                            // 添加对方为好友
                            if (success) {
                                getFriendGroup().addContact(contactInfo);
                                //   将mGroupList保存一次
                                EventBus.getDefault().post(new PresenceSubscribedEvent(contactInfo));
                                EventBus.getDefault().post(new ContactListUpdateEvent(mGroupList));
                            }
                        }
                    });

                }
            };
            loadContactFromPresence(presence, listener);
        }
    }


    private void loadContactFromPresence(Presence presence, OnFetchContactListener l) {
        String from = presence.getFrom();
        // 由于在缓存中获取的用户信息不全，需要重新拉到一次信息
        ContactInfo contactInfo = null; // getContact(from);
        // 这里基本上都为空,
        if (contactInfo == null) {
            XMPPApi.fetchContactInfo(from, l);
        } else {
            if (l != null) {
                l.onFetchContactInfo(contactInfo);
            }
        }
    }


    /**
     * Get a contact from a RosterEntry.
     *
     * @param entry a roster entry containing information for the contact.
     * @return a contact for this entry.
     */
    private ContactInfo getContactFromRosterEntry(RosterEntry entry) {
        String user = entry.getUser();
        ContactInfo contact = new ContactInfo();
        contact.setBareJid(user);
        Presence presence = mRoster.getPresence(user);
//        contact.setStatus(presence);
//        contact.setGroups(entry.getGroups());
//        Iterator<Presence> iPres = mRoster.getPresences(user);
//        while (iPres.hasNext()) {
//            presence = iPres.next();
//            if (!presence.getType().equals(Presence.Type.unavailable)) {
//                contact.addResource(XmppStringUtils.parseResource(presence.getFrom()));
//            }
//        }
        contact.setNickname(entry.getName());
        contact.setRemark(entry.getNickName());
        contact.setAvatar(entry.getAvatarId());

//        contact.setPersonalMsg(entry.getPersonalMsg());
//        contact.setLatitude(entry.getLatitude());
//        contact.setLongitude(entry.getLongitude());
//        contact.setRobot(entry.isRobot());
//        contact.setAccount(mLoginUserName);
        return contact;
    }


    /**
     * 将group list 中的联系人添加到缓存中
     *
     * @param result
     */
    public void addGroupList(List<ContactGroup> result) {
        for (ContactGroup group : result) {
            for (ContactInfo info : group.getContactList()) {
                addContactInfo(info);
            }
        }
    }

    /**
     * 加载缓存中联系人列表
     */
    public void loadContactGroupFromDB() {
        if (mCacheLoaded) {
            EventBus.getDefault().post(new ContactGroupLoadEvent(mGroupList));
            return;
        }
        mCacheLoaded = true;
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, List<ContactGroup>>() {
            @Override
            protected List<ContactGroup> doInBackground(Void... voids) {
                return ContactDBCache.getGroupList();
            }

            @Override
            protected void onPostExecute(List<ContactGroup> result) {
                super.onPostExecute(result);
                if (!mNetworkLoaded) {
                    mGroupList.addAll(result);
                    addGroupList(result);
                    EventBus.getDefault().post(new ContactGroupLoadEvent(mGroupList));
                }
            }
        });
    }

    public void loadContactGroupFromDBSync() {
        if (mCacheLoaded) {
            EventBus.getDefault().post(new ContactGroupLoadEvent(mGroupList));
            return;
        }
        mCacheLoaded = true;
        List<ContactGroup> result = ContactDBCache.getGroupList();
        if (!mNetworkLoaded) {
            mGroupList.addAll(result);
            addGroupList(result);
            EventBus.getDefault().post(new ContactGroupLoadEvent(mGroupList));
        }
    }


    /**
     * 刷新登陆用户信息
     */
    private void refreshLoginUserInfo() {
        String loginUserJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        XMPPApi.fetchLoginUser(loginUserJid, new OnFetchLoginUserListener() {
            @Override
            public void onFetchLoginUser(LoginUser user) {
                if (!user.isValid()) {
                    Debug.printStackTrace();
                    return;
                }

                mLoginUser = user;
                EventBus.getDefault().post(new LoginUserUpdateEvent(mLoginUser));
                UiUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        ContactInfo contactInfo = new ContactInfo();
                        contactInfo.setBareJid(mLoginUser.getJid());
                        contactInfo.setAvatar(mLoginUser.getAvatar());
                        contactInfo.setNickname(mLoginUser.getNickname());
                        contactInfo.setGender(mLoginUser.getGender());
                        XMPPManager.getInstance().getRosterManager().addContactInfo(contactInfo);
                    }
                });
            }

        });
    }

    /**
     * 添加申请用户列表, 有新的朋友添加我为好友
     *
     * @param willToAdd
     */
    private void addFriendRequest(ContactInfo willToAdd) {
        MeetDatabase.getInstance().saveFriendRequest(willToAdd);
        ContactInfo exist = getFriendRequest(willToAdd.getBareJid());
        if (exist == null) {
            mFriendRequestList.add(willToAdd);
        }
    }

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


    /**
     * 获取登陆用户信息
     *
     * @return
     */
    public LoginUser getLoginUser() {
        return mLoginUser;
    }


    /**
     * 保存登陆用户信息
     *
     * @param user
     */
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

//    /**
//     *
//     /**
//     * 获取好友申请列表
//     *
//     * @return
//     */
//    public List<ContactInfo> getFriendRequestList();
//
//    /**
//     * 获取申请人信息
//     *
//     * @param jid
//     * @return
//     */
//    public ContactInfo getFriendRequest(String jid);

//    /**
//     * 同意添加自己为好友
//     *
//     * @param contactInfo
//     */
//    public void acceptFriend(ContactInfo contactInfo);


//    /**
//     * 删除好友申请消息
//     *
//     * @param contactInfo
//     */
//    void removeFriendRequest(ContactInfo contactInfo);
//
//    /**
//     * 加载所有好友分组
//     *
//     * @param l
//     */
//    void loadContactGroup(OnLoadContactGroupListener l);


    /**
     * 刷新一次联系人列表
     *
     * @param entryCollection
     */
    private void refreshContactList(Collection<RosterGroup> entryCollection) {
        // 将列表同步到内存中

        // 删除不存在好友及聊天记录
        // 新列表
        List<ContactInfo> newList = genContactList(entryCollection);

        // 将列表同步到数据库中
        ContactDBCache.saveAndClearOldList(newList);

        // 旧的列表
        List<ContactInfo> oldList = RosterHelper.toContactList(mGroupList);

        // 查找需要删除的列表
        List<ContactInfo> deleteList = findDeleteList(newList, oldList);

        // 删除所有不是好友的聊天记录
        MessageManager messageManager = XMPPManager.getInstance().getMessageManager();
        String loginUserId = mConnection.getBareJid();
        for (ContactInfo contactInfo : deleteList) {
            messageManager.deleteMessage(loginUserId, contactInfo.getBareJid());
        }

        mGroupList.clear();
        mGroupList.addAll(RosterHelper.toGroupList(newList));
        addGroupList(mGroupList);

    }


    /**
     * 查找old列表中被删除的item项目
     *
     * @param newList
     * @param oldList
     * @return
     */
    private static List<ContactInfo> findDeleteList(List<ContactInfo> newList, List<ContactInfo> oldList) {
        List<ContactInfo> toDelete = new ArrayList<>();
        for (ContactInfo old : oldList) {
            ContactInfo exist = RosterHelper.findContactInfo(old.getBareJid(), newList);
            if (exist == null) {
                // 如果在新列表中找不到对应的item, 则表示需要删除
                toDelete.add(old);
            }
        }
        return toDelete;
    }


    /**
     * 将所有的用户转成列表
     *
     * @param entryCollection
     * @return
     */
    private List<ContactInfo> genContactList(Collection<RosterGroup> entryCollection) {
        List<ContactInfo> list = new ArrayList<>();
        for (RosterGroup group : entryCollection) {
            for (RosterEntry entry : group.getEntries()) {
                // 直接从服务器拉取
                ContactInfo contactInfo = RosterHelper.loadContactInfo(entry.getUser());
                contactInfo.setGroupName(group.getName());
                list.add(contactInfo);
            }
        }
        return list;
    }


    /**
     * 获取联系人信息，基本上信息为空
     *
     * @param jid
     * @return
     */
    @Deprecated
    private ContactInfo getContact(String jid) {
        if (mRoster.contains(jid)) {
            return getContactFromRosterEntry(mRoster.getEntry(jid));
        }
        return null;
    }
}
