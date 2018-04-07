package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;

/**
 * 我的信息界面
 */
public class MyInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_my_info);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

}
