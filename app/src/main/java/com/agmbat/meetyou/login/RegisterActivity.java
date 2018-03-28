package com.agmbat.meetyou.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.api.ApiResult;
import com.agmbat.text.PhoneNumberUtil;

/**
 * 注册界面
 */
public class RegisterActivity extends Activity {

    private Button mRegisterButton;

    /**
     * 获取验证码控件
     */
    private Button mGetVerificationCodeButton;
    private EditText mUserNameView;
    private EditText mPasswordView;
    private EditText mVerificationCodeView;

    private Counter mCountDownTimer;

    private LoginManager mLoginManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLoginManager = new LoginManager(this);
        setupViews();
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
        mUserNameView.addTextChangedListener(new TelTextChange());
        mPasswordView.addTextChangedListener(new TextChange());

        mGetVerificationCodeButton = (Button) findViewById(R.id.btn_get_verification_code);
        mGetVerificationCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCountDownTimer == null) {
                    mCountDownTimer = new Counter(60000, 1000); // 第一参数是总的时间，第二个是间隔时间
                }
                mCountDownTimer.start();
                String phone = mUserNameView.getText().toString();
                mLoginManager.getVerificationCode(phone, new LoginManager.OnGetVerificationCodeListener() {
                    @Override
                    public void onGetVerificationCode(ApiResult result) {
                        mCountDownTimer.cancel();
                        mGetVerificationCodeButton.setEnabled(true);
                        mGetVerificationCodeButton.setText("发送验证码");
                        ToastUtil.showToastLong(result.mErrorMsg);
                    }
                });
            }
        });

        mRegisterButton = (Button) findViewById(R.id.btn_register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        final String name = mUserNameView.getText().toString();
        final String pwd = mPasswordView.getText().toString();
        String code = mVerificationCodeView.getText().toString();
        if (!PhoneNumberUtil.isValidPhoneNumber(name)) {
            ToastUtil.showToastLong("请使用手机号码注册账户！");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToastLong("请填写手机号码，并获取验证码！");
            return;
        }
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)
                || TextUtils.isEmpty(code)) {
            ToastUtil.showToastLong("请填写核心信息！");
            return;
        }
        showDialog();
        mRegisterButton.setEnabled(false);
        mGetVerificationCodeButton.setEnabled(false);

        mLoginManager.register(name, pwd, code, new LoginManager.OnRegisterListener() {
            @Override
            public void onRegister(ApiResult result) {
                dismissDialog();
                if (result.mResult) {
//                    Intent intent = new Intent(RegisterActivity.this, EditUserInfoActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    finish();
                } else {
                    mRegisterButton.setEnabled(true);
                    mGetVerificationCodeButton.setEnabled(true);

                }
                ToastUtil.showToastLong(result.mErrorMsg);
            }
        });
    }


    // 手机号 EditText监听器
    private class TelTextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            String phone = mUserNameView.getText().toString();
            if (phone.length() == 11) {
                if (PhoneNumberUtil.isValidPhoneNumber(phone)) {
                    mGetVerificationCodeButton.setTextColor(0xFFFFFFFF);
                    mGetVerificationCodeButton.setEnabled(true);
                    mRegisterButton.setTextColor(0xFFFFFFFF);
                    mRegisterButton.setEnabled(true);
                } else {
                    mUserNameView.requestFocus();
                    ToastUtil.showToastLong("请输入正确的手机号码！");
                }
            } else {
                mGetVerificationCodeButton.setTextColor(0xFFD0EFC6);
                mGetVerificationCodeButton.setEnabled(false);
                mRegisterButton.setTextColor(0xFFD0EFC6);
                mRegisterButton.setEnabled(true);
            }
        }
    }

    // EditText监听器
    private class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            updateRegisterButtonState();
        }
    }

    private void updateRegisterButtonState() {
        boolean VerificationCodeValid = mVerificationCodeView.getText().length() > 0;
        boolean userNameValid = mUserNameView.getText().length() > 0;
        boolean passwordValid = mPasswordView.getText().length() > 0;
        if (VerificationCodeValid & userNameValid & passwordValid) {
            mRegisterButton.setTextColor(0xFFFFFFFF);
            mRegisterButton.setEnabled(true);
        } else {
            mRegisterButton.setTextColor(0xFFD0EFC6);
            mRegisterButton.setEnabled(false);
        }
    }

    /* 定义一个倒计时的内部类 */
    private class Counter extends CountDownTimer {

        public Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mGetVerificationCodeButton.setEnabled(true);
            mGetVerificationCodeButton.setText("发送验证码");
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
