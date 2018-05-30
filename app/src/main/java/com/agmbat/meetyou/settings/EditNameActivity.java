package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑昵称界面
 */
public class EditNameActivity extends Activity {

    /**
     * 昵称编辑框
     */
    @BindView(R.id.input_name)
    EditText mNameEditText;

    /**
     * 保存button
     */
    @BindView(R.id.btn_save)
    Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_edit_name);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNameEditText.getText().toString().trim().length() > 0) {
                    mSaveButton.setEnabled(true);
                } else {
                    mSaveButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        update(XMPPManager.getInstance().getRosterManager().getLoginUser());
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
        update(user);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击保存
     */
    @OnClick(R.id.btn_save)
    void onClickSave() {
        changeNickName();
    }

    private void update(LoginUser user) {
        mNameEditText.setText(user.getNickname());
        mNameEditText.setSelection(mNameEditText.getText().toString().trim().length());
    }

    /**
     * 修改昵称
     */
    private void changeNickName() {
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        String nickName = mNameEditText.getText().toString();
        if (nickName.equals(user.getNickname())) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            // 修改昵称的逻辑
            user.setNickname(nickName);
            XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
            finish();
        }
    }

}
