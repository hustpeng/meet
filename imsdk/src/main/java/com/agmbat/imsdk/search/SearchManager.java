package com.agmbat.imsdk.search;

import android.text.TextUtils;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.search.group.GroupCategoryResult;
import com.agmbat.imsdk.search.group.OnGetGroupCategoryListener;
import com.agmbat.imsdk.search.group.OnSearchGroupListener;
import com.agmbat.imsdk.search.group.SearchGroupResult;
import com.agmbat.imsdk.search.user.OnSearchUserListener;
import com.agmbat.imsdk.search.user.SearchUserResult;

/**
 * 搜索用户
 */
public class SearchManager {

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
                SearchUserResult result = SearchApi.searchUser(phone, ticket, keyword);
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
    public static void searchGroup(final String keyword, final OnSearchGroupListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, SearchGroupResult>() {
            @Override
            protected SearchGroupResult doInBackground(Void... voids) {
                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                SearchGroupResult result = SearchApi.searchGroup(phone, ticket, keyword);
                if (result == null) {
                    result = new SearchGroupResult();
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
            protected void onPostExecute(SearchGroupResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onSearchGroup(result);
                }
            }
        });
    }

    /**
     * 搜索群
     *
     * @param keyword
     * @param groupCategoryId
     * @return
     */
    public static SearchGroupResult searchGroupSync(final String keyword, int groupCategoryId, int page) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        SearchGroupResult result = SearchApi.searchGroup(phone, ticket, groupCategoryId, keyword, page);
        if (result == null) {
            result = new SearchGroupResult();
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

    /**
     * 获取所有群分类
     *
     * @param listener
     */
    public static void getGroupCategory(final OnGetGroupCategoryListener listener) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, GroupCategoryResult>() {
            @Override
            protected GroupCategoryResult doInBackground(Void... voids) {
                String phone = XMPPManager.getInstance().getConnectionUserName();
                String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                GroupCategoryResult result = SearchApi.getGroupCategory(phone, ticket);
                if (result == null) {
                    result = new GroupCategoryResult();
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
            protected void onPostExecute(GroupCategoryResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onGetGroupCategory(result);
                }
            }
        });
    }
}