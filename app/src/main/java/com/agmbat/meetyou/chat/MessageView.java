package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.agmbat.imsdk.asmack.MessageManager;

import org.jivesoftware.smackx.message.MessageObject;

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

    public void update(MessageObject msg, boolean showTime) {
        if (MessageManager.isToOthers(msg)) {
            mFromView.setVisibility(View.GONE);
            mToView.setVisibility(View.VISIBLE);
            mToView.update(msg, showTime);
        } else {
            mToView.setVisibility(View.GONE);
            mFromView.setVisibility(View.VISIBLE);
            mFromView.update(msg, showTime);
        }
    }

}
