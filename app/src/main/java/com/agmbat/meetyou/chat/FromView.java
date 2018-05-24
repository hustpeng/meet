package com.agmbat.meetyou.chat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.meetyou.R;

/**
 * 其他发送过来的消息
 */
public class FromView extends ItemView {

    public FromView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.chat_list_item_from;
    }

    @Override
    protected void setAudioDrawable(AudioBody audioBody) {
        Drawable[] drawable = mChatContentView.getCompoundDrawables();
        if (isPlaying(audioBody)) {
            if (drawable[0] == null || !(drawable[0] instanceof AnimationDrawable)) {
                mChatContentView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatfrom_voice_playing_f, 0, 0,
                        0);
                drawable = mChatContentView.getCompoundDrawables();
            }
            AnimationDrawable animationDrawable = (AnimationDrawable) drawable[0];
            animationDrawable.start();
        } else {
            if (drawable[0] != null && drawable[0] instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable[0];
                animationDrawable.stop();
                animationDrawable.selectDrawable(0);
            }
            mChatContentView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatfrom_voice_playing, 0, 0, 0);
        }
    }

    @Override
    protected void setupViews() {
        mChatContentView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }


}