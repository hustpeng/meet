package com.agmbat.imsdk.reportuser;

import android.text.TextUtils;

import com.agmbat.android.image.ImageUtils;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.mgr.UserFileManager;
import com.agmbat.imsdk.remotefile.FileApi;
import com.agmbat.imsdk.remotefile.FileApiResult;

import java.io.File;

/**
 * 举报用户
 */
public class ReportUserManager {

    /**
     * 举报用户
     *
     * @param targetUid 被举报用户
     * @param content
     * @param path
     * @param listener
     */
    public static void reportUser(final String targetUid, final String content, final String path,
                                  final OnReportUserListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                String imageUrl = "";
                if (path != null && new File(path).exists()) {
                    File frontFile = new File(path);
                    String frontFileName = frontFile.getName();
                    File outFrontFile = new File(UserFileManager.getCurImageDir(), frontFileName);
                    ImageUtils.resizeImage(frontFile.getAbsolutePath(), outFrontFile.getAbsolutePath(), 1080, 1920);
                    String format = FileUtils.getExtension(frontFile);
                    FileApiResult result1 = FileApi.uploadCommonFile(phone, ticket, format, outFrontFile);
                    if (result1 == null || !result1.mResult) {
                        ApiResult result = new ApiResult();
                        result.mResult = false;
                        result.mErrorMsg = "上传图片失败";
                        return result;
                    }
                    imageUrl = result1.url;
                }
                ApiResult result = ReportUserApi.reportUser(phone, ticket, targetUid, content, imageUrl);
                if (result == null) {
                    result = new ApiResult();
                    result.mResult = false;
                    result.mErrorMsg = "举报失败!";
                    return result;
                } else if (result.mResult) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "举报成功";
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onReportUser(result);
                }
            }
        });
    }
}