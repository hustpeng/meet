package com.agmbat.meetyou.tab.msg;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.agmbat.imsdk.data.RecentChat;

import org.jivesoftware.smackx.message.MessageObject;

import java.util.List;

public class RecentChatAdapter extends ArrayAdapter<MessageObject> {

    public RecentChatAdapter(Context context, List<MessageObject> contactList) {
        super(context, 0, contactList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = new RecentMsgView(getContext());
        }
        MessageObject recentChat = getItem(position);
        RecentMsgView view = (RecentMsgView) convertView;
        view.update(recentChat);
        return convertView;
    }

//    public void updateRecentChat(MessageObject recentChat) {
//        int count = getCount();
//        for (int i = 0; i < count; i++) {
//            RecentChat current = getItem(i);
//            if (current.getContact().equals(recentChat.getContact())) {
//                current.setContact(recentChat.getContact());
//                current.setLastChatMessage(recentChat.getLastChatMessage());
//                current.setUnreadCount(recentChat.getUnreadCount());
//                break;
//            }
//        }
//        notifyDataSetChanged();
//        notifyDataSetInvalidated();
//    }

//    public boolean isRecentChatExist(RecentChat recentChat) {
//        boolean exist = false;
//        int count = getCount();
//        for (int i = 0; i < count; i++) {
//            RecentChat current = getItem(i);
//            if (current.getContact().equals(recentChat.getContact())) {
//                exist = true;
//                break;
//            }
//        }
//        return exist;
//    }


//    public void sort() {
//        super.sort(new RecentChat.RecentChatComparator());
//        notifyDataSetChanged();
//    }


}
