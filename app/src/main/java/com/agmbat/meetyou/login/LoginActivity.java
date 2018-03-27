package com.agmbat.meetyou.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.meetyou.MainTabActivity;
import com.agmbat.meetyou.R;

/**
 * 登陆界面
 */
public class LoginActivity extends FragmentActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button mLoginButton;
    private EditText mUserNameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
            }
        });

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mUserNameView = (EditText) findViewById(R.id.input_username);
        mUserNameView.addTextChangedListener(new TextChange());
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mPasswordView.addTextChangedListener(new TextChange());

        findViewById(R.id.login_problem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
