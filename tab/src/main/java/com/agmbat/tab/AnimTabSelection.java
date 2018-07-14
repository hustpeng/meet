/**
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * Tab Manager
 *
 * @author mayimchen
 * @since 2016-05-28
 */
package com.agmbat.tab;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import android.graphics.drawable.Drawable;

/**
 * 选中Tab后，以动画的方式移动下标
 */
public class AnimTabSelection implements TabWidget.OnTabSelectionChanged {

    private final TabWidget.OnTabSelectionChanged mListener;
    private final TabWidget mTabWidget;

    // anim spring
    private Spring mScrollSpring;
    private final BaseSpringSystem mSpringSystem = SpringSystem.create();

    public AnimTabSelection(TabWidget tabWidget, TabWidget.OnTabSelectionChanged l) {
        mTabWidget = tabWidget;
        mListener = l;
        mScrollSpring = mSpringSystem.createSpring();
        mScrollSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                int offset = calcStripOffset(spring);
                mTabWidget.setStripOffset(offset);
            }
        });
    }

    @Override
    public void onTabSelectionChanged(int tabIndex, boolean clicked) {
        int tabCount = mTabWidget.getTabCount();
        if (tabCount <= 0) {
            return;
        }
        if (tabCount > 1) {
            float endValue = (float) tabIndex / (tabCount - 1);
            mScrollSpring.setEndValue(endValue);
        }
        if (mListener != null) {
            mListener.onTabSelectionChanged(tabIndex, clicked);
        }
    }

    /**
     * 计算StripOffset
     *
     * @param spring
     * @return
     */
    private int calcStripOffset(Spring spring) {
        float val = (float) spring.getCurrentValue();
        int viewWidth = mTabWidget.getMeasuredWidth();
        Drawable stripDrawable = mTabWidget.getStripDrawable();
        int stripWidth = stripDrawable.getBounds().width();
        int tabCount = mTabWidget.getTabCount();
        float tabItemWidth =
                (viewWidth - mTabWidget.getPaddingLeft() - mTabWidget.getPaddingRight()) / (float) tabCount;
        float minTranslate = (tabItemWidth - stripWidth) / 2 + mTabWidget.getPaddingLeft();
        float maxTranslate = viewWidth - minTranslate - stripWidth - mTabWidget.getPaddingRight();
        float range = maxTranslate - minTranslate;
        float mappedValue = mTabWidget.getPaddingLeft() + (val * range) + minTranslate;
        return (int) mappedValue;
    }
}
