package com.agmbat.meetyou.discovery.search;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.GenderHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 附近的人联系人view
 */
public class ContactView extends LinearLayout {

    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    @BindView(R.id.last_message)
    TextView mMessageView;

    @BindView(R.id.last_msg_time)
    TextView mLastMsgTimeView;

    @BindView(R.id.gender)
    ImageView mGenderView;

    public ContactView(Context context) {
        super(context);
        View.inflate(context, R.layout.nearby_user_item, this);
        ButterKnife.bind(this, this);
    }

    public void update(ContactInfo contactInfo) {
        mNickNameView.setText(contactInfo.getNickName());
        String distText = contactInfo.getDist() + "米以内";
        mMessageView.setText(distText);
        mGenderView.setImageResource(GenderHelper.getIconRes(contactInfo.getGender()));
        mLastMsgTimeView.setVisibility(View.GONE);
        String uri = contactInfo.getAvatar();
        ImageManager.displayImage(uri, mAvatarView, ImageManager.getCircleOptions());
    }

}
