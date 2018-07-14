package com.agmbat.appupdate;

import android.text.TextUtils;

import com.agmbat.android.utils.ApkUtils;
import com.google.gson.annotations.SerializedName;

/**
 * app 检测升级结果
 */
public class AppVersionInfo {

    /**
     * 可选升级
     */
    public static final int OPTIONAL_UPDATE = 0;

    /**
     * 强制升级
     */
    public static final int FORCE_UPDATE = 1;

    /**
     * 当前是最新版本
     */
    public static final int LAST_VERSION = 2;

    /**
     * 0,可选升级；1，强制升级；2，当前是最新版本
     */
    @SerializedName("upgradeStrategy")
    private int mUpgradeStrategy;

    /**
     * 版本更新描述
     */
    @SerializedName("description")
    private String mDescription;

    /**
     * 新版本安装地址
     */
    @SerializedName("url")
    private String mUrl;

    /**
     * apk包名
     */
    private String mPackageName;

    /**
     * apk version code
     */
    private int mVersionCode;

    /**
     * apk version name
     */
    private String mVersionName;

    /**
     * apk文件md5值
     */
    private String mApkHash;

    public int getUpgradeStrategy() {
        return mUpgradeStrategy;
    }

    public void setUpgradeStrategy(int upgradeStrategy) {
        mUpgradeStrategy = upgradeStrategy;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    /**
     * 判断能否升级
     *
     * @return
     */
    public boolean canUpdate() {
        return hasUpdate() && !TextUtils.isEmpty(mUrl);
    }

    public boolean isOptionalUpdate() {
        return mUpgradeStrategy == OPTIONAL_UPDATE;
    }

    public boolean isForceUpdate() {
        return mUpgradeStrategy == FORCE_UPDATE;
    }

    /**
     * 当前版本是否为最新版本
     *
     * @return
     */
    public boolean isLastVersion() {
        return mUpgradeStrategy == LAST_VERSION;
    }

    /**
     * 是否有升级
     *
     * @return
     */
    private boolean hasUpdate() {
        return (isOptionalUpdate() || isForceUpdate());
    }

    /**
     * 是否有升级,也可通过version code来比较,由本地判断
     */
    public boolean hasUpdateFormLocal() {
        return ApkUtils.getPackageName().equals(mPackageName)
                && ApkUtils.getVersionCode() < mVersionCode;
    }
}
