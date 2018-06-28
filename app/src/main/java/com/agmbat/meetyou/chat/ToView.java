package com.agmbat.meetyou.chat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.meetyou.R;

/**
 * 发送给其他人的消息
 */
public class ToView extends ItemView {

    public ToView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.chat_list_item_to;
    }

    @Override
    protected void setAudioDrawable(AudioBody audioBody) {
        Drawable[] drawable = mChatContentView.getCompoundDrawables();
        if (isPlaying(audioBody)) {
            if (drawable[2] == null || !(drawable[2] instanceof AnimationDrawable)) {
                mChatContentView
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_f, 0);
                drawable = mChatContentView.getCompoundDrawables();
            }
            AnimationDrawable animationDrawable = (AnimationDrawable) drawable[2];
            animationDrawable.start();
        } else {
            if (drawable[2] != null && drawable[2] instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable[2];
                animationDrawable.stop();
                animationDrawable.selectDrawable(0);
            }
            mChatContentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
        }
    }

    @Override
    protected void setupViews() {
    }

}