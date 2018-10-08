package com.agmbat.meetyou.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;
import com.agmbat.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看图片的Activity
 */
public class ViewImageActivity extends Activity {

    @BindView(R.id.image)
    PhotoView mPhotoView;

    /**
     * 查看图片
     *
     * @param context
     * @param url
     */
    public static void viewImage(Context context, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setClass(context, ViewImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        String url = uri.toString();
        ImageManager.displayImage(url, mPhotoView);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.image)
    void onClickBack() {
        finish();
    }

}
