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
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Container for a tabbed window view. This object holds two children: a set of tab labels that the
 * user clicks to select a specific tab, and a FrameLayout object that displays the contents of that
 * page. The individual elements are typically controlled using this container object, rather than
 * setting values on the child elements themselves.
 *
 * @author mayimchen
 */
public class TabHost implements TabWidget.OnTabSelectionChanged {

    private TabWidget mTabWidget;
    private FrameLayout mTabContent;
    private List<TabInfo> mTabs = new ArrayList<TabInfo>(2);
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected int mCurrentTab = -1;
    private View mCurrentView = null;

    private OnTabChangeListener mOnTabChangeListener;

    public TabHost(View root) {
        mTabWidget = (TabWidget) root.findViewById(android.R.id.tabs);
        if (mTabWidget == null) {
            throw new RuntimeException(
                    "Your TabHost must have a TabWidget whose id attribute is 'android.R.id.tabs'");
        }
        mTabWidget.setTabSelectionListener(this);

        mTabContent = (FrameLayout) root.findViewById(android.R.id.tabcontent);
        if (mTabContent == null) {
            throw new RuntimeException(
                    "Your TabHost must have a FrameLayout whose id attribute is "
                            + "'android.R.id.tabcontent'");
        }
        initTabHost();
    }

    /**
     * Add a tab.
     *
     * @param contentView content.
     */
    public void addTab(View indicator, String tag, View contentView) {
        addTab(new Indicator(indicator), tag, contentView);
    }

    /**
     * Add a tab.
     */
    public void addTab(Indicator indicator, String tag, View contentView) {
        TabInfo info = new TabInfo(tag);
        info.setContent(contentView);
        mTabs.add(info);
        mTabWidget.addView(indicator.createIndicatorView(mTabWidget.getContext()));
        if (mCurrentTab == -1) {
            setCurrentTab(0);
        }
    }

    /**
     * Removes all tabs from the tab widget associated with this tab host.
     */
    public void clearAllTabs() {
        mTabWidget.removeAllViews();
        initTabHost();
        mTabContent.removeAllViews();
        mTabs.clear();
    }

    private void initTabHost() {
        mCurrentTab = -1;
        mCurrentView = null;
    }

