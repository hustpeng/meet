package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.IM;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

/**
 * 我的信息界面
 */
public class PersonalInfoActivity extends Activity {

    /**
     * 头像
     */
    private ImageView mHeadView;

    /**
     * 用户信息
     */
    private VCardObject mVCardObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_personal_info);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mHeadView = (ImageView) findViewById(R.id.head);
        findViewById(R.id.im_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVCardObject == null) {
                    return;
                }
                Intent intent = new Intent(PersonalInfoActivity.this, EditAvatarActivity.class);
                intent.putExtra("url", mVCardObject.getAvatar());
                startActivity(intent);
            }
        });
        findViewById(R.id.nick_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInfoActivity.this, EditNameActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.my_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInfoActivity.this, QRCodeCardActivity.class);
                startActivity(intent);
            }
        });
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
        updateView();
    }

    private void updateView() {
        if (mVCardObject == null) {
            return;
        }
//        mNickNameView.setText(mVCardObject.getNickname());
        ImageManager.displayImage(mVCardObject.getAvatar(), mHeadView);
//        mUserNameView.setText(getString(R.string.id_name_format) + " " + mVCardObject.getUserName());
//        mGenderView.setImageResource(getGenderImage(mVCardObject.getGender()));
    }

}
