package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.jivesoftware.smackx.message.MessageObject;

import java.util.List;

/**
 * 聊天界面adapter
 */
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
        MessageObject prevMessage = null;
        if (position > 0) {
            prevMessage = getItem(position - 1);
        }
        boolean isShowTime = isShowTime(message, prevMessage);
        view.update(message, isShowTime, false);
        return convertView;
    }

    /**
     * 是否显示时间
     *
     * @param messageObject
     * @param prevMessage
     * @return
     */
    private boolean isShowTime(MessageObject messageObject, MessageObject prevMessage) {
        if (prevMessage == null) {
            return true;
        }
        long current = messageObject.getDate();
        long prev = prevMessage.getDate();
        return (current - prev > (5 * 60 * 1000));
    }

}