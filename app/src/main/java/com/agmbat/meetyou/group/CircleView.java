package com.agmbat.meetyou.group;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.group.CircleInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleView extends RelativeLayout {

    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    public CircleView(Context context) {
        super(context);
        View.inflate(context, R.layout.contacts_item, this);
        ButterKnife.bind(this, this);
    }

    public void update(CircleInfo contactInfo) {
        mNickNameView.setText(contactInfo.getName());
        ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, AvatarHelper.getGroupOptions());
    }

}
