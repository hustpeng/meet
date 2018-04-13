package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.IM;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.data.GenderHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的信息界面
 */
public class PersonalInfoActivity extends Activity {

    /**
     * 头像
     */
    @BindView(R.id.head)
    ImageView mHeadView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    @BindView(R.id.gender)
    TextView mGenderView;

    /**
     * 用户信息
     */
    private VCardObject mVCardObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        updateView();
        IM.get().fetchMyVCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 收到vcard更新信息
     *
     * @param vCardObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
        updateView();
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击头像
     */
    @OnClick(R.id.im_header)
    void onClickHead() {
        if (mVCardObject == null) {
            return;
        }
        Intent intent = new Intent(this, EditAvatarActivity.class);
        startActivity(intent);
    }

    /**
     * 点击昵称
     */
    @OnClick(R.id.btn_nickname)
    void onClickNickname() {
        Intent intent = new Intent(this, EditNameActivity.class);
        startActivity(intent);
    }

    /**
     * 点击编辑性别
     */
    @OnClick(R.id.btn_gender)
    void onClickGender() {
        Intent intent = new Intent(this, EditGenderActivity.class);
        startActivity(intent);
    }

    /**
     * 点击我的二维码
     */
    @OnClick(R.id.my_qrcode)
    void onClickQRCode() {
        Intent intent = new Intent(this, QRCodeCardActivity.class);
        startActivity(intent);
    }

    private void updateView() {
        if (mVCardObject == null) {
            return;
        }
        ImageManager.displayImage(mVCardObject.getAvatar(), mHeadView);
        mNickNameView.setText(mVCardObject.getNickname());
        mGenderView.setText(GenderHelper.getName(mVCardObject.getGender()));
    }


}
