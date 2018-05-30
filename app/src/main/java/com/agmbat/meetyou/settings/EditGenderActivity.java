package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.GenderHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
     * 用户选中的性别
     */
    private int mGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_edit_gender);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        updateGenderSelected(user.getGender());
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
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginUserUpdateEvent event) {
        LoginUser user = event.getLoginUser();
        updateGenderSelected(user.getGender());
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
        updateGenderSelected(mGender);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.btn_gender_female)
    void onClickFemale() {
        mGender = GenderHelper.GENDER_FEMALE;
        updateGenderSelected(mGender);
    }

    /**
     * 点击保存
     */
    @OnClick(R.id.btn_save)
    void onClickSave() {
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        if (mGender == user.getGender()) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            // 修改性别
            user.setGender(mGender);
            XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            finish();
        }
    }

    private void updateGenderSelected(int gender) {
        if (gender == GenderHelper.GENDER_MALE) {
            mGenderMaleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_selected, 0);
            mGenderFemaleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (gender == GenderHelper.GENDER_FEMALE) {
            mGenderMaleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mGenderFemaleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_selected, 0);
        }
    }

}
