package com.agmbat.meetyou.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.meetyou.R;

/**
 * 修改密码界面
 */
public class ChangePasswordActivity extends Activity {

    private EditText mInputCurrentPwd;
    private EditText mInputNewPwd;
    private EditText mInputConfirmPwd;

    private ImAccountManager mLoginManager;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_change_password);
        findViewById(R.id.title_btn_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_done).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDone();
            }
        });

        mLoginManager = new ImAccountManager(this);
        TextView tv = (TextView) findViewById(R.id.username);
        tv.setText(mLoginManager.getConnectionUserName());
        mInputCurrentPwd = (EditText) findViewById(R.id.input_old_pwd);
        mInputNewPwd = (EditText) findViewById(R.id.input_new_pwd);
        mInputConfirmPwd = (EditText) findViewById(R.id.input_confirm_pwd);
        mInputCurrentPwd.requestFocus();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private void handleDone() {
        ViewUtils.hideInputMethod(mInputConfirmPwd);

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
