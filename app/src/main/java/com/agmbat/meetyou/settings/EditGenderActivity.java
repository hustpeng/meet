package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.asmack.XMPPManager;
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
 * 编辑性别界面
 */
public class EditGenderActivity extends Activity {

    /**
     * 男性
     */
    @BindView(R.id.btn_gender_male)
    TextView mGenderMaleView;

    /**
     * 女性
     */
    @BindView(R.id.btn_gender_female)
    TextView mGenderFemaleView;
    /**
     * 保存button
     */
    @BindView(R.id.btn_save)
    Button mSaveButton;

    /**
     * 用户信息
     */
    private VCardObject mVCardObject;

    private int mGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_edit_gender);
        ButterKnife.bind(this);
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

    /**
     * 收到vcard更新信息
     *
     * @param vCardObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.btn_gender_male)
    void onClickMale() {
        mGender = GenderHelper.GENDER_MALE;
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.btn_gender_female)
    void onClickFemale() {
        mGender = GenderHelper.GENDER_FEMALE;
    }

    /**
     * 点击保存
     */
    @OnClick(R.id.btn_save)
    void onClickSave() {
        if (mGender == mVCardObject.getGender()) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            // 修改性别
            mVCardObject.setGender(mGender);
            EventBus.getDefault().post(mVCardObject);
            XMPPManager.getInstance().getvCardManager().setMyVCard(mVCardObject);
            finish();
        }
    }

}
