package com.agmbat.meetyou.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.agmbat.android.utils.KeyboardUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.meetyou.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改密码界面
 */
public class ChangePasswordActivity extends Activity {

    @BindView(R.id.input_old_pwd)
    EditText mInputCurrentPwd;

    @BindView(R.id.input_new_pwd)
    EditText mInputNewPwd;

    @BindView(R.id.input_confirm_pwd)
    EditText mInputConfirmPwd;

    private ImAccountManager mLoginManager;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        mLoginManager = new ImAccountManager(this);
        TextView tv = (TextView) findViewById(R.id.username);
        tv.setText(mLoginManager.getConnectionUserName());
        mInputCurrentPwd.requestFocus();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_done)
    void onClickDone() {
        KeyboardUtils.hideInputMethod(mInputConfirmPwd);

        String currentPwd = mInputCurrentPwd.getText().toString().trim();
        String newPwd = mInputNewPwd.getText().toString();
        String confirmPwd = mInputConfirmPwd.getText().toString();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.change_password_processing));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mLoginManager.changePassword(currentPwd, newPwd, confirmPwd, new ImAccountManager.OnChangePasswordListener() {
            @Override
            public void onChangePassword(ApiResult result) {
                if (null != mProgressDialog && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                ToastUtil.showToastLong(result.mErrorMsg);
                if (result.mResult) {
                    finish();
                }
            }
        });
    }

}
