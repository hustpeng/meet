/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * 权限管理
 *
 * @author mayimchen
 * @since 2016-10-30
 */
package com.agmbat.android.permissions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agmbat.log.Log;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Android 6.0 权限管理
 */
public class Permissions {

    private static final Map<Integer, PendingAction> PENDING_ACTION_MAP = new HashMap<>();

    /**
     * 用于自动生成RequestCode
     */
    private static int sRequestCode = 153;

    /**
     * 自动生成requestCode
     *
     * @return
     */
    static int genRequestCode() {
        sRequestCode++;
        if (sRequestCode > 240) {
            sRequestCode = 150;
        }
        return sRequestCode;
    }

    /**
     * 调起系统应用的权限管理页面，依赖与android.permission.GRANT_RUNTIME_PERMISSIONS权限（
     * 正式平台签名apk才会授予）若自身应用没有平台权限，建议引导用户跳转到setting页面即可
     */
    public static void openManageAppPermission(Activity activity, int pendingRequestCode) {
        if (null == activity) {
            Log.w("openManageAppPermission activity is null ");
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent.putExtra("android.intent.extra.PACKAGE_NAME", activity.getPackageName());
            // 屏蔽掉权限管理页面右上角的应用详情按钮
            intent.putExtra("hideInfoButton", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // 使用startActivityForResult，避免权限管理器操作未完成，而权限申请结果已回调
            activity.startActivityForResult(intent, pendingRequestCode);
        } catch (ActivityNotFoundException e) {
            Log.w("start action android.intent.action.MANAGE_APP_PERMISSIONS ActivityNotFoundException error");
        } catch (SecurityException ee) {
            Log.w("start action android.intent.action.MANAGE_APP_PERMISSIONS java.lang.SecurityException==Permission "
                    + "Denial error");
        }
    }

    /**
     * 请求单个权限
     *
     * @param context
     * @param permission
     * @param action
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void request(Activity context, String permission, PermissionAction action) {
        // 6.0以下版本直接同意使用权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            granted(action);
            return;
        }
        if (Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
            // 此权限需要特殊处理
            if (!Settings.canDrawOverlays(context)) {
                // 执行没有获取权限的操作
                requestPermission(context, permission, action);
            } else {
                granted(action);
            }
            return;
        }

        // 检测权限
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // 执行获取权限后的操作
            granted(action);
            return;
        }
        // 判断是否还能显示权限对话框
        boolean shouldShowRequest = shouldShowRequestPermissionRationale(context, permission);
        if (shouldShowRequest) {
            // 执行没有获取权限的操作
            requestPermission(context, permission, action);
        } else {
            // 已拒绝,并且不会再显示权限对话框,需要提示用户在Settings->App->权限中打开权限
            denied(action);
        }
    }

    /**
     * 同时请求多个权限
     *
     * @param context
     * @param permissions
     * @param action
     */
    public static void request(Activity context, String[] permissions, PermissionArrayAction action) {
        // 6.0以下版本直接同意使用权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            granted(action, permissions);
            return;
        }

        boolean[] result = new boolean[permissions.length];
        List<String> permsToRequestList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            // 检测权限
            String permission = permissions[i];
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            boolean granted = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            if (!granted) {
                // 判断是否还能显示权限对话框
                boolean shouldShowRequest = shouldShowRequestPermissionRationale(context, permission);
                if (shouldShowRequest) {
                    permsToRequestList.add(permission);
                }
            }
            result[i] = granted;
        }
        if (permsToRequestList.size() == 0) {
            if (action != null) {
                action.onResult(permissions, result);
            }
            return;
        }

        String[] permsToRequest = permsToRequestList.toArray(new String[permsToRequestList.size()]);
        requestPermission(context, permissions, permsToRequest, result, action);
    }

    /**
     * 检测请求权限的结果,是否全部允许
     *
     * @param grantResults 需要被检测的结果
     * @return
     */
    public static boolean checkResult(boolean[] grantResults) {
        for (boolean r : grantResults) {
            if (!r) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     *
     * @param context
     * @param permissions
     * @param permsToRequest
     * @param result
     * @param action
     */
    private static void requestPermission(Activity context, String[] permissions, String[] permsToRequest,
                                          boolean[] result, PermissionArrayAction action) {
        int code = genRequestCode();
        PendingAction pendingAction = new PendingAction(permissions, result, permsToRequest, action);
        PENDING_ACTION_MAP.put(code, pendingAction);
        RequestPermissionActivity.request(context, permsToRequest, code);
    }

    /**
     * 请求权限
     *
     * @param context
     * @param permission
     * @param action
     */
    private static void requestPermission(Activity context, String permission, final PermissionAction action) {
        String[] permissions = new String[]{permission};
        boolean[] result = new boolean[1];
        result[0] = false;
        PermissionArrayAction arrayAction = new PermissionArrayAction() {
            @Override
            public void onResult(String[] permissions, boolean[] grantResults) {
                if (grantResults[0]) {
                    granted(action);
                } else {
                    denied(action);
                }
            }
        };
        requestPermission(context, permissions, permissions, result, arrayAction);
    }

    /**
     * 根据用户选择的结果，处理权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PendingAction pendingAction = PENDING_ACTION_MAP.remove(requestCode);
        if (pendingAction != null) {
            pendingAction.onRequestPermissionsResult(permissions, grantResults);
        }
        if (PENDING_ACTION_MAP.size() != 0) {
            Log.e("onRequestPermissionsResult: map size:" + PENDING_ACTION_MAP.size());
            PENDING_ACTION_MAP.clear();
        }
    }

    /**
     * 执行获取权限后的操作
     *
     * @param action
     */
    static void granted(PermissionAction action) {
        if (action != null) {
            action.onGranted();
        }
    }

    /**
     * 执行没有获取权限的操作
     *
     * @param action
     */
    static void denied(PermissionAction action) {
        if (action != null) {
            action.onDenied();
        }
    }

    /**
     * 执行获取权限后的操作
     *
     * @param action
     * @param permissions
     */
    static void granted(PermissionArrayAction action, String[] permissions) {
        if (action != null) {
            boolean[] result = new boolean[permissions.length];
            Arrays.fill(result, true);
            action.onResult(permissions, result);
        }
    }

    /**
     * 判断是否再弹出对话框提示权限申请
     *
     * @param activity
     * @param permission
     * @return
     */
    static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return true;
//        if (isFirstCheck(activity, permission)) {
//            return true;
//        }
//        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 是否第一次检测权限
     */
    private static boolean isFirstCheck(Context context, String permission) {
        SharedPreferences prefs = context.getSharedPreferences("permission_check", Context.MODE_PRIVATE);
        boolean first = prefs.getBoolean(permission, true);
        if (first) {
            prefs.edit().putBoolean(permission, false).commit();
        }
        return first;
    }

}
