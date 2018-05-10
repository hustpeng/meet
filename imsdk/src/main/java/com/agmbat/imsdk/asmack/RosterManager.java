package com.agmbat.imsdk.asmack;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.imsdk.asmack.api.OnFetchContactListener;
import com.agmbat.imsdk.asmack.api.XMPPApi;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.ContactGroupBelong;
import com.agmbat.imsdk.db.ContactGroupBelongTableManager;
import com.agmbat.imsdk.db.ContactGroupTableManager;
import com.agmbat.imsdk.db.ContactTableManager;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.imsdk.imevent.ContactUpdateEvent;
import com.agmbat.imsdk.imevent.PresenceSubscribeEvent;
import com.agmbat.imsdk.user.OnLoadContactGroupListener;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.log.Log;
import com.agmbat.utils.ArrayUtils;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 管理好友列表
 */
public class RosterManager {
    /**
     * 好友分组
     */
    private static final String GROUP_FRIENDS = "Hotlist";

    /**
     * 管理好友列表
     */
    private final Roster mRoster;

    private final String mLoginUserName;
    private final Connection mConnection;


    /**
     * 所有的好友
     */
    private List<ContactInfo> mContactList;

    /**
     * 所有好友分组列表
     */
    private List<ContactGroup> mGroupList;

    /**
     * 我的好友分组, 默认存在一个分组
     */
    private ContactGroup mFriendGroup;

    public RosterManager(Connection connection, Roster roster) {
        mRoster = roster;
        roster.setSubscriptionMode(SubscriptionMode.manual);
        Log.i("Delete", "Subscription mode change to : " + roster.getSubscriptionMode());
        roster.addRosterListener(mRosterListener);
        mLoginUserName = connection.getConfiguration().getUsername();
        mConnection = connection;

        mFriendGroup = new ContactGroup();
        mFriendGroup.setGroupName(GROUP_FRIENDS);

        addRosterListener(mIRosterListener);


        mContactList = new ArrayList<>();
        mGroupList = new ArrayList<>();
        mGroupList.add(mFriendGroup);
    }


    public static final String GROUP_RECENTLY = "Recently";


    public static final String GROUP_BLOCK = "Block";

    private static final String TAG = RosterManager.class.getSimpleName();


    private final List<IRosterListener> mRemoteRosListeners = new ArrayList<IRosterListener>();
    private final RosterListenerAdapter mRosterListener = new RosterListenerAdapter();

    public static final int MAX_CONTACT_COUNT = 20;
    public static final int MAX_BLOCK_COUNT = 20;
    public static final int MAX_RECENTLY_COUNT = 20;


    public void addRosterListener(IRosterListener listen) {
        if (listen != null) {
            mRemoteRosListeners.add(listen);
        }
    }

