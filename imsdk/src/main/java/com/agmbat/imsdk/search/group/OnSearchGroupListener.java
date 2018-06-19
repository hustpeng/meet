package com.agmbat.imsdk.search.group;

/**
 * 搜索用户监听器
 */
public interface OnSearchGroupListener {

    /**
     * 当搜索到用户
     *
     * @param result
     */
    public void onSearchGroup(SearchGroupResult result);
}
