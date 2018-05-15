package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ChatMessage;
import com.agmbat.imsdk.user.UserManager;

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

    public void update(MessageObject msg) {
        if (isToOthers(msg)) {
            mFromView.setVisibility(View.GONE);
            mToView.setVisibility(View.VISIBLE);
            mToView.update(msg);
        } else {
            mToView.setVisibility(View.GONE);
            mFromView.setVisibility(View.VISIBLE);
            mFromView.update(msg);
        }
    }

    private static boolean isToOthers(MessageObject messageObject) {
        return messageObject.getSenderJid().equals(UserManager.getInstance().getLoginUser().getJid());
    }
}
