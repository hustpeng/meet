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
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑昵称界面
 */
public class EditNameActivity extends Activity {

    /**
     * 昵称编辑框
     */
    @BindView(R.id.input_name)
    EditText mNameEditText;

    /**
     * 保存button
     */
    @BindView(R.id.btn_save)
    Button mSaveButton;

    /**
     * 用户信息
     */
    private VCardObject mVCardObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_edit_name);
        ButterKnife.bind(this);
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
        IM.get().fetchMyVCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 收到vcard更新信息
     *
     * @param vCardObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
        mNameEditText.setText(vCardObject.getNickname());
        mNameEditText.setSelection(mNameEditText.getText().toString().trim().length());
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击保存
     */
    @OnClick(R.id.btn_save)
    void onClickSave() {
        changeNickName();
    }

    /**
     * 修改昵称
     */
    private void changeNickName() {
        String nickName = mNameEditText.getText().toString();
        if (nickName.equals(mVCardObject.getNickname())) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            // 修改昵称的逻辑
            mVCardObject.setNickname(nickName);
            EventBus.getDefault().post(mVCardObject);
            XMPPManager.getInstance().getvCardManager().setMyVCard(mVCardObject);
            finish();
        }
    }

}