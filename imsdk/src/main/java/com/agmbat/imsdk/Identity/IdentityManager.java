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

/**
 * 身份认证管理
 */
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
                        result.mErrorMsg = "待审核";
                    }
                } else if (result.mAuth.mStatus == Auth.STATUS_PASS) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "审核通过";
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
                            final String frontUrl, final String backUrl, final OnIdentityListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return requestUpload(name, identity, frontPath, backPath, frontUrl, backUrl);
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


    private static ApiResult requestUpload(String name, String identity, String frontPath, String backPath,
                                           String frontUrl, String backUrl) {

        String phone = XMPPManager.getInstance().getConnectionUserName();
        String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();

        if (!TextUtils.isEmpty(frontPath)) {
            FileApiResult result = uploadFile(phone, ticket, frontPath);
            if (result == null || !result.mResult) {
                return errorResult("上传正面身份证照片失败!");
            } else {
                frontUrl = result.url;
            }
        } // else frontUrl 是不为空的

        if (!TextUtils.isEmpty(backPath)) {
            FileApiResult result = uploadFile(phone, ticket, backPath);
            if (result == null || !result.mResult) {
                return errorResult("上传背面身份证照片失败!");
            } else {
                backUrl = result.url;
            }
        } // else backUrl 是不为空的

        ApiResult result = IdentityApi.auth(phone, ticket, name, identity, frontUrl, backUrl);
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

    /**
     * 上传文件
     *
     * @param phone
     * @param ticket
     * @param path
     * @return
     */
    private static FileApiResult uploadFile(String phone, String ticket, String path) {
        File frontFile = new File(path);
        String frontFileName = frontFile.getName();
        File outFrontFile = new File(UserFileManager.getCurImageDir(), frontFileName);
        ImageUtils.resizeImage(frontFile.getAbsolutePath(), outFrontFile.getAbsolutePath(), 540, 960);
        String format = FileUtils.getExtension(path);
        return FileApi.uploadCommonFile(phone, ticket, format, outFrontFile);
    }

}
