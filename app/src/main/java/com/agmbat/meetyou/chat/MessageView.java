package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.agmbat.imsdk.data.ChatMessage;


public class MessageView extends FrameLayout {

    private ItemView mFromView;
    private ItemView mToView;

    public MessageView(Context context) {
        super(context);
        mFromView = new FromView(context);
        mToView = new ToView(context);
        addView(mFromView);
        addView(mToView);
    }

    public void update(ChatMessage msg) {
        if (msg.getMsgDirection() == ChatMessage.DIRECTION_TO_OTHERS) {
            mFromView.setVisibility(View.GONE);
            mToView.setVisibility(View.VISIBLE);
            mToView.update(msg);
        } else if (msg.getMsgDirection() == ChatMessage.DIRECTION_FROM_OTHERS) {
            mToView.setVisibility(View.GONE);
            mFromView.setVisibility(View.VISIBLE);
            mFromView.update(msg);
        }
    }

}
