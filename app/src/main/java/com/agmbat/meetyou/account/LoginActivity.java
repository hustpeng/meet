package com.agmbat.meetyou.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.meetyou.MainTabActivity;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登陆界面
 */
public class LoginActivity extends FragmentActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.btn_login)
    Button mLoginButton;

    @BindView(R.id.input_username)
    EditText mUserNameView;

    @BindView(R.id.input_password)
    EditText mPasswordView;

    private ImAccountManager mLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        mLoginManager = new ImAccountManager(this);

        mUserNameView.addTextChangedListener(new TextChange());
        mPasswordView.addTextChangedListener(new TextChange());
        // for test
        mUserNameView.setText("13437122759");
        mPasswordView.setText("a123123");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 收到注册成功的消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RegisterSuccessEvent event) {
        finish();
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_login)
    void onClickLogin() {
        login();
    }

    @OnClick(R.id.btn_signup)
    void onClickSignup() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.login_problem)
    void onClickLoginProblem() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    private void login() {
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        mLoginManager.login(userName, password, new ImAccountManager.OnLoginListener() {
            @Override
            public void onLogin(ApiResult result) {
                ToastUtil.showToastLong(result.mErrorMsg);
                if (result.mResult) {
                    finish();
                    startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
                }
            }
        });
    }


    /**
     * EditText监听器
     */
    private class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            updateLoginButtonState();
        }
    }

    /**
     * 更新登陆Button状态
     */
    private void updateLoginButtonState() {
        boolean userNameValid = mUserNameView.getText().length() > 0;
        boolean passwordValid = mPasswordView.getText().length() > 4;
        boolean enabled = userNameValid && passwordValid;
        mLoginButton.setEnabled(enabled);
    }

}
