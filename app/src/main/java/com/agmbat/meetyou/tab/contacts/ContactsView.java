package com.agmbat.meetyou.tab.contacts;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsView extends RelativeLayout {

    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    public ContactsView(Context context) {
        super(context);
        View.inflate(context, R.layout.contacts_item, this);
        ButterKnife.bind(this, this);
    }

    public void update(ContactInfo contactInfo) {
        mNickNameView.setText(contactInfo.getNickName());
        ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, AvatarHelper.getOptions());
    }

}
