package com.agmbat.imsdk.Identity;

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

public class IdentityManager {

    /**
     * 查询身份验证状态
     *
     * @param l
     */
    public static void authStatus(final OnLoadAuthStatusListener l) {

        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Object, Object, AuthStatusResult>() {
            @Override
            protected AuthStatusResult doInBackground(Object... objects) {
                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                AuthStatusResult result = IdentityApi.authStatus(phone, ticket);
                if (result == null) {
                    result = new AuthStatusResult();
                    result.mResult = false;
                    result.mErrorMsg = "请求服务器失败！";
                    return result;
                }
                if (!result.mResult) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "请求服务器失败！";
                    }
                    return result;
                }

                if (result.mAuth == null) {
                    result.mAuth = new Auth();
                    result.mAuth.mStatus = Auth.STATUS_NONE;
                    result.mErrorMsg = "未申请认证";
                    return result;
                }

                if (result.mAuth.mStatus == Auth.STATUS_PENDING_TRIAL) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "待审核！";
                    }
                } else if (result.mAuth.mStatus == Auth.STATUS_PASS) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "审核通过！";
                    }
                } else if (result.mAuth.mStatus == Auth.STATUS_FAIL) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = result.mAuth.mOpinion;
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(AuthStatusResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onLoadAuthStatus(result);
                }
            }
        });
    }

    /**
     * 身份认证
     */
    public static void auth(final String name, final String identity, final String frontPath, final String backPath,
                            final OnIdentityListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                File frontFile = new File(frontPath);
                String frontFileName = frontFile.getName();
                File outFrontFile = new File(UserFileManager.getCurImageDir(), frontFileName);
                ImageUtils.resizeImage(frontFile.getAbsolutePath(), outFrontFile.getAbsolutePath(), 1080, 1920);


                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                String format = FileUtils.getExtension(frontPath);
                FileApiResult result1 = FileApi.uploadCommonFile(phone, ticket, format, outFrontFile);
                if (result1 == null || !result1.mResult) {
                    return errorResult("上传正面身份证照片失败!");
                }

                File backFile = new File(backPath);
                String backFileName = backFile.getName();
                File outBackFile = new File(UserFileManager.getCurImageDir(), backFileName);
                ImageUtils.resizeImage(frontFile.getAbsolutePath(), outBackFile.getAbsolutePath(), 1080, 1920);

                format = FileUtils.getExtension(backPath);
                FileApiResult result2 = FileApi.uploadCommonFile(phone, ticket, format, outBackFile);
                if (result2 == null || !result2.mResult) {
                    return errorResult("上传背面身份证照片失败!");
                }
                ApiResult result = IdentityApi.auth(phone, ticket, name, identity, result1.url, result2.url);
                if (result == null) {
                    return errorResult("上传认证资料失败!");
                }
                if (!result.mResult) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "上传认证资料失败!!";
                    }
                } else {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "上传认证资料成功!";
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onIdentity(result);
                }
            }
        });

    }

    /**
     * 生成错误的result
     *
     * @param msg
     * @return
     */
    private static ApiResult errorResult(String msg) {
        ApiResult result = new ApiResult();
        result.mResult = false;
        result.mErrorMsg = msg;
        return result;
    }
}
