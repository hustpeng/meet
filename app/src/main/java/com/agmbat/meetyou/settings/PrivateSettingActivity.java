package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.blocklist.BlockListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivateSettingActivity extends Activity {

    @BindView(R.id.unauth_user_denied_switch)
    CheckBox mUnauthDeniedCheckBox;

    public static void launch(Context context) {
        Intent intent = new Intent(context, PrivateSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_setting);
        ButterKnife.bind(this);
        initContentView();
    }

    @OnClick(R.id.title_btn_back)
    void onClickBackBtn() {
        finish();
    }

    @OnClick(R.id.block_list_item)
    void onClickBlockList() {
        BlockListActivity.launch(getApplicationContext());
    }

    private void initContentView() {
        mUnauthDeniedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppConfigUtils.setUnauthDeniedEnable(getBaseContext(), isChecked);
            }
        });
        mUnauthDeniedCheckBox.setChecked(AppConfigUtils.isUnauthDeniedEnable(this));
    }
}
