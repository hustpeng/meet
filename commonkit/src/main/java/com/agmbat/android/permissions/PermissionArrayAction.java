package com.agmbat.android.permissions;

/**
 * 同时请求多个权限后执行的回调操作
 */
public interface PermissionArrayAction {

    public void onResult(String[] permissions, boolean[] grantResults);

}