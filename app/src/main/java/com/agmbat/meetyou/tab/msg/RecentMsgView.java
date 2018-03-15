package com.agmbat.meetyou.tab.msg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.data.ChatMessage;
import com.agmbat.meetyou.data.RecentChat;

public class RecentMsgView extends LinearLayout {

    private ImageView mAvatarView;
    private TextView mNameView;
    private TextView mMessageView;
    private TextView mUnreadCountView;
    private TextView mLastMsgTimeView;

    public RecentMsgView(Context context) {
        super(context);
        View.inflate(context, R.layout.recent_chat_item, this);
        mAvatarView = (ImageView) findViewById(R.id.avatar);
        mNameView = (TextView) findViewById(R.id.name);
        mMessageView = (TextView) findViewById(R.id.last_message);
        mLastMsgTimeView = (TextView) findViewById(R.id.last_msg_time);
        mUnreadCountView = (TextView) findViewById(R.id.unread_count);
    }

    public void update(RecentChat recentChat) {
        mNameView.setText(recentChat.getContact().getDisplayName());
        ChatMessage msg = recentChat.getLastChatMessage();
        if (msg != null) {
            mMessageView.setVisibility(View.VISIBLE);
            setLastMessageBody(msg);
            mLastMsgTimeView.setVisibility(View.VISIBLE);
            mLastMsgTimeView.setText(msg.getShowLastText());
        } else {
            mMessageView.setVisibility(View.GONE);
            mLastMsgTimeView.setVisibility(View.GONE);
        }

        int unreadCount = recentChat.getUnreadCount();
        if (unreadCount > 0) {
            mUnreadCountView.setVisibility(View.VISIBLE);
            mUnreadCountView.setText(String.valueOf(unreadCount));
        } else {
            mUnreadCountView.setVisibility(View.GONE);
        }

//        String uri = Scheme.wrapUri("avatar", recentChat.getContact().getAvatarId());
//        ImageManager.displayImage(uri, mAvatarView);

//        if (ConnectionHelper.getAvatarManager().isAvatarCached(
//                recentChat.getContact().getAvatarId())) {
//            Bitmap avatar = ConnectionHelper.getAvatarManager().getAvatarBitmap(
//                    recentChat.getContact().getAvatarId());
//            mAvatarView.setImageBitmap(avatar);
//        } else {
//            mAvatarView.setImageResource(R.drawable.default_avatar);
//        }
    }

    private void setLastMessageBody(ChatMessage msg) {
//        Body body = msg.getBody();
//        if (body instanceof TextBody) {
//            TextBody textBody = (TextBody) body;
//            mMessageView.setText(textBody.getContent());
//        } else if (body instanceof AudioBody) {
//            mMessageView.setText(R.string.recent_chat_type_voice);
//        } else if (body instanceof FireBody) {
//            mMessageView.setText(R.string.recent_chat_type_fire);
//        } else if (body instanceof ImageBody) {
//            mMessageView.setText(R.string.recent_chat_type_pic);
//        } else if (body instanceof LocationBody) {
//            mMessageView.setText(R.string.recent_chat_type_location);
//        } else if (body instanceof FriendBody) {
//            mMessageView.setText(R.string.recent_chat_type_recommend);
//        }
    }
}