    public TabWidget getTabWidget() {
        return mTabWidget;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public String getCurrentTabTag() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabs.size()) {
            return mTabs.get(mCurrentTab).getTag();
        }
        return null;
    }

    public View getCurrentTabView() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabs.size()) {
            return mTabWidget.getChildTabViewAt(mCurrentTab);
        }
        return null;
    }

    public View getCurrentView() {
        return mCurrentView;
    }

    public void setCurrentTabByTag(String tag) {
        int i;
        for (i = 0; i < mTabs.size(); i++) {
            if (mTabs.get(i).getTag().equals(tag)) {
                setCurrentTab(i);
                break;
            }
        }
    }

    /**
     * Get the FrameLayout which holds tab content
     */
    public FrameLayout getTabContentView() {
        return mTabContent;
    }

    public void setCurrentTab(int index) {
        if (index < 0 || index >= mTabs.size()) {
            return;
        }

        if (index == mCurrentTab) {
            return;
        }

        // notify old tab content
        if (mCurrentTab != -1) {
            mTabs.get(mCurrentTab).mContentStrategy.tabClosed();
        }

        mCurrentTab = index;
        final TabInfo spec = mTabs.get(index);

        // Call the tab widget's focusCurrentTab(), instead of just
        // selecting the tab.
        mTabWidget.focusCurrentTab(mCurrentTab);

        // tab content
        mCurrentView = spec.mContentStrategy.getContentView();

        if (mCurrentView.getParent() == null) {
            mTabContent.addView(mCurrentView,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (!mTabWidget.hasFocus()) {
            // if the tab widget didn't take focus (likely because we're in touch mode)
            // give the current tab content view a shot
            mCurrentView.requestFocus();
        }

        // mTabContent.requestFocus(View.FOCUS_FORWARD);
        invokeOnTabChangeListener();
    }

    /**
     * Register a callback to be invoked when the selected state of any of the items
     * in this list changes
     *
     * @param l The callback that will run
     */
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    private void invokeOnTabChangeListener() {
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(getCurrentTabTag());
        }
    }

    @Override
    public void onTabSelectionChanged(int tabIndex, boolean clicked) {
        setCurrentTab(tabIndex);
        if (clicked) {
            mTabContent.requestFocus(View.FOCUS_FORWARD);
        }
    }

    /**
     * Interface definition for a callback to be invoked when tab changed
     */
    public interface OnTabChangeListener {
        void onTabChanged(String tabId);
    }

    /**
     * Makes the content of a tab when it is selected. Use this if your tab
     * content needs to be created on demand, i.e. you are not showing an
     * existing view or starting an activity.
     */
    public interface TabContentFactory {
        /**
         * Callback to make the tab contents
         *
         * @param tag Which tab was selected.
         * @return The view to display the contents of the selected tab.
         */
        View createTabContent(String tag);
    }

    /**
     * A tab has a tab indicator, content, and a tag that is used to keep
     * track of it.  This builder helps choose among these options.
     * <p>
     * For the tab indicator, your choices are:
     * 1) set a label
     * 2) set a label and an icon
     * <p>
     * For the tab content, your choices are:
     * 1) the id of a {@link View}
     * 2) a {@link TabContentFactory} that creates the {@link View} content.
     * 3) an {@link Intent} that launches an {@link android.app.Activity}.
     */
    private class TabInfo {

        private String mTag;

        private ContentStrategy mContentStrategy;

        private TabInfo(String tag) {
            mTag = tag;
        }

        /**
         * Specify the id of the view that should be used as the content
         * of the tab.
         */
        public void setContent(int viewId) {
            mContentStrategy = new ViewIdContentStrategy(viewId);
        }

        /**
         * Specify a {@link android.widget.TabHost.TabContentFactory} to use to
         * create the content of the tab.
         */
        public void setContent(TabContentFactory contentFactory) {
            mContentStrategy = new FactoryContentStrategy(mTag, contentFactory);
        }

        public void setContent(View view) {
            mContentStrategy = new ContentViewStrategy(mTag, view);
        }

        public String getTag() {
            return mTag;
        }
    }

    /**
     * Specifies what you do to manage the tab content.
     */
    private static interface ContentStrategy {

        /**
         * Return the content view.  The view should may be cached locally.
         */
        View getContentView();

        /**
         * Perhaps do something when the tab associated with this content has
         * been closed (i.e make it invisible, or remove it).
         */
        void tabClosed();
    }

    /**
     * How to create the tab content via a view id.
     */
    private class ViewIdContentStrategy implements ContentStrategy {

        private final View mView;

        private ViewIdContentStrategy(int viewId) {
            mView = mTabContent.findViewById(viewId);
            if (mView != null) {
                mView.setVisibility(View.GONE);
            } else {
                throw new RuntimeException(
                        "Could not create tab content because could not find view with id " + viewId);
            }
        }

        public View getContentView() {
            mView.setVisibility(View.VISIBLE);
            return mView;
        }

        public void tabClosed() {
            mView.setVisibility(View.GONE);
        }
    }

    /**
     * How tab content is managed using {@link TabContentFactory}.
     */
    private class FactoryContentStrategy implements ContentStrategy {

        private View mTabContent;
        private final CharSequence mTag;
        private TabContentFactory mFactory;

        public FactoryContentStrategy(CharSequence tag, TabContentFactory factory) {
            mTag = tag;
            mFactory = factory;
        }

        public View getContentView() {
            if (mTabContent == null) {
                mTabContent = mFactory.createTabContent(mTag.toString());
            }
            mTabContent.setVisibility(View.VISIBLE);
            return mTabContent;
        }

        public void tabClosed() {
            mTabContent.setVisibility(View.GONE);
        }
    }

    private class ContentViewStrategy implements ContentStrategy {
        private View mTabContent;
        private final CharSequence mTag;

        public ContentViewStrategy(CharSequence tag, View content) {
            mTag = tag;
            mTabContent = content;
        }

        public View getContentView() {
            mTabContent.setVisibility(View.VISIBLE);
            return mTabContent;
        }

        public void tabClosed() {
            mTabContent.setVisibility(View.GONE);
        }
    }

}
