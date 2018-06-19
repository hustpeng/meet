package com.agmbat.imsdk.search.user;

/**
 * 搜索用户监听器
 */
public interface OnSearchUserListener {

    /**
     * 当搜索到用户
     *
     * @param result
     */
    public void onSearchUser(SearchUserResult result);
}
