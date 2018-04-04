package com.agmbat.meetyou.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.imsdk.account.RegisterInfo;
import com.agmbat.meetyou.MainTabActivity;
import com.agmbat.meetyou.R;
import com.agmbat.text.PhoneNumberUtil;

/**
 * 注册界面
 */
public class RegisterActivity extends Activity {

    /**
     * 注册button
     */
    private Button mRegisterButton;

    /**
     * 获取验证码控件
     */
    private Button mGetVerificationCodeButton;

    /**
     * 用户名
     */
    private EditText mUserNameView;

    /**
     * 密码
     */
    private EditText mPasswordView;

    /**
     * 验证码
     */
    private EditText mVerificationCodeView;

    /**
     * 昵称
     */
    private EditText mNickNameView;

    /**
     * 性别
     */
    private TextView mGaderView;

    /**
     * 出生年份
     */
    private TextView mBirthYearView;

    /**
     * 邀请码
     */
    private EditText mInviteCodeView;

    private Counter mCountDownTimer;

    /**
     * 注册登陆管理
     */
    private ImAccountManager mLoginManager;

    private TelTextWatcher.OnInputTelephoneListener mOnInputTelephoneListener = new TelTextWatcher.OnInputTelephoneListener() {
        @Override
        public void onInputTelephone(boolean complete, boolean isTelephone) {
            if (complete) {
                if (isTelephone) {
                    mGetVerificationCodeButton.setEnabled(true);
                    updateRegisterButtonState();
                } else {
                    mUserNameView.requestFocus();
                    ToastUtil.showToastLong("请输入正确的手机号码！");
                }
            } else {
                mGetVerificationCodeButton.setEnabled(false);
                mRegisterButton.setEnabled(false);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_register);
        mLoginManager = new ImAccountManager(this);
        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    /**
     * 启动timer
     */
    private void startTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new Counter();
            mCountDownTimer.start();
        }
    }

    /**
     * 取消timer
     */
    private void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    private void setupViews() {
        findViewById(R.id.title_btn_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mUserNameView = (EditText) findViewById(R.id.input_username);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mVerificationCodeView = (EditText) findViewById(R.id.et_code);
        mUserNameView.addTextChangedListener(new TelTextWatcher(mOnInputTelephoneListener));
        mPasswordView.addTextChangedListener(new TextChange());
        mGetVerificationCodeButton = (Button) findViewById(R.id.btn_get_verification_code);
        mGetVerificationCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                String phone = mUserNameView.getText().toString();
                mLoginManager.getRegisterVerificationCode(phone, new ImAccountManager.OnGetVerificationCodeListener() {
                    @Override
                    public void onGetVerificationCode(ApiResult result) {
//                        mCountDownTimer.cancel();
//                        mGetVerificationCodeButton.setEnabled(true);
//                        mGetVerificationCodeButton.setText(getText(R.string.get_verification_code));
                        ToastUtil.showToastLong(result.mErrorMsg);
                    }
                });
            }
        });
        mNickNameView = (EditText) findViewById(R.id.input_nickname);
        mGaderView = (TextView) findViewById(R.id.input_gender);
        mBirthYearView = (TextView) findViewById(R.id.input_birthday);
        mInviteCodeView = (EditText) findViewById(R.id.input_invite_code);
        mRegisterButton = (Button) findViewById(R.id.btn_register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String name = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String verificationCode = mVerificationCodeView.getText().toString();
        String nickName = mNickNameView.getText().toString();
        String birthYear = mBirthYearView.getText().toString();
        String inviteCode = mInviteCodeView.getText().toString();
        if (ImAccountManager.DEBUG_CHECK_SMS && !PhoneNumberUtil.isValidPhoneNumber(name)) {
            ToastUtil.showToastLong("请使用手机号码注册账户！");
            return;
        }
        if (ImAccountManager.DEBUG_CHECK_SMS && TextUtils.isEmpty(verificationCode)) {
            ToastUtil.showToastLong("请填写手机号码，并获取验证码！");
            return;
        }
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(verificationCode)) {
            ToastUtil.showToastLong("请填写核心信息！");
            return;
        }
        showDialog();
        mRegisterButton.setEnabled(false);
        mGetVerificationCodeButton.setEnabled(false);
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setUserName(name);
        registerInfo.setPassword(password);
        registerInfo.setVerificationCode(verificationCode);
        registerInfo.setNickName(nickName);
        registerInfo.setGender(1);
        registerInfo.setBirthYear(birthYear);
        registerInfo.setInviteCode(inviteCode);

        mLoginManager.register(registerInfo, new ImAccountManager.OnRegisterListener() {
            @Override
            public void onRegister(ApiResult result) {
                dismissDialog();
                if (result.mResult) {
//                    Intent intent = new Intent(RegisterActivity.this, EditUserInfoActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    Intent intent = new Intent(RegisterActivity.this, MainTabActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mRegisterButton.setEnabled(true);
                    mGetVerificationCodeButton.setEnabled(true);

                }
                ToastUtil.showToastLong(result.mErrorMsg);
            }
        });
    }


    private void updateRegisterButtonState() {
        boolean verificationCodeValid = mVerificationCodeView.getText().length() > 0;
        boolean userNameValid = mUserNameView.getText().length() > 0;
        boolean passwordValid = mPasswordView.getText().length() > 0;
        if (verificationCodeValid & userNameValid & passwordValid) {
            mRegisterButton.setEnabled(true);
        } else {
            mRegisterButton.setEnabled(false);
        }
    }

    /**
     * EditText监听器
     */
    private class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateRegisterButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }

    /**
     * 定义一个倒计时的内部类
     */
    private class Counter extends CountDownTimer {

        public Counter() {
            this(60000, 1000);
        }

        /**
         * @param millisInFuture    总的时间
         * @param countDownInterval 间隔时间
         */
        public Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mGetVerificationCodeButton.setEnabled(true);
            mGetVerificationCodeButton.setText(getText(R.string.get_verification_code));
            cancelTimer();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mGetVerificationCodeButton.setEnabled(false);
            String text = "(" + millisUntilFinished / 1000 + ")秒";
            mGetVerificationCodeButton.setText(text);
        }
    }

    private void showDialog() {
//        getLoadingDialog("正在登录...").show();
    }

    private void dismissDialog() {
//        getLoadingDialog("正在登录").dismiss();
    }

//    private FlippingLoadingDialog getLoadingDialog(String msg) {
//        if (mLoadingDialog == null) {
//            mLoadingDialog = new FlippingLoadingDialog(this, msg);
//        }
//        return mLoadingDialog;
//    }

}
