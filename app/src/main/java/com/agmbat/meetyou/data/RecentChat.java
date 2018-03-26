package com.agmbat.meetyou.data;

import com.agmbat.imsdk.data.ChatMessage;
import com.agmbat.imsdk.data.ContactInfo;

import java.util.Comparator;

/**
 * 最近消息记录
 */
public class RecentChat {

    private ContactInfo mContact;
    private int mUnreadCount;
    private ChatMessage mLastChatMessage;

    public ContactInfo getContact() {
        return mContact;
    }

    public void setContact(ContactInfo contactInfo) {
        mContact = contactInfo;
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }

    public void setUnreadCount(int count) {
        mUnreadCount = count;
    }

    public ChatMessage getLastChatMessage() {
        return mLastChatMessage;
    }

    public void setLastChatMessage(ChatMessage message) {
        mLastChatMessage = message;
    }

    public static class RecentChatComparator implements Comparator<RecentChat> {

        @Override
        public int compare(RecentChat o1, RecentChat o2) {
            ChatMessage lastChatMessage1 = o1.getLastChatMessage();
            ChatMessage lastChatMessage2 = o2.getLastChatMessage();
            if (o1.getUnreadCount() > 0 && o2.getUnreadCount() <= 0) {
                return -1;
            } else if (o1.getUnreadCount() <= 0 && o2.getUnreadCount() > 0) {
                return 1;
            } else if (null != lastChatMessage1 && null != lastChatMessage2
                    && lastChatMessage1.getTimestamp() > lastChatMessage2.getTimestamp()) {
                return -1;
            } else if (null != lastChatMessage1 && null != lastChatMessage2
                    && lastChatMessage1.getTimestamp() < lastChatMessage2.getTimestamp()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
