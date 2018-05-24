package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.jivesoftware.smackx.message.MessageObject;

import java.util.List;

public class MessageListAdapter extends ArrayAdapter<MessageObject> {

    public MessageListAdapter(Context context, List<MessageObject> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new MessageView(getContext());
        }
        MessageObject message = getItem(position);
        MessageView view = (MessageView) convertView;
        view.update(message);
        return convertView;
    }

}