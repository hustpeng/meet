package com.agmbat.imsdk.searchuser;

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
import com.agmbat.imsdk.reportuser.OnReportUserListener;
import com.agmbat.imsdk.reportuser.ReportUserApi;

import java.io.File;

/**
 * 搜索用户
 */
public class SearchUserManager {

    /**
     * 搜索用户
     *
     * @param keyword  用户的im_uid（例如10061）或手机号
     * @param listener
     */
    public static void searchUser(final String keyword, final OnSearchUserListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, SearchUserResult>() {
            @Override
            protected SearchUserResult doInBackground(Void... voids) {
                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                SearchUserResult result = SearchUserApi.searchUser(phone, ticket, keyword);
                if (result == null) {
                    result = new SearchUserResult();
                    result.mResult = false;
                    result.mErrorMsg = "搜索失败!";
                    return result;
                } else if (result.mResult) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "搜索成功";
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(SearchUserResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onSearchUser(result);
                }
            }
        });
    }

    /**
     * 搜索群组
     *
     * @param keyword  用户的im_uid（例如10061）或手机号
     * @param listener
     */
    public static void searchGroup(final String keyword, final OnSearchUserListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, SearchUserResult>() {
            @Override
            protected SearchUserResult doInBackground(Void... voids) {
                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                SearchUserResult result = SearchUserApi.searchGroup(phone, ticket, keyword);
                if (result == null) {
                    result = new SearchUserResult();
                    result.mResult = false;
                    result.mErrorMsg = "搜索失败!";
                    return result;
                } else if (result.mResult) {
                    if (TextUtils.isEmpty(result.mErrorMsg)) {
                        result.mErrorMsg = "搜索成功";
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(SearchUserResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onSearchUser(result);
                }
            }
        });
    }
}