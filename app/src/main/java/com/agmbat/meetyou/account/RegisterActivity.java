package com.agmbat.meetyou.account;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.imsdk.account.RegisterInfo;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.MainTabActivity;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.GenderHelper;
import com.agmbat.meetyou.util.StringUtil;
import com.agmbat.picker.NumberPicker;
import com.agmbat.picker.SinglePicker;
import com.agmbat.picker.helper.GenderItem;
import com.agmbat.picker.helper.PickerHelper;
import com.agmbat.text.StringParser;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册界面
 */
public class RegisterActivity extends Activity {

    /**
     * 注册button
     */
    @BindView(R.id.btn_register)
    Button mRegisterButton;

    /**
     * 下一步Button
     */
    @BindView(R.id.btn_next)
    Button mNextButton;

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
    @BindView(R.id.input_code)
    EditText mVerificationCodeView;

    /**
     * 昵称
     */
    @BindView(R.id.input_nickname)
    EditText mNickNameView;

    /**
     * 性别
     */
    @BindView(R.id.input_gender)
    TextView mGenderView;

    /**
     * 出生年份
     */
    @BindView(R.id.input_birth_year)
    TextView mBirthYearView;

    /**
     * 邀请码
     */
    @BindView(R.id.input_invite_code)
    EditText mInviteCodeView;


    @BindView(R.id.page1)
    View mPage1View;

    @BindView(R.id.page2)
    View mPage2View;

    /**
     * 注册Loading框
     */
    private ISLoadingDialog mLoadingDialog;

    private Counter mCountDownTimer;

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
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mUserNameView.addTextChangedListener(new TelTextWatcher(mOnInputTelephoneListener));
        mPasswordView.addTextChangedListener(new TextChange());
        mGenderView.setText(GenderHelper.female());
        mBirthYearView.setText("1990");
        showPage1();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    @Override
    public void onBackPressed() {
        onClickBack();
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        if (mPage2View.isShown()) {
            showPage1();
            return;
        }
        finish();
    }


    /**
     * 缓存已请求的code
     */
    private Map<String, String> mCacheCode = new HashMap<>();

    @OnClick(R.id.btn_next)
    void onClickNext() {
        final String name = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String verificationCode = mVerificationCodeView.getText().toString();

        if (ImAccountManager.DEBUG_CHECK_SMS && !StringUtil.isMobile(name)) {
            ToastUtil.showToastLong("请使用手机号码注册账户！");
            return;
        }
        if (ImAccountManager.DEBUG_CHECK_SMS && TextUtils.isEmpty(verificationCode)) {
            ToastUtil.showToastLong("请填写手机号码，并获取验证码！");
            return;
        }

        if (verificationCode.length() != 4) {
            ToastUtil.showToastLong("请填正确的验证码！");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToastLong("请输入密码！");
            return;
        }

        String code = mCacheCode.get(name);
        if (verificationCode.equals(code)) {
            showPage2();
        } else {
            showDialog();
            ImAccountManager.verificationCode(name, verificationCode, new ImAccountManager.OnVerificationCodeListener() {
                @Override
                public void onVerificationCode(ApiResult result) {
                    dismissDialog();
                    if (result.mResult) {
                        mCacheCode.put(name, password);
                        showPage2();
                    } else {
                        mRegisterButton.setEnabled(true);
                        mGetVerificationCodeButton.setEnabled(true);
                    }
                    ToastUtil.showToastLong(result.mErrorMsg);
                }
            });
        }
    }


    /**
     * 选择性别
     */
    @OnClick(R.id.btn_gender)
    void onClickGender() {
        String text = mGenderView.getText().toString();
        int value = GenderHelper.getGender(text);
        GenderItem item = GenderItem.valueOf(value);
        PickerHelper.showGenderPicker(this, item, new SinglePicker.OnItemPickListener<GenderItem>() {
            @Override
            public void onItemPicked(int index, GenderItem item) {
                mGenderView.setText(item.mName);
            }

        });
    }

