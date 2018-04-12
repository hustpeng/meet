package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imagepicker.ImagePicker;
import com.agmbat.imagepicker.loader.UILImageLoader;
import com.agmbat.imagepicker.ui.ImageGridActivity;
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

    /**
     * 拍照
     */
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;

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
                new String[]{"拍照", "从手机相册选择", "保存图片"},
                this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(AlertView view, int position) {
                if (position == 0) {
                    takePicture();
                } else if (position == 1) {

                } else if (position == 2) {

                }
            }
        }).setCancelable(true).show();
    }


    /**
     * 拍照
     */
    private void takePicture() {
        ImagePicker.getInstance().setImageLoader(new UILImageLoader());
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {

        }
    }
}
