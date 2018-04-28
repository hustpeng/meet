package com.agmbat.imsdk.db;

import android.content.Context;

import com.agmbat.android.AppResources;
import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.db.DbManagerFactory;
import com.agmbat.imsdk.data.ChatMessage;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.data.RecentChat;
import com.agmbat.sqlite.SqliteDbConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MeetDatabase {

    private static final String DB_NAME = "meet.db";
    private static final int DB_VERSION = 1;

    private static final SqliteDbConfig sConfig;

    private static SqliteDbConfig initDbConfig() {
        SqliteDbConfig daoConfig = new SqliteDbConfig();
        daoConfig.setDbName(DB_NAME);
        File dir = AppResources.getAppContext().getDir("sqldb", Context.MODE_PRIVATE);
        daoConfig.setDbDir(dir);
        daoConfig.setDbVersion(DB_VERSION);
        daoConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                // TODO: ...
                // db.addColumn(...);
                // db.dropTable(...);
                // ...
            }
        });
        return daoConfig;
    }

    static {
        sConfig = initDbConfig();
    }

    private static final MeetDatabase INSTANCE = new MeetDatabase();

    public static MeetDatabase getInstance() {
        return INSTANCE;
    }

    private MeetDatabase() {
    }

    /**
     * 获取数据库
     *
     * @return
     */
    public DbManager getDatabase() {
        return DbManagerFactory.getInstance(sConfig);
    }

    /**
     * 保存好友申请列表
     *
     * @param contactInfo
     */
    public void saveFriendRequest(ContactInfo contactInfo) {
        FriendRequest request = new FriendRequest(contactInfo);
        DbManager db = DbManagerFactory.getInstance(sConfig);
        try {
            FriendRequest exist = db.selector(FriendRequest.class)
                    .where("jid", "in", new String[]{request.getJid()})
                    .findFirst();
            if (exist == null) {
                db.save(request);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除好友申请列表中的item
     *
     * @param contactInfo
     */
    public void deleteFriendRequest(ContactInfo contactInfo) {
        DbManager db = DbManagerFactory.getInstance(sConfig);
        try {
            FriendRequest exist = db.selector(FriendRequest.class)
                    .where("jid", "in", new String[]{contactInfo.getBareJid()})
                    .findFirst();
            if (exist != null) {
                db.delete(exist);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取好友申请信息
     *
     * @return
     */
    public List<ContactInfo> getFriendRequestList() {
        List<ContactInfo> list = new ArrayList<>();
        DbManager db = DbManagerFactory.getInstance(sConfig);
        try {
            List<FriendRequest> requestList = db.selector(FriendRequest.class).findAll();
            if (requestList != null) {
                for (FriendRequest request : requestList) {
                    list.add(request.toContactInfo());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * TODO 暂时模拟, 需要实现
     * 获取消息列表
     *
     * @return
     */
    public List<RecentChat> getRecentChatList() {
        List<RecentChat> list = new ArrayList<RecentChat>();
        for (int i = 0; i < 50; i++) {
            RecentChat chat = new RecentChat();
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setNickname("联系" + i);
            chat.setContact(contactInfo);
            ChatMessage message = new ChatMessage();
            message.setContent("Content:" + i);
            message.setTimestamp(System.currentTimeMillis());
            chat.setLastChatMessage(message);
            list.add(chat);
        }
        return list;
    }

    public List<ContactGroup> getGroupList(String mLoginUserName) {
        List<ContactGroup> groups = new ArrayList<ContactGroup>();

        ContactGroup group1 = new ContactGroup("同事");
        List<ContactInfo> friends = queryFriends(mLoginUserName);
        group1.setContactList(friends);

        ContactGroup group2 = new ContactGroup("恋人");
        List<ContactInfo> recentContacts = queryRecentContacts(mLoginUserName);
        group2.setContactList(recentContacts);

        ContactGroup group3 = new ContactGroup("亲人");
        List<ContactInfo> blockContacts = queryAllBlockUsers(mLoginUserName);
        group3.setContactList(blockContacts);

        ContactGroup group4 = new ContactGroup("朋友");
        List<ContactInfo> blockContacts1 = queryAllBlockUsers(mLoginUserName);
        group4.setContactList(blockContacts1);

//        friendGroup.sort();
        // recentlyGroup.sort();
//        blockGroup.sort();

//        groups.add(group1);
//        groups.add(group2);
//        groups.add(group3);
//        groups.add(group4);
        return groups;
    }

    private RecentChat queryRecentChatFor(ContactInfo contactInfo) {
        RecentChat recentChat = new RecentChat();
        recentChat.setContact(contactInfo);
        ChatMessage lastChatMessage = new ChatMessage();
        recentChat.setUnreadCount(5);
        recentChat.setLastChatMessage(lastChatMessage);
        return recentChat;
    }

    public List<ContactInfo> queryFriends(String mLoginUserName) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setNickname("Friends" + i);
            list.add(contactInfo);
        }
        return list;
    }

    public List<ContactInfo> queryRecentContacts(String mLoginUserName) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setNickname("Friends" + i);
            list.add(contactInfo);
        }
        return list;
    }

    public List<ContactInfo> queryAllBlockUsers(String mLoginUserName) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setNickname("Friends" + i);
            list.add(contactInfo);
        }
        return list;
    }

    /**
     * 通过jid查找联系人
     *
     * @param jid
     * @return
     */
    public ContactInfo findParticipant(String jid) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setBareJid(jid);
        contactInfo.setNickname("小小");
        return contactInfo;
    }

    /**
     * 获取聊天信息
     *
     * @param contactInfo
     * @return
     */
    public List<ChatMessage> getMessage(ContactInfo contactInfo) {
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage message = new ChatMessage();
        list.add(message);
        list.add(message);
        list.add(message);
        list.add(message);
        list.add(message);
        list.add(message);
        list.add(message);
        list.add(message);
        list.add(message);
        return list;
    }


}
