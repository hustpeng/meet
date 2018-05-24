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
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑个性签名界面
 */
public class EditSignatureActivity extends Activity {

    /**
     * 昵称编辑框
     */
    @BindView(R.id.input_text)
    EditText mEditText;

    @BindView(R.id.tips)
    TextView mTipsView;

    /**
     * 保存button
     */
    @BindView(R.id.btn_save)
    Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_signature);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mEditText.getText().toString();
                int remainder = 50 - text.length();
                mTipsView.setText(String.valueOf(remainder));
            }
        });
        update(UserManager.getInstance().getLoginUser());
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
        LoginUser user = UserManager.getInstance().getLoginUser();
        String text = mEditText.getText().toString();
        if (text.equals(user.getStatus())) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            user.setStatus(text);
            UserManager.getInstance().saveLoginUser(user);
            finish();
        }
    }

    private void update(LoginUser user) {
        mEditText.setText(user.getStatus());
        mEditText.setSelection(mEditText.getText().toString().trim().length());
    }
}
