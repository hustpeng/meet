package com.agmbat.meetyou.tab;

import com.agmbat.meetyou.data.ContactInfo;
import com.agmbat.meetyou.data.RecentChat;

import java.util.ArrayList;
import java.util.List;

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
        for (int i = 0; i < 10; i++) {
            RecentChat chat = new RecentChat();
            ContactInfo contactInfo = new ContactInfo();
            chat.setContact(contactInfo);
            list.add(chat);
        }
        return list;
    }

    public List<ContactInfo> queryFriends(String mLoginUserName) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            list.add(contactInfo);
        }
        return list;
    }

    public List<ContactInfo> queryRecentContacts(String mLoginUserName) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            list.add(contactInfo);
        }
        return list;
    }

    public List<ContactInfo> queryAllBlockUsers(String mLoginUserName) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            list.add(contactInfo);
        }
        return list;
    }
}
