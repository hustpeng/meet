package com.agmbat.meetyou.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索用户界面
 */
public class SearchUserActivity extends Activity {

    @BindView(R.id.input_text)
    EditText mEditText;

    @BindView(R.id.search_result_tips)
    RelativeLayout mNoResultTipView;

    @BindView(R.id.btn_search)
    LinearLayout mSearchButton;

    @BindView(R.id.search_text)
    TextView mSearchTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);
        mNoResultTipView.setVisibility(View.GONE);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = mEditText.getText().toString().trim();
                mNoResultTipView.setVisibility(View.GONE);
                if (content.length() > 0) {
                    mSearchButton.setVisibility(View.VISIBLE);
                    mSearchTextView.setText(content);
                } else {
                    mSearchButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_search)
    void onClickSearch() {
        String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast("内容不能为空");
            return;
        }
        // TODO 显示loading框
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setBareJid("123");
        if (contactInfo == null) {
            mNoResultTipView.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.GONE);
        } else {
            UserInfoActivity.viewUserInfo(this, contactInfo);
        }
    }
}
