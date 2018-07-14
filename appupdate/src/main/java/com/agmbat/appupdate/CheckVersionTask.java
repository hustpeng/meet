package com.agmbat.appupdate;

import android.app.ProgressDialog;
import android.content.Context;

import com.agmbat.android.task.DialogAsyncTask;
import com.agmbat.android.utils.ApkUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.log.Log;

/**
 * 检测更新的Task
 */
public class CheckVersionTask extends DialogAsyncTask<Void, Void, AppVersionInfo> {

    private AppVersionInfoRequester mRequester;

    public CheckVersionTask(Context context, AppVersionInfoRequester requester) {
        super(context);
        mRequester = requester;
    }

    @Override
    protected void onPrepareDialog(ProgressDialog dialog) {
        super.onPrepareDialog(dialog);
        dialog.setMessage(getContext().getText(R.string.appupdate_app_version_check_msg));
    }

    @Override
    protected AppVersionInfo doInBackground(Void... arg0) {
        return mRequester.request(ApkUtils.getPackageName(), ApkUtils.getVersionCode());
    }

    @Override
    protected void onPostExecute(AppVersionInfo result) {
        super.onPostExecute(result);
        if (result == null) {
            ToastUtil.showToast(R.string.appupdate_network_time_out_msg);
            //  ToastUtil.showToast(R.string.sme_settings_app_version_check_failed);
            return;
        }

        if (result.isLastVersion()) {
            ToastUtil.showToast(R.string.appupdate_app_version_is_last);
        } else {
            if (result.canUpdate()) {
                AppVersionHelper.showUpdateDialog(getContext(), result);
            } else {
                Log.e("AppVersionHelper", "has last version, but url is empty?");
            }
        }
    }

}