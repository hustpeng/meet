package com.agmbat.emoji.pageset;

import android.view.View;
import android.view.ViewGroup;

/**
 * 单个页面Entity
 *
 * @param <T>
 */
public class PageEntity<T extends PageEntity> {

    protected View mRootView;

    public PageEntity() {
    }

    public PageEntity(View view) {
        this.mRootView = view;
    }

    public View getRootView() {
        return mRootView;
    }

    public void setRootView(View rootView) {
        this.mRootView = rootView;
    }

    public View instantiateItem(final ViewGroup container, int position) {
        return null;
    }
}
