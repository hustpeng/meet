/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * 权限管理
 *
 * @author mayimchen
 * @since 2016-10-30
 */
package com.agmbat.android.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Android 6.0 权限管理
 * 由于请求权限需要Activity承接返回值
 */
public class RequestPermissionActivity extends Activity {

    private static final String KEY_REQUEST_CODE = "requestCode";
    private static final String KEY_PERMISSIONS = "permsToRequest";

    public static void request(Context context, String[] permissions, int requestCode) {
        Intent intent = new Intent(context, RequestPermissionActivity.class);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        intent.putExtra(KEY_REQUEST_CODE, requestCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            // 去掉切换动画关键
            activity.overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handleIntent(Intent intent) {
        String[] permissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
        if (permissions == null || permissions.length == 0) {
            finish();
            return;
        }
        int requestCode = intent.getIntExtra(KEY_REQUEST_CODE, -1);
        if (permissions.length == 1 && Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permissions[0])) {
            Intent permissionIntent =
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(permissionIntent, requestCode);
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int[] grantResults;
        if (Settings.canDrawOverlays(this)) {
            grantResults = new int[]{PackageManager.PERMISSION_GRANTED};
        } else {
            grantResults = new int[]{PackageManager.PERMISSION_DENIED};
        }
        String[] permissions = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // 去掉切换动画关键
        overridePendingTransition(0, 0);
    }
}
