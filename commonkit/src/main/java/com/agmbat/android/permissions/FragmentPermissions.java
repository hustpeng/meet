package com.agmbat.android.permissions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Fragment中权限管理
 */
public class FragmentPermissions {

    private final Map<Integer, WeakReference<PendingAction>> mPendingActionMap = new HashMap<>();

    private Fragment mFragment;

    public FragmentPermissions(Fragment fragment) {
        mFragment = fragment;
    }

    /**
     * 根据用户选择的结果，处理权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        WeakReference<PendingAction> wrfPendingAction = mPendingActionMap.remove(requestCode);
        if (wrfPendingAction != null) {
            PendingAction pendingAction = wrfPendingAction.get();
            if (pendingAction != null) {
                pendingAction.onRequestPermissionsResult(permissions, grantResults);
            }
        }
    }

    /**
     * 请求单个权限
     *
     * @param permission
     * @param action
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void request(String permission, PermissionAction action) {
        // 6.0以下版本直接同意使用权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Permissions.granted(action);
            return;
        }
        // 检测权限
        int permissionCheck = ContextCompat.checkSelfPermission(mFragment.getActivity(), permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // 执行获取权限后的操作
            Permissions.granted(action);
            return;
        }
        // 判断是否还能显示权限对话框
        boolean shouldShowRequest =
                Permissions.shouldShowRequestPermissionRationale(mFragment.getActivity(), permission);
        if (shouldShowRequest) {
            // 执行没有获取权限的操作
            requestPermission(permission, action);
        } else {
            // 已拒绝,并且不会再显示权限对话框,需要提示用户在Settings->App->权限中打开权限
            Permissions.denied(action);
        }
    }

    /**
     * 同时请求多个权限
     *
     * @param permissions
     * @param action
     */
    public void request(String[] permissions, PermissionArrayAction action) {
        // 6.0以下版本直接同意使用权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Permissions.granted(action, permissions);
            return;
        }

        boolean[] result = new boolean[permissions.length];
        List<String> permsToRequestList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            // 检测权限
            String permission = permissions[i];
            int permissionCheck = ContextCompat.checkSelfPermission(mFragment.getActivity(), permission);
            boolean granted = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            if (!granted) {
                // 判断是否还能显示权限对话框
                boolean shouldShowRequest =
                        Permissions.shouldShowRequestPermissionRationale(mFragment.getActivity(), permission);
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
        requestPermission(permissions, permsToRequest, result, action);
    }

    /**
     * 请求权限
     *
     * @param permission
     * @param action
     */
    private void requestPermission(String permission, final PermissionAction action) {
        String[] permissions = new String[] {permission};
        boolean[] result = new boolean[1];
        result[0] = false;
        PermissionArrayAction arrayAction = new PermissionArrayAction() {
            @Override
            public void onResult(String[] permissions, boolean[] grantResults) {
                if (grantResults[0]) {
                    Permissions.granted(action);
                } else {
                    Permissions.denied(action);
                }
            }
        };
        requestPermission(permissions, permissions, result, arrayAction);
    }

    /**
     * 请求权限
     *
     * @param permissions
     * @param permsToRequest
     * @param result
     * @param action
     */
    private void requestPermission(String[] permissions, String[] permsToRequest,
                                   boolean[] result, PermissionArrayAction action) {
        int code = Permissions.genRequestCode();
        PendingAction
                pendingAction = new PendingAction(permissions, result, permsToRequest, action);
        mPendingActionMap.put(code, new WeakReference<PendingAction>(pendingAction));
        mFragment.requestPermissions(permsToRequest, code);
    }

}
