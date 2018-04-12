package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;

/**
 * 编辑昵称界面
 */
public class EditNameActivity extends Activity {

    private EditText mNameEditText;
    private Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_name);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSaveButton = (Button) findViewById(R.id.btn_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNickName();
            }
        });
        mNameEditText = (EditText) findViewById(R.id.input_name);
        mNameEditText.setText("Test");
        mNameEditText.setSelection(mNameEditText.getText().toString().trim().length());
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNameEditText.getText().toString().trim().length() > 0) {
                    mSaveButton.setEnabled(true);
                } else {
                    mSaveButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void changeNickName() {
        String nickName = mNameEditText.getText().toString();
        // TODO 添加修改昵称的逻辑
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

}
