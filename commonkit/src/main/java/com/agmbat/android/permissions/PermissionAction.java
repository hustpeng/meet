package com.agmbat.android.permissions;

/**
 * 请求单个权限后执行的操作
 */
public interface PermissionAction {

    public void onGranted();

    public void onDenied();

}