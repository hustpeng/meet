package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.agmbat.imsdk.data.ChatMessage;

import java.util.List;

public class MessageListAdapter extends ArrayAdapter<ChatMessage> {

    public MessageListAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new MessageView(getContext());
        }
        ChatMessage message = getItem(position);
        MessageView view = (MessageView) convertView;
        view.update(message);
        return convertView;
    }

}