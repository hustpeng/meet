package com.agmbat.appupdate;

/**
 * app信息请求
 */
public interface AppVersionInfoRequester {

    /**
     * 请求插件信息
     *
     * @param packageName 当前插件id
     * @param versionCode 当前插件版本
     * @return
     */
    public AppVersionInfo request(String packageName, int versionCode);

}