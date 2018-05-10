package com.agmbat.meetyou.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.helper.GenderHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户信息界面, 用于验证是否加为好友
 */
public class UserInfoVerifyActivity extends Activity {

    /**
     * 申请人信息
     */
    private ContactInfo mContactInfo;

    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    @BindView(R.id.gender)
    ImageView mGenderView;

    @BindView(R.id.username)
    TextView mUserNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_verify);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        ButterKnife.bind(this);
        String jid = getIntent().getStringExtra("userInfo");
        mContactInfo = UserManager.getInstance().getFriendRequest(jid);
        mNickNameView.setText(mContactInfo.getNickName());
        mGenderView.setImageResource(GenderHelper.getIconRes(mContactInfo.getGender()));
        ImageManager.displayImage(mContactInfo.getAvatar(), mAvatarView, AvatarHelper.getOptions());
        mUserNameView.setText(getString(R.string.id_name_format) + " " + mContactInfo.getUserName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 同意添加为好友
     */
    @OnClick(R.id.btn_pass_validation)
    void onClickPassValidation() {
        XMPPManager.getInstance().getRosterManager().acceptFriend(mContactInfo);
    }


}