    /**
     * 请求添加好友, requestAddContactToFriend
     * 添加好友到朋友分组中
     *
     * @param contact
     * @return
     */
    public boolean addContactToFriend(ContactInfo contact) {
        return addContact(contact, mFriendGroup.getGroupName());
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
            mRoster.createEntry(contact.getBareJid(), contact.getNickName(),
                    contact.getRemark(), "", "", 0,
                    0, false, new String[]{group});
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteContact(ContactInfo contact) {
        boolean success = false;
        try {
            RosterEntry entry = mRoster.getEntry(contact.getBareJid());
            mRoster.removeEntry(entry);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
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

    /**
     * 获取联系人信息
     *
     * @param jid
     * @return
     */
    private ContactInfo getContact(String jid) {
        if (mRoster.contains(jid)) {
            return getContactFromRosterEntry(mRoster.getEntry(jid));
        }
        return null;
    }

    public List<ContactInfo> getContactList() {
        Collection<RosterEntry> list = mRoster.getEntries();
        List<ContactInfo> contactList = new ArrayList<ContactInfo>(list.size());
        for (RosterEntry entry : list) {
            contactList.add(getContactFromRosterEntry(entry));
        }
        return contactList;
    }

    /**
     * 获取所有分组
     *
     * @return
     */
    public List<RosterGroup> getGroupList() {
        List<RosterGroup> list = new ArrayList<>();
        list.addAll(mRoster.getGroups());
        return list;
    }

    public List<String> getGroupsNames() {
        Collection<RosterGroup> groups = mRoster.getGroups();
        List<String> result = new ArrayList<String>(groups.size());
        for (RosterGroup rosterGroup : groups) {
            result.add(rosterGroup.getName());
        }
        return result;
    }

    public void removeRosterListener(IRosterListener listen) {
        if (listen != null) {
            mRemoteRosListeners.remove(listen);
        }
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

//    public PresenceAdapter getPresence(String jid) {
//        return new PresenceAdapter(mRoster.getPresence(jid));
//    }

    public void addContactToGroup(String groupName, String jid) {
        createGroup(groupName);
        RosterGroup group = mRoster.getGroup(groupName);
        try {
            group.addEntry(mRoster.getEntry(jid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public Roster getRoster() {
        return mRoster;
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

    //
    private void insertContactTable(ContactInfo friend) {
//        if (null != friend) {
//            MeetDatabase dataManager = MeetDatabase.getInstance();
//            if (!dataManager.isFriendExist(mLoginUserName, friend.getUserName())) {
//                friend.setGroups(ContactGroup.GROUP_FRIENDS);
//                dataManager.insertFriend(friend);
//            }
//
//            // Update recently and block list, but not insert
//            if (dataManager.isRecentContactExist(mLoginUserName, friend.getUserName())) {
//                friend.setGroups(ContactGroup.GROUP_RECENTLY);
//                dataManager.updateRecentContact(friend, false);
//            }
//
//            if (dataManager.isBlockUserExist(mLoginUserName, friend.getUserName())) {
//                friend.setGroups(ContactGroup.GROUP_BLOCK);
//                dataManager.updateBlockUser(friend);
//            }
//
//        }
    }
//
//    private void updateContactPresence(ContactInfo contact) {
//        if (null != contact) {
//            MeetDatabase dataManager = MeetDatabase.getInstance();
//            contact.setGroups(ContactGroup.GROUP_FRIENDS);
//            dataManager.updateFriend(contact);
//
//            if (dataManager.isRecentContactExist(mLoginUserName, contact.getUserName())) {
//                contact.setGroups(ContactGroup.GROUP_RECENTLY);
//                dataManager.updateRecentContact(contact, false);
//            }
//
//            contact.setGroups(ContactGroup.GROUP_BLOCK);
//            dataManager.updateBlockUser(contact);
//        }
//    }
//
//    private void deleteContactFromTable(String userName) {
//        if (!TextUtils.isEmpty(userName)) {
//            MeetDatabase dataManager = MeetDatabase.getInstance();
//            dataManager.deleteFriendByUserName(mLoginUserName, userName);
//        }
//    }
//

    /**
     * Listener for the roster events. It will call the remote listeners
     * registered.
     */
    private class RosterListenerAdapter implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            List<String> tab = new ArrayList<String>(addresses);
            for (IRosterListener l : mRemoteRosListeners) {
                l.onEntriesAdded(tab);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
//            for (String address : addresses) {
//                Log.d(TAG, "==>onEntriesDeleted(Thread):" + address);
//                deleteContactFromTable(XmppStringUtils.parseName(address));
//            }
            List<String> tab = new ArrayList<String>(addresses);
            for (IRosterListener l : mRemoteRosListeners) {
                l.onEntriesDeleted(tab);
            }
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
            for (IRosterListener l : mRemoteRosListeners) {
                l.onEntriesUpdated(tab);
            }
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
            final String from = presence.getFrom();
            ContactInfo contactInfo = getContact(from);
            if (contactInfo == null) {
                XMPPApi.fetchContactInfo(from, new OnFetchContactListener() {
                    @Override
                    public void onFetchContactInfo(ContactInfo contactInfo) {
                        for (IRosterListener l : mRemoteRosListeners) {
                            l.presenceSubscribe(contactInfo);
                        }
                    }
                });
            } else {
                for (IRosterListener l : mRemoteRosListeners) {
                    l.presenceSubscribe(contactInfo);
                }
            }

        }

        @Override
        public void presenceSubscribed(Presence presence) {
            Log.d(TAG, "presenceSubscribed:" + presence);
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
//    public void clearContactsCache() {
//        mStrangersCache.clear();
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


    private IRosterListener mIRosterListener = new IRosterListener() {
        @Override
        public void onEntriesAdded(List<String> addresses) {
            final String last = ArrayUtils.selectedLast(addresses);
            if (last == null) {
                return;
            }
            for (final String address : addresses) {
                com.agmbat.log.Log.d(TAG, "==>onEntriesAdded(Thread): [address=" + address);
                String bareAddress = XmppStringUtils.parseBareAddress(address);
                String userName = XmppStringUtils.parseName(address);
                XMPPApi.fetchContactInfo(address, new OnFetchContactListener() {
                    @Override
                    public void onFetchContactInfo(ContactInfo contactInfo) {
                        if (contactInfo != null) {
                            com.agmbat.log.Log.d("contact:" + contactInfo.toString());
                            saveOrUpdateContact(contactInfo);
                        }
                        if (last.equals(address)) {
                            // 最后一个需要发送消息到UI
                            updateGroupList();
                            EventBus.getDefault().post(new ContactUpdateEvent());
                        }
                    }
                });
            }

        }

        @Override
        public void onEntriesUpdated(List<String> addresses) {

        }

        @Override
        public void onEntriesDeleted(List<String> addresses) {

        }

        @Override
        public void presenceSubscribe(final ContactInfo contactInfo) {
            // 需要用本地数据库存为列表
            UiUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    UserManager.getInstance().addFriendRequest(contactInfo);
                    EventBus.getDefault().post(new PresenceSubscribeEvent(contactInfo));
                }
            });
        }
    };


    /////
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
        List<RosterGroup> list = getGroupList();
        for (RosterGroup rosterGroup : list) {
            ContactGroup exist = findExitContactGroup(rosterGroup.getName());
            if (exist == null) {
                ContactGroup item = new ContactGroup(rosterGroup.getName());
                ContactGroupTableManager.save(item);
                for (RosterEntry entry : rosterGroup.getEntries()) {
                    ContactInfo contactInfo = findExistContactInfo(entry.getUser());
                    if (contactInfo == null) {
                        android.util.Log.e("updateGroupList", "updateGroupList error");
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

}
