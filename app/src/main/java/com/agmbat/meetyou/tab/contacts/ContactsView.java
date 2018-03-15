package com.agmbat.meetyou.tab.contacts;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.data.ContactInfo;

public class ContactsView extends RelativeLayout {

    public ImageView mAvatarView;
    public TextView mNameView;
    public TextView mPersonalMsgView;
    public TextView mStatusView;

    public ContactsView(Context context) {
        super(context);
        View.inflate(context, R.layout.contacts_item, this);
        mAvatarView = (ImageView) findViewById(R.id.avatar);
        mNameView = (TextView) findViewById(R.id.name);
        mPersonalMsgView = (TextView) findViewById(R.id.personal_msg);
        mStatusView = (TextView) findViewById(R.id.status);
    }

    public void update(ContactInfo contactInfo) {
        mAvatarView.setImageResource(R.drawable.ic_default_avatar);
        mNameView.setText(contactInfo.getDisplayName());
        mPersonalMsgView.setText(contactInfo.getPersonalMsg());
        mStatusView.setText("在线");
    }

}
