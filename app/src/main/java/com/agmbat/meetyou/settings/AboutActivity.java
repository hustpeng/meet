package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.appupdate.AppVersionHelper;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于界面
 */
public class AboutActivity extends Activity {

    @BindView(R.id.version)
    TextView mVersionTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initContentView();
    }

    private void initContentView() {
        mVersionTv.setText("版本号：" + SystemUtil.getAppVersion(this));
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

    @OnClick(R.id.visit_offical_web)
    void onClickOfficalWeb() {
        SystemUtil.openBrowser(this, "http://www.baidu.com");
    }


    @OnClick(R.id.user_feedback)
    void onClickFeedback() {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    /**
     * 点击检查更新
     */
    @OnClick(R.id.check_update)
    void onClickCheckUpdate() {
        AppVersionHelper.checkVersion(this);
    }


}
