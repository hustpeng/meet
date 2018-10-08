package com.agmbat.meetyou.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.MainTabActivity;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    private ISLoadingDialog mISLoadingDialog;

    public static void launch(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        mUserNameView.addTextChangedListener(new TextChange());
        mPasswordView.addTextChangedListener(new TextChange());
        // for test
//        mUserNameView.setText("13437122759");
//        mPasswordView.setText("a123123");

        String username = AppConfigUtils.getUserName(getBaseContext());
        if (!TextUtils.isEmpty(username)) {
            mUserNameView.setText(username);
        }
        String password = AppConfigUtils.getPassword(getBaseContext());
        if (!TextUtils.isEmpty(password)) {
            mPasswordView.setText(password);
        }
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
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.login_problem)
    void onClickLoginProblem() {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 登录
     */
    private void login() {
        showLoadingDialog();
        final String userName = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        ImAccountManager.login(userName, password, new ImAccountManager.OnLoginListener() {
            @Override
            public void onLogin(ApiResult result) {
                hideLoadingDialog();
                ToastUtil.showToastLong(result.mErrorMsg);
                if (result.mResult) {
                    //AccountPrefs.saveAccount(userName, password);
                    // 每次登陆成功,重置一次数据
                    XMPPManager.getInstance().getRosterManager().resetData();
                    Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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

    /**
     * 显示loading框
     */
    private void showLoadingDialog() {
        if (mISLoadingDialog == null) {
            mISLoadingDialog = new ISLoadingDialog(this);
            mISLoadingDialog.setMessage("正在登录...");
            mISLoadingDialog.setCancelable(false);
        }
        mISLoadingDialog.show();
    }

    /**
     * 隐藏loading框
     */
    private void hideLoadingDialog() {
        if (mISLoadingDialog != null) {
            mISLoadingDialog.dismiss();
        }
    }
}
