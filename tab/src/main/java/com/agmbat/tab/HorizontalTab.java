/**
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * Tab Manager
 *
 * @author mayimchen
 * @since 2016-05-28
 */
package com.agmbat.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class HorizontalTab extends HorizontalScrollView {

    private TabWidget mTabWidget;

    public HorizontalTab(Context context) {
        super(context);
        init(context);
    }

    public HorizontalTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setHorizontalScrollBarEnabled(false);
        mTabWidget = new TabWidget(context);
        mTabWidget.setOrientation(LinearLayout.HORIZONTAL);
        mTabWidget.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTabWidget.setPadding(20, 0, 5, 0);
        addView(mTabWidget);
    }

    public void addTab(View tab) {
        mTabWidget.addView(tab);
    }

    public void addTab(View tab, int index) {
        mTabWidget.addView(tab, index);
    }

    public void addTab(View tab, TabWidget.LayoutParams params) {
        mTabWidget.addView(tab, params);
    }

    public void addTab(View tab, int width, int height) {
        mTabWidget.addView(tab, width, height);
    }

    public void addTab(View tab, int index, TabWidget.LayoutParams params) {
        mTabWidget.addView(tab, index, params);
    }

    public TabWidget getTabWidget() {
        return mTabWidget;
    }

    public void setCurrentTab(int index) {
        mTabWidget.setCurrentTab(index);
        if (index < 0 || index >= mTabWidget.getTabCount()) {
            return;
        }
        View v = mTabWidget.getChildTabViewAt(index);
        if (v == null) {
            return;
        }
        int scrollX = (v.getLeft() + v.getRight() - getWidth()) / 2;
        smoothScrollTo(scrollX, 0);
    }

    public void setTabSelectionListener(TabWidget.OnTabSelectionChanged listener) {
        mTabWidget.setTabSelectionListener(listener);
    }

    /**
     * 清除tab, 实现方式将需要的tab View 移除
     *
     * @param tabIndex
     */
    public void clearTab(int tabIndex) {
        View v = mTabWidget.getChildTabViewAt(tabIndex);
        if (v == null) {
            return;
        }
    }

    public void clearTab(View v) {
        int oldSelectedTabIndex = mTabWidget.getCurrentTab();
        int tabViewConut = mTabWidget.getTabCount();
        View[] tabViews = new View[tabViewConut];

        for (int i = 0; i < tabViewConut; i++) {
            tabViews[i] = mTabWidget.getChildTabViewAt(i);
        }
        mTabWidget.removeAllViews();

        int newSelectTab = 0;

        for (int i = 0; i < tabViewConut; i++) {
            if (v != tabViews[i]) {
                mTabWidget.addView(tabViews[i]);
            } else {
                newSelectTab = i;
            }
        }

        if (newSelectTab <= oldSelectedTabIndex) {
            oldSelectedTabIndex -= 1;
            if (oldSelectedTabIndex < 0) {
                oldSelectedTabIndex = 0;
            }
        }
        newSelectTab = oldSelectedTabIndex;
        mTabWidget.setCurrentTab(newSelectTab);
    }

    public void clearAllTabs() {
        mTabWidget.removeAllViews();
    }
}
