package com.agmbat.meetyou.account;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.meetyou.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重置密码
 */
public class ResetPasswordActivity extends Activity {

    /**
     * 重置button
     */
    @BindView(R.id.btn_reset)
    Button mResetButton;

    /**
     * 获取验证码控件
     */
    @BindView(R.id.btn_get_verification_code)
    Button mGetVerificationCodeButton;

    /**
     * 用户名
     */
    @BindView(R.id.input_username)
    EditText mUserNameView;

    /**
     * 密码
     */
    @BindView(R.id.input_password)
    EditText mPasswordView;

    /**
     * 验证码
     */
    @BindView(R.id.et_code)
    EditText mVerificationCodeView;

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
                    updateResetButtonState();
                } else {
                    mUserNameView.requestFocus();
                    ToastUtil.showToastLong("请输入正确的手机号码！");
                }
            } else {
                mGetVerificationCodeButton.setEnabled(false);
                mResetButton.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        mLoginManager = new ImAccountManager(this);
        // setupViews
        mUserNameView.addTextChangedListener(new TelTextWatcher(mOnInputTelephoneListener));
        mPasswordView.addTextChangedListener(new TextChange());
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

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_get_verification_code)
    void onClickGetVerificationCode() {
        startTimer();
        String phone = mUserNameView.getText().toString();
        mLoginManager.getResetVerificationCode(phone, new ImAccountManager.OnGetVerificationCodeListener() {
            @Override
            public void onGetVerificationCode(ApiResult result) {
                ToastUtil.showToastLong(result.mErrorMsg);
            }
        });
    }

    @OnClick(R.id.btn_reset)
    void onClickReset() {
        resetPassword();
    }

    private void updateResetButtonState() {
        boolean verificationCodeValid = mVerificationCodeView.getText().length() > 0;
        boolean userNameValid = mUserNameView.getText().length() > 0;
        boolean passwordValid = mPasswordView.getText().length() > 0;
        if (verificationCodeValid & userNameValid & passwordValid) {
            mResetButton.setEnabled(true);
        } else {
            mResetButton.setEnabled(false);
        }
    }

    /**
     * 重置密码
     */
    private void resetPassword() {
        String name = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String verificationCode = mVerificationCodeView.getText().toString();
        mLoginManager.resetPassword(name, password, verificationCode, new ImAccountManager.OnResetPasswordListener() {
            @Override
            public void onResetPassword(ApiResult result) {
                ToastUtil.showToastLong(result.mErrorMsg);
                if (result.mResult) {
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateResetButtonState();
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

}
