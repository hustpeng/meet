package com.agmbat.meetyou.tab.msg;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.asmack.MessageManager;
import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.FireBody;
import com.agmbat.imsdk.chat.body.FriendBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;

import org.jivesoftware.smackx.message.MessageObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentMsgView extends LinearLayout {

    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    @BindView(R.id.last_message)
    TextView mMessageView;

    @BindView(R.id.unread_count)
    TextView mUnreadCountView;

    @BindView(R.id.last_msg_time)
    TextView mLastMsgTimeView;

    public RecentMsgView(Context context) {
        super(context);
        View.inflate(context, R.layout.recent_chat_item, this);
        ButterKnife.bind(this, this);
    }

    public void update(MessageObject messageObject) {
        ContactInfo contactInfo = MessageManager.getTalkContactInfo(messageObject);
        if (contactInfo != null) {
            mNickNameView.setText(TextUtils.isEmpty(contactInfo.getRemark()) ? contactInfo.getNickName() : contactInfo.getRemark());
            mMessageView.setVisibility(View.VISIBLE);
            mLastMsgTimeView.setVisibility(View.VISIBLE);
            ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, AvatarHelper.getOptions());
        }
//
//        ChatMessage msg = recentChat.getLastChatMessage();
//        if (msg != null) {

        setLastMessageBody(messageObject);

//        mLastMsgTimeView.setText(TimeUtils.formatTime(msg.getTimestamp()));
//        } else {
//            mMessageView.setVisibility(View.GONE);
//            mLastMsgTimeView.setVisibility(View.GONE);
//        }

//        int unreadCount = recentChat.getUnreadCount();
//        if (unreadCount > 0) {
//            mUnreadCountView.setVisibility(View.VISIBLE);
//            mUnreadCountView.setText(String.valueOf(unreadCount));
//        } else {
//            mUnreadCountView.setVisibility(View.GONE);
//        }
    }


    private void setLastMessageBody(MessageObject msg) {
        mMessageView.setText(msg.getBody());
        Body body = BodyParser.parse(msg.getBody());
        if (body instanceof TextBody) {
            TextBody textBody = (TextBody) body;
            mMessageView.setText(textBody.getContent());
        } else if (body instanceof AudioBody) {
            mMessageView.setText(R.string.recent_chat_type_voice);
        } else if (body instanceof FireBody) {
            mMessageView.setText(R.string.recent_chat_type_fire);
        } else if (body instanceof ImageBody) {
            mMessageView.setText(R.string.recent_chat_type_pic);
        } else if (body instanceof LocationBody) {
            mMessageView.setText(R.string.recent_chat_type_location);
        } else if (body instanceof FriendBody) {
            mMessageView.setText(R.string.recent_chat_type_recommend);
        }
    }
}
