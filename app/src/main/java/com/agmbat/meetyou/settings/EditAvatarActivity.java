package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.isalertview.AlertView;
import com.agmbat.isalertview.OnItemClickListener;
import com.agmbat.meetyou.R;
import com.agmbat.photoview.PhotoView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑头像界面
 */
public class EditAvatarActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_avatar);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");
        PhotoView photoView = findViewById(R.id.pv);
        ImageManager.displayImage(url, photoView);
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

    /**
     * 点击menu
     */
    @OnClick(R.id.title_menu_more)
    void onClickMenuMore() {
        new AlertView(null, null, "取消", null,
                new String[]{"拍照", "从相册中选择", "保存图片"},
                this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(AlertView view, int position) {

            }
        }).setCancelable(true).show();
    }

}
