package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑个人介绍
 */
public class EditIntroduceActivity extends Activity {

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
    private VCardExtendObject mVCardObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_introduce);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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
     * @param vCardObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardExtendObject vCardObject) {
        mVCardObject = vCardObject;
        mNameEditText.setText(vCardObject.getIntroduce());
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
        String text = mNameEditText.getText().toString();
        if (text.equals(mVCardObject.getIntroduce())) {
            // 未修改
            finish();
        } else {
            // TODO 需要添加loading框
            mVCardObject.setIntroduce(text);
            EventBus.getDefault().post(mVCardObject);
            XMPPManager.getInstance().getvCardExtendManager().setMyVCardExtend(mVCardObject);
            finish();
        }
    }
}
