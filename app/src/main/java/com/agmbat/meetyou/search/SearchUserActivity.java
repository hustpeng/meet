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
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.feedback.FeedbackManager;
import com.agmbat.imsdk.feedback.OnFeedbackListener;
import com.agmbat.imsdk.searchuser.OnSearchUserListener;
import com.agmbat.imsdk.searchuser.SearchUserManager;
import com.agmbat.imsdk.searchuser.SearchUserResult;
import com.agmbat.isdialog.ISLoadingDialog;
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

    /**
     * 搜索用户
     */
    @BindView(R.id.btn_search_user)
    LinearLayout mSearchUserButton;

    /**
     * 搜索群
     */
    @BindView(R.id.btn_search_group)
    LinearLayout mSearchGroupButton;

    @BindView(R.id.search_text)
    TextView mSearchTextView;

    @BindView(R.id.search_text2)
    TextView mSearchTex2tView;

    /**
     * loading对话框
     */
    private ISLoadingDialog mISLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);
        mNoResultTipView.setVisibility(View.GONE);
        mEditText.addTextChangedListener(new TextChangedListener());
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

    /**
     * 点击搜索用户
     */
    @OnClick(R.id.btn_search_user)
    void onClickSearch() {
        String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast("内容不能为空");
            return;
        }
        if (content.length() < 4) {
            ToastUtil.showToast("请输入最少4个数字");
            return;
        }
        showLoadingDialog();

        // 搜索用户
        SearchUserManager.searchUser(content, new OnSearchUserListener() {
            @Override
            public void onSearchUser(SearchUserResult result) {
                hideLoadingDialog();
                ToastUtil.showToast(result.mErrorMsg);
                if (result.mResult) {
                    ContactInfo contactInfo = result.mData;
                    if (contactInfo == null) {
                        mNoResultTipView.setVisibility(View.VISIBLE);
                        mSearchUserButton.setVisibility(View.GONE);
                    } else {
                        ViewUserHelper.openStrangerDetail(SearchUserActivity.this, contactInfo);
                    }
                }
            }

        });
    }

    @OnClick(R.id.btn_search_group)
    void onClickSearchGroup() {
        String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast("内容不能为空");
            return;
        }
        if (content.length() < 4) {
            ToastUtil.showToast("请输入最少4个数字");
            return;
        }
        showLoadingDialog();

        // 搜索用户
        SearchUserManager.searchGroup(content, new OnSearchUserListener() {
            @Override
            public void onSearchUser(SearchUserResult result) {
                hideLoadingDialog();
                ToastUtil.showToast(result.mErrorMsg);
                if (result.mResult) {
                    ContactInfo contactInfo = result.mData;
                    if (contactInfo == null) {
                        mNoResultTipView.setVisibility(View.VISIBLE);
                        mSearchUserButton.setVisibility(View.GONE);
                    } else {
//                        ViewUserHelper.openStrangerDetail(SearchUserActivity.this, contactInfo);
                    }
                }
            }

        });
    }

    /**
     * 显示loading框
     */
    private void showLoadingDialog() {
        if (mISLoadingDialog == null) {
            mISLoadingDialog = new ISLoadingDialog(this);
            mISLoadingDialog.setMessage("正在搜索...");
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

    private class TextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String content = mEditText.getText().toString().trim();
            mNoResultTipView.setVisibility(View.GONE);
            if (content.length() >= 4) {
                mSearchUserButton.setVisibility(View.VISIBLE);
                mSearchTextView.setText(content);

                mSearchGroupButton.setVisibility(View.VISIBLE);
                mSearchTex2tView.setText(content);
            } else {
                mSearchUserButton.setVisibility(View.GONE);
                mSearchGroupButton.setVisibility(View.GONE);
            }

            if (content.length() == 11) {
                // 如果为手机号, 则显示搜索用户
                mSearchGroupButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