    @OnClick(R.id.btn_birth_year)
    void onClickBirthYear() {
        int year = StringParser.parseInt(mBirthYearView.getText().toString());
        PickerHelper.showYearPicker(this, year, new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                mBirthYearView.setText(item.toString());
            }
        });
    }

    @OnClick(R.id.btn_get_verification_code)
    void onClickGetVerificationCode() {
        startTimer();
        String phone = mUserNameView.getText().toString();
        ImAccountManager.getRegisterVerificationCode(phone, new ImAccountManager.OnGetVerificationCodeListener() {
            @Override
            public void onGetVerificationCode(ApiResult result) {
                mCountDownTimer.cancel();
                mGetVerificationCodeButton.setEnabled(true);
                mGetVerificationCodeButton.setText(getText(R.string.get_verification_code));
                ToastUtil.showToastLong(result.mErrorMsg);
            }
        });
    }

    @OnClick(R.id.btn_register)
    void onClickRegister() {
        register();
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

    private void register() {
        String name = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String verificationCode = mVerificationCodeView.getText().toString();
        String nickName = mNickNameView.getText().toString();
        int gender = GenderHelper.getGender(mGenderView.getText().toString());
        int birthYear = StringParser.parseInt(mBirthYearView.getText().toString());
        final String inviteCode = mInviteCodeView.getText().toString();
        if (ImAccountManager.DEBUG_CHECK_SMS && !StringUtil.isMobile(name)) {
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

        if (!TextUtils.isEmpty(inviteCode)) {
            if (inviteCode.startsWith("0")) {
                ToastUtil.showToast("邀请码不能以0作为开头");
                return;
            }
            if (inviteCode.length() < 4) {
                ToastUtil.showToast("邀请码不能少于4个数字");
                return;
            }
        }
        showDialog();
        mRegisterButton.setEnabled(false);
        mGetVerificationCodeButton.setEnabled(false);
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setUserName(name);
        registerInfo.setPassword(password);
        registerInfo.setVerificationCode(verificationCode);
        registerInfo.setNickName(nickName);
        registerInfo.setGender(gender);
        registerInfo.setBirthYear(birthYear);
        registerInfo.setInviteCode(inviteCode);

        ImAccountManager.register(registerInfo, new ImAccountManager.OnRegisterListener() {
            @Override
            public void onRegister(ApiResult result) {
                dismissDialog();
                if (result.mResult) {
                    ISAlertDialog dialog = new ISAlertDialog(RegisterActivity.this);
                    if(TextUtils.isEmpty(inviteCode)){
                        dialog.setMessage("欢迎您注册成为真约会员，你目前是未认证会员，如想提升信用度和使用更强大功能，请免费升级成为认证会员。");
                    }else{
                        dialog.setMessage(String.format("因%s邀请你注册会员，你和他/她同时增加100缘币，马上加%s为好友吧！", inviteCode, inviteCode));
                    }
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(RegisterActivity.this, MainTabActivity.class);
                            startActivity(intent);
                            finish();
                            EventBus.getDefault().post(new RegisterSuccessEvent());
                        }
                    });
                    dialog.show();
                } else {
                    mRegisterButton.setEnabled(true);
                    mGetVerificationCodeButton.setEnabled(true);
                    ToastUtil.showToastLong(result.mErrorMsg);
                }
            }
        });
    }


    private void updateRegisterButtonState() {
        boolean verificationCodeValid = mVerificationCodeView.getText().length() > 0;
        boolean userNameValid = mUserNameView.getText().length() > 0;
        boolean passwordValid = mPasswordView.getText().length() > 0;
        if (verificationCodeValid & userNameValid & passwordValid) {
            mRegisterButton.setEnabled(true);
            mNextButton.setEnabled(true);
        } else {
            mRegisterButton.setEnabled(false);
            mNextButton.setEnabled(false);
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
        if (mLoadingDialog == null) {
            mLoadingDialog = new ISLoadingDialog(this);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setMessage("正在处理, 请稍候...");
        }
        mLoadingDialog.show();
    }

    private void dismissDialog() {
        UiUtils.dismissDialog(mLoadingDialog);
    }

    private void showPage1() {
        mPage1View.setVisibility(View.VISIBLE);
        mPage2View.setVisibility(View.INVISIBLE);
    }

    private void showPage2() {
        mPage1View.setVisibility(View.INVISIBLE);
        mPage2View.setVisibility(View.VISIBLE);
    }
}
