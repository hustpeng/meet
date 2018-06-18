package com.agmbat.meetyou.discovery.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 找群的Activity界面
 */
public class GroupSearchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_group_search);
        ButterKnife.bind(this);
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

}
