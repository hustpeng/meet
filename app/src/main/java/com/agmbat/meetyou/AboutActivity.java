package com.agmbat.meetyou;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.android.utils.WindowUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于界面
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

}
