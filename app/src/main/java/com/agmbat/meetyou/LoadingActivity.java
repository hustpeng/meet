package com.agmbat.meetyou;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.agmbat.android.permissions.PermissionArrayAction;
import com.agmbat.android.permissions.Permissions;

/**
 * Loading界面
 */
public class LoadingActivity extends FragmentActivity {

    private static final String TAG = LoadingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

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
                } else {
                    showNoPermissionDialog(LoadingActivity.this);
                }
            }
        });

    }

    /**
     * 显示没有权限的对话框并退出
     *
     * @param activity
     */
    private void showNoPermissionDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("请允许相关权限")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

}
