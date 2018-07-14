/**
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * Tab Manager
 *
 * @author mayimchen
 * @since 2016-05-28
 */
package com.agmbat.tab;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * This is a helper class that implements a generic mechanism for associating fragments with the tabs in a tab host.
 */
public class TabManager implements TabWidget.OnTabSelectionChanged {

    /**
     * 切换tab时采用hide fragment方式,使用时再显示
     */
    public static final int SWITCH_MODE_HIDE = 1;

    /**
     * 切换tab时采用detach fragment方式,这样每次都会attach,会导致Fragment不停的重新实例化
     */
    public static final int SWITCH_MODE_DETACH = 2;

    private final FragmentManager mManager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private TabInfo mLastTab;

    private TabWidget mTabWidget;
    private OnTabChangeListener mOnTabChangeListener;

    private int mSwitchMode = SWITCH_MODE_HIDE;

    public TabManager(FragmentManager manager, View root) {
        mManager = manager;
        mTabWidget = (TabWidget) root.findViewById(android.R.id.tabs);
        mTabWidget.setTabSelectionListener(this);
    }

    @Override
    public void onTabSelectionChanged(int tabIndex, boolean clicked) {
        if (clicked) {
            doTabChanged(mTabs.get(tabIndex));
        }
    }

    /**
     * Set the switch mode
     *
     * @param mode Pass {@link #SWITCH_MODE_HIDE} or {@link #SWITCH_MODE_DETACH}. Default value is
     *             {@link #SWITCH_MODE_HIDE}.
     */
    public void setSwitchMode(int mode) {
        mSwitchMode = mode;
    }

    public void addTab(View indicator, String tag, Fragment fragment) {
        addTab(new Indicator(indicator), tag, fragment);
    }

    public void addTab(Indicator indicator, String tag, Fragment fragment) {
        TabInfo info = new TabInfo(tag, fragment);
        mTabs.add(info);
        mTabWidget.addView(indicator.createIndicatorView(mTabWidget.getContext()));
    }

    public void setCurrentTab(int index) {
        if (index < 0 || index >= mTabs.size()) {
            return;
        }
        mTabWidget.setCurrentTab(index);
        doTabChanged(mTabs.get(index));
    }

    /**
     * Change the tab
     *
     * @param newTab
     */
    private void doTabChanged(TabInfo newTab) {
        if (mLastTab != newTab) {
            FragmentTransaction ft = mManager.beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    if (mSwitchMode == SWITCH_MODE_HIDE) {
                        ft.hide(mLastTab.fragment);
                    } else if (mSwitchMode == SWITCH_MODE_DETACH) {
                        ft.detach(mLastTab.fragment);
                    }
                }
            }
            if (newTab != null) {
                if (!newTab.fragment.isAdded()) {
                    ft.add(android.R.id.tabcontent, newTab.fragment, newTab.tag);
                } else {
                    if (mSwitchMode == SWITCH_MODE_HIDE) {
                        ft.show(newTab.fragment);
                    } else if (mSwitchMode == SWITCH_MODE_DETACH) {
                        ft.attach(newTab.fragment);
                    }
                }
            }
            mLastTab = newTab;
            ft.commit();
            if (mOnTabChangeListener != null) {
                mOnTabChangeListener.onTabChanged(mLastTab.tag);
            }
        }
    }

    /**
     * Register a callback to be invoked when the selected state of any of the items in this list changes
     *
     * @param l The callback that will run
     */
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    public TabWidget getTabWidget() {
        return mTabWidget;
    }

    public int getCurrentTab() {
        return mTabWidget.getCurrentTab();
    }

    public String getCurrentTabTag() {
        return mLastTab.tag;
    }

    public Fragment getCurrentTabFragment() {
        return mLastTab.fragment;
    }

    /**
     * 将某个Tab启用或禁用
     *
     * @param index
     */
    public void setTabEnabled(int index, boolean enabled) {
        if (index < 0 || index >= mTabs.size()) {
            return;
        }
        mTabWidget.setTabEnabled(index, enabled);
    }

    /**
     * Interface definition for a callback to be invoked when tab changed
     */
    public interface OnTabChangeListener {
        public void onTabChanged(String tabId);
    }

    private static final class TabInfo {

        private final String tag;
        private final Fragment fragment;

        TabInfo(String tag, Fragment fragment) {
            this.tag = tag;
            this.fragment = fragment;
        }

    }

}
