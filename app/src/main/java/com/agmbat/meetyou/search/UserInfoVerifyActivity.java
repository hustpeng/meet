package com.agmbat.meetyou.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.asmack.RosterManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户信息界面, 用于验证是否加为好友
 */
public class UserInfoVerifyActivity extends Activity {

    private RosterManager mRosterManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        String jid = getIntent().getStringExtra("userInfo");
        mRosterManager = XMPPManager.getInstance().getRosterManager();
        IM.get().fetchVCard(jid);
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

    private VCardObject mVCardObject;

    /**
     * 收到vcard更新信息
     *
     * @param vCardObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
    }
}
