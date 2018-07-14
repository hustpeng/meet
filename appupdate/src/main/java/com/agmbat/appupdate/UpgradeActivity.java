package com.agmbat.appupdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * 显示升级的对话框
 */
public class UpgradeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpdateDialog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showUpdateDialog();
    }

    private void showUpdateDialog() {
        AppVersionInfo info = AppVersionHelper.getAppVersionInfo();
        Dialog dialog = AppVersionHelper.showUpdateDialog(this, info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                overridePendingTransition(0, 0);
            }

        });
        if (dialog == null) {
            finish();
            overridePendingTransition(0, 0);
        }
    }
}
