package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑择友要求
 */
public class EditDemandActivity extends Activity {

    /**
     * 编辑框
     */
    @BindView(R.id.input_text)
    EditText mEditText;

    @BindView(R.id.tips)
    TextView mTipsView;

    /**
     * 保存button
     */
    @BindView(R.id.btn_save)
    Button mSaveButton;

    /**
     * 用户信息
     */
    private VCardExtendObject mVCardObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_demand);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mEditText.getText().toString();
                int remainder = 100 - text.length();
                mTipsView.setText(String.valueOf(remainder));
            }
        });
        IM.get().fetchMyVCardExtend();
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
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginUserUpdateEvent event) {
        mVCardObject = event.mVCardExtendObject;
        mEditText.setText(mVCardObject.getDemand());
        mEditText.setSelection(mEditText.getText().toString().trim().length());
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
        String text = mEditText.getText().toString();
        if (text.equals(mVCardObject.getDemand())) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            mVCardObject.setDemand(text);
            EventBus.getDefault().post(mVCardObject);
            XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardObject);
            finish();
        }
    }
}
