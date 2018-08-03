package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.meetyou.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationSettingActivity extends Activity {

    @BindView(R.id.notification_switch)
    CheckBox mNotificationCheckBox;
    @BindView(R.id.sound_switch_item)
    View mSoundSwitchItem;
    @BindView(R.id.sound_switch)
    CheckBox mSoundCheckBox;

    public static void launch(Context context){
        Intent intent = new Intent(context, NotificationSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        ButterKnife.bind(this);
        initContentView();
    }

    private void initContentView(){
        mNotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSoundSwitchItem.setVisibility(View.VISIBLE);
                }else{
                    mSoundSwitchItem.setVisibility(View.GONE);
                }
                AppConfigUtils.setNotificationEnable(getBaseContext(), isChecked);
            }
        });
        mSoundCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppConfigUtils.setNotificationSoundEnable(getBaseContext(), isChecked);
            }
        });
        mNotificationCheckBox.setChecked(AppConfigUtils.isNotificationEnable(this));
        if(mNotificationCheckBox.isChecked()){
            mSoundSwitchItem.setVisibility(View.VISIBLE);
        }else{
            mSoundSwitchItem.setVisibility(View.GONE);
        }
        mSoundCheckBox.setChecked(AppConfigUtils.isNotificationSoundEnable(this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
