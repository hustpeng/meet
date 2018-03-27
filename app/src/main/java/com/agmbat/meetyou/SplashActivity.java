package com.agmbat.meetyou;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.permissions.PermissionArrayAction;
import com.agmbat.android.permissions.Permissions;
import com.agmbat.meetyou.login.LoginActivity;
import com.nostra13.universalimageloader.core.download.Scheme;

/**
 * 闪屏页面
 */
public class SplashActivity extends Activity {

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            finish();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String uri = Scheme.wrapUri("drawable", String.valueOf(R.drawable.splash));
        ImageManager.displayImage(uri, imageView);
        imageView.setImageResource(R.drawable.splash);
        setContentView(imageView);
        Permissions.request(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, new PermissionArrayAction() {
            @Override
            public void onResult(String[] permissions, boolean[] grantResults) {
                if (Permissions.checkResult(grantResults)) {
//                    addFragment();
                    mHandler.postDelayed(mRunnable, 3000);
                } else {
                    showNoPermissionDialog();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * 显示没有权限的对话框并退出
     */
    private void showNoPermissionDialog() {
        new AlertDialog.Builder(this)
                .setMessage("请允许相关权限")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
}
