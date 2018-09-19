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

    private OnChatLongClickListener mOnChatLongClickListener;

    public MessageListAdapter(Context context, List<MessageObject> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new MessageView(getContext());
        }
        MessageObject message = getItem(position);
        final MessageView messageView = (MessageView) convertView;
        MessageObject prevMessage = null;
        if (position > 0) {
            prevMessage = getItem(position - 1);
        }
        boolean isShowTime = isShowTime(message, prevMessage);
        messageView.update(message, isShowTime);
        messageView.setOnItemLongClickListener(new ItemView.OnItemLongClickListener() {
            @Override
            public void onContentLongClick(View view) {
                if (null != mOnChatLongClickListener) {
                    mOnChatLongClickListener.onContentLongClick(position, messageView);
                }
            }

            @Override
            public void onAvatarLongClick(View view) {
                if (null != mOnChatLongClickListener) {
                    mOnChatLongClickListener.onAvatarLongClick(position, messageView);
                }
            }
        });
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


    public void setOnChatLongClickListener(OnChatLongClickListener onChatLongClickListener) {
        mOnChatLongClickListener = onChatLongClickListener;
    }


    public interface OnChatLongClickListener {

        public void onAvatarLongClick(int position, MessageView messageView);

        public void onContentLongClick(int position, MessageView messageView);

    }

}