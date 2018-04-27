package com.agmbat.meetyou.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.asmack.RosterManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户信息界面, 用于验证是否加为好友
 */
public class UserInfoVerifyActivity extends Activity {

    private RosterManager mRosterManager;

    /**
     * 申请人信息
     */
    private ContactInfo mContactInfo;


    ImageView mhead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        String jid = getIntent().getStringExtra("userInfo");
        mContactInfo = UserManager.getInstance().getFriendRequest(jid);
        mRosterManager = XMPPManager.getInstance().getRosterManager();
        ImageManager.displayImage(mContactInfo.getAvatar(), mhead);
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
     * 点击添加联系人
     */
    @OnClick(R.id.btn_add_to_contact)
    void onClickAddToContact() {
//        ContactInfo contactInfo = null;
//        String loginUser = XMPPManager.getInstance().getConnectionUserName();
//        if (loginUser.equals("13437122759")) {
//            contactInfo = new ContactInfo("15002752759@yuan520.com/Android");
//            contactInfo.setNickname("接电弧");
//        } else {
//            contactInfo = new ContactInfo("13437122759@yuan520.com/Android");
//            contactInfo.setNickname("好名");
//        }
//        boolean result = mRosterManager.addContactToFriend(contactInfo);
//        if (result) {
//            ToastUtil.showToastLong("已发送");
//        } else {
//            ToastUtil.showToastLong("添加好友失败");
//        }
    }


}
