package com.agmbat.meetyou.tab;

import com.agmbat.meetyou.data.ChatMessage;
import com.agmbat.meetyou.data.ContactInfo;
import com.agmbat.meetyou.data.RecentChat;
import com.agmbat.meetyou.tab.contacts.GroupHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理
 */
public class MeetDatabase {

    private static final MeetDatabase INSTANCE = new MeetDatabase();

    public static MeetDatabase getInstance() {
        return INSTANCE;
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

    public List<GroupHolder> getGroupList(String mLoginUserName) {
        List<GroupHolder> groups = new ArrayList<GroupHolder>();

        GroupHolder group1 = new GroupHolder("同事");
        List<ContactInfo> friends = queryFriends(mLoginUserName);
        group1.setContactList(friends);

        GroupHolder group2 = new GroupHolder("恋人");
        List<ContactInfo> recentContacts = queryRecentContacts(mLoginUserName);
        group2.setContactList(recentContacts);

        GroupHolder group3 = new GroupHolder("亲人");
        List<ContactInfo> blockContacts = queryAllBlockUsers(mLoginUserName);
        group3.setContactList(blockContacts);

        GroupHolder group4 = new GroupHolder("朋友");
        List<ContactInfo> blockContacts1 = queryAllBlockUsers(mLoginUserName);
        group4.setContactList(blockContacts1);

//        friendGroup.sort();
        // recentlyGroup.sort();
//        blockGroup.sort();

        groups.add(group1);
        groups.add(group2);
        groups.add(group3);
        groups.add(group4);
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
}
