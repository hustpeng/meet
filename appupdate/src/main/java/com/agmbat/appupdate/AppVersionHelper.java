package com.agmbat.appupdate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.widget.ProgressBar;

import com.agmbat.android.SystemManager;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.ApkUtils;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.NetworkUtil;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.net.HttpUtils;

import java.io.File;

/**
 * 检测应用升级
 * <p>
 * 升级流程 <br/>
 * 程序启动到主界面后，在后面检测升级，服务器会返回三种结果<br/>
 * 1. 可选升级，用户可以点击取消，然后继续使用<br/>
 * 用户点击升级后，对话框变成模态对话框，并下载apk文件，下载完成后打开安装界面<br/>
 * 用户点击取消后，暂时没定下次检测的时间，而每次都会检测并弹出对话框<br/>
 * 2. 强制升级，用户只能升级才可用， 用户只能点击升级。<br/>
 * 3. 无更新包<br/>
 */
public class AppVersionHelper {

    /**
     * 当前加载的App版本信息
     */
    private static AppVersionInfo sAppVersionInfo;
    /**
     * 当前版本检测请求
     */
    private static AppVersionInfoRequester sAppVersionInfoRequester;

    public static AppVersionInfo getAppVersionInfo() {
        return sAppVersionInfo;
    }

    public static void setAppVersionInfo(AppVersionInfo info) {
        sAppVersionInfo = info;
    }

    public static void setAppVersionInfoRequester(AppVersionInfoRequester requester) {
        sAppVersionInfoRequester = requester;
    }

    /**
     * 在后台检测升级
     */
    public static void checkVersionOnBackground() {
        checkVersionOnBackground(sAppVersionInfoRequester);
    }

    /**
     * 在后台检测升级
     */
    public static void checkVersionOnBackground(final AppVersionInfoRequester requester) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, AppVersionInfo>() {

            @Override
            protected AppVersionInfo doInBackground(Void... arg0) {
                return requester.request(ApkUtils.getPackageName(), ApkUtils.getVersionCode());
            }

            @Override
            protected void onPostExecute(AppVersionInfo result) {
                super.onPostExecute(result);
                if (result != null && result.canUpdate()) {
                    openUpgradeActivity(result);
                }
            }
        }, AsyncTaskUtils.Priority.LOW);
    }

    /**
     * 在前台检测升级
     */
    public static void checkVersion(Context context) {
        checkVersion(context, sAppVersionInfoRequester);
    }

    /**
     * 在前台检测升级
     */
    public static void checkVersion(Context context, final AppVersionInfoRequester requester) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtil.showToast(R.string.appupdate_no_network_msg);
            return;
        }
        AsyncTaskUtils.executeAsyncTask(new CheckVersionTask(context, requester), AsyncTaskUtils.Priority.HIGH);
    }

    /**
     * 打开升级界面
     *
     * @param info
     */
    private static void openUpgradeActivity(AppVersionInfo info) {
        ComponentName topActivity = AppUtils.getTopActivity();
        if (!ApkUtils.getPackageName().equals(topActivity.getPackageName())) {
            return;
        }
        setAppVersionInfo(info);
        Context context = SystemManager.getContext();
        Intent intent = new Intent(context, UpgradeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 显示更新对对话框
     *
     * @param context
     * @param appVersionInfo
     */
    public static Dialog showUpdateDialog(final Context context, final AppVersionInfo appVersionInfo) {
        return showUpdateDialog(context, appVersionInfo, null);
    }

    /**
     * 显示更新对对话框
     *
     * @param context
     * @param result
     */
    public static Dialog showUpdateDialog(final Context context, final AppVersionInfo appVersionInfo,
                                          final DialogInterface.OnClickListener l) {
        if (appVersionInfo == null) {
            return null;
        }
        if (appVersionInfo.isOptionalUpdate() || appVersionInfo.isForceUpdate()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog);
            builder.setTitle(context.getText(R.string.appupdate_app_version_dlg_title));
            builder.setMessage(appVersionInfo.getDescription());
            builder.setCancelable(false);
            builder.setPositiveButton(context.getText(R.string.appupdate_app_version_dlg_confirm),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Dialog loadingDialog = updateDialogLoading(context, appVersionInfo);
                            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (l != null) {
                                        l.onClick(dialog, 0);
                                    }
                                }
                            });
                        }
                    });
            if (appVersionInfo.isOptionalUpdate()) {
                builder.setNegativeButton(context.getText(R.string.appupdate_app_version_dlg_cancel), l);
            }
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }
        return null;
    }

    private static Dialog updateDialogLoading(final Context context, final AppVersionInfo info) {
        String fileName = context.getPackageName() + ".apk";
        final File saveToFile = new File(Environment.getExternalStorageDirectory(), fileName);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog);
        builder.setTitle(context.getText(R.string.appupdate_app_version_dlg_title));
        builder.setCancelable(false);
        builder.setView(new ProgressBar(context));
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {
                return HttpUtils.downloadFile(info.getUrl(), saveToFile);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (info.isOptionalUpdate()) {
                    UiUtils.dismissDialog(dialog);
                }
                if (result) {
                    ApkUtils.installPackage(context, saveToFile.getAbsolutePath());
                } else {
                    UiUtils.dismissDialog(dialog);
                    ToastUtil.showToast(R.string.appupdate_app_version_download_apk_failed);
                }
            }
        }, AsyncTaskUtils.Priority.HIGH);
        return dialog;
    }

}
