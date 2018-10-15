/**
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * Tab Manager
 *
 * @author mayimchen
 * @since 2016-05-28
 */
package com.agmbat.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agmbat.android.SysResources;

/**
 * Displays a list of tab labels representing each page in the parent's tab collection.
 */
public class TabWidget extends LinearLayout implements View.OnFocusChangeListener {

    private OnTabSelectionChanged mSelectionChangedListener;

    /**
     * 当前选中的tab index;
     */
    private int mSelectedTab = 0;

    /**
     * 分隔线drawable
     */
    private Drawable mDividerDrawable;

    /**
     * The bottom strips on the tab indicators.
     */
    private Drawable mStripDrawable;

    public TabWidget(Context context) {
        this(context, null);
    }

    public TabWidget(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.tabWidgetStyle);
    }

    public TabWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initTabWidget();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        // Always draw the selected tab last, so that drop shadows are drawn
        // in the correct z-order.
        if (i == childCount - 1) {
            return mSelectedTab;
        } else if (i >= mSelectedTab) {
            return i + 1;
        } else {
            return i;
        }
    }

    @Override
    public void childDrawableStateChanged(View child) {
        if (getTabCount() > 0 && child == getChildTabViewAt(mSelectedTab)) {
            // To make sure that the bottom strip is redrawn
            invalidate();
        }
        super.childDrawableStateChanged(child);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // Do nothing if there are no tabs.
        if (getTabCount() == 0) {
            return;
        }
        if (mStripDrawable == null) {
            return;
        }
        final Rect bounds = mStripDrawable.getBounds();
        if (bounds.width() <= 0) {
            final View selectedChild = getChildTabViewAt(mSelectedTab);
            bounds.set(bounds.left, bounds.top, bounds.left + selectedChild.getMeasuredWidth(), bounds.bottom);
        }
        int stripHeight = mStripDrawable.getIntrinsicHeight();
        if (stripHeight <= 0) {
            // 默认高度是2dp
            stripHeight = (int) SysResources.dipToPixel(2);
        }
        final int viewHeight = getHeight();
        bounds.set(bounds.left, viewHeight - stripHeight, bounds.right, viewHeight);
        mStripDrawable.draw(canvas);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = getTabCount();
        for (int i = 0; i < count; i++) {
            View child = getChildTabViewAt(i);
            child.setEnabled(enabled);
        }
    }

    /**
     * 设置Tab是否可用
     *
     * @param index
     * @param enabled
     */
    public void setTabEnabled(int index, boolean enabled) {
        View child = getChildTabViewAt(index);
        child.setEnabled(enabled);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this && hasFocus && getTabCount() > 0) {
            getChildTabViewAt(mSelectedTab).requestFocus();
            return;
        }
        if (hasFocus) {
            int i = 0;
            int numTabs = getTabCount();
            while (i < numTabs) {
                if (getChildTabViewAt(i) == v) {
                    setCurrentTab(i);
                    callbackTabChanged(i, false);
                    break;
                }
                i++;
            }
        }
    }

    @Override
    public void addView(View child) {
        if (child.getLayoutParams() == null) {
            child.setLayoutParams(generateTabLayoutParams());
        }
        // Ensure you can navigate to the tab with the keyboard, and you can
        // touch it
        child.setFocusable(true);
        child.setClickable(true);
        // If we have dividers between the tabs and we already have at least one
        // tab, then add a divider before adding the next tab.
        if (mDividerDrawable != null && getTabCount() > 0) {
            ImageView divider = new ImageView(getContext());
            divider.setLayoutParams(generateDividerLayoutParams());
            divider.setBackgroundDrawable(mDividerDrawable);
            super.addView(divider);
        }
        super.addView(child);
        // detect this via geometry with a tabwidget listener rather
        // than potentially interfere with the view's listener
        child.setOnClickListener(new TabClickListener(getTabCount() - 1));
        child.setOnFocusChangeListener(this);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        child.setLayoutParams(params);
        addView(child);
    }

    @Override
    public void removeAllViews() {
        mSelectedTab = 0;
        super.removeAllViews();
    }

    /**
     * Returns the tab indicator view at the given index.
     *
     * @param index the zero-based index of the tab indicator view to return
     * @return the tab indicator view at the given index
     */
    public View getChildTabViewAt(int index) {
        // If we are using dividers, then instead of tab views at 0, 1, 2, ...
        // we have tab views at 0, 2, 4, ...
        if (mDividerDrawable != null) {
            index *= 2;
        }
        return getChildAt(index);
    }

    /**
     * Returns the number of tab indicator views.
     *
     * @return the number of tab indicator views.
     */
    public int getTabCount() {
        int children = getChildCount();

        // If we have dividers, then we will always have an odd number of
        // children: 1, 3, 5, ... and we want to convert that sequence to
        // this: 1, 2, 3, ...
        if (mDividerDrawable != null) {
            children = (children + 1) / 2;
        }
        return children;
    }

    /**
     * Sets the drawable to use as a divider between the tab indicators.
     *
     * @param drawable the divider drawable
     */
    public void setDividerDrawable(Drawable drawable) {
        mDividerDrawable = drawable;
        requestLayout();
        invalidate();
    }

    /**
     * Sets the drawable to use as a divider between the tab indicators.
     *
     * @param resId the resource identifier of the drawable to use as a divider.
     */
    public void setDividerDrawable(int resId) {
        mDividerDrawable = getResources().getDrawable(resId);
        requestLayout();
        invalidate();
    }

    public Drawable getStripDrawable() {
        return mStripDrawable;
    }

    /**
     * 设置底部光标
     *
     * @param drawable
     */
    public void setStripDrawable(Drawable drawable) {
        mStripDrawable = drawable;
        configureStripBounds();
        requestLayout();
        invalidate();
    }

    /**
     * 设置Strip的位置
     *
     * @param offset 绝对位置
     */
    public void setStripOffset(int offset) {
        if (mStripDrawable == null) {
            return;
        }
        configureStripBounds();
        mStripDrawable.getBounds().offset(offset, 0);
        invalidate();
    }

    /**
     * 设置Strip的位置, 用于ViewPager滑动
     *
     * @param index  当前已选中的tab index
     * @param offset 相对位置
     */
    public void setStripOffset(int index, float offset) {
        if (mStripDrawable == null) {
            return;
        }
        final int tabCount = getTabCount();
        if (index < 0 || index >= tabCount) {
            return;
        }
        int viewWidth = getMeasuredWidth();
        int stripWidth = mStripDrawable.getBounds().width();
        float tabItemWidth = (viewWidth - getPaddingLeft() - getPaddingRight()) / (float) tabCount;
        float padding = (tabItemWidth - stripWidth) / 2;
        float absOffset = getPaddingLeft() + (index + offset) * tabItemWidth + padding;
        setStripOffset((int) absOffset);
    }

    /**
     * 设置Strip的位置
     *
     * @param index 设置选中的tab
     */
    public void setStripIndex(int index) {
        setStripOffset(index, 0);
    }

    public int getCurrentTab() {
        return mSelectedTab;
    }

    /**
     * Sets the current tab. This method is used to bring a tab to the front of the Widget, and is used to post to the
     * rest of the UI that a different tab has been brought to the foreground. Note, this is separate from the
     * traditional "focus" that is employed from the view logic. For instance, if we have a list in a tabbed view, a
     * user may be navigating up and down the list, moving the UI focus (orange highlighting) through the list items.
     * The cursor movement does not effect the "selected" tab though, because what is being scrolled through is all on
     * the same tab. The selected tab only changes when we navigate between tabs (moving from the list view to the next
     * tabbed view, in this example). To move both the focus AND the selected tab at once, please use
     * {@link #setCurrentTab}. Normally, the view logic takes care of adjusting the focus, so unless you're
     * circumventing the UI, you'll probably just focus your interest here.
     *
     * @param index The tab that you want to indicate as the selected tab (tab brought to the front of the widget)
     * @see #focusCurrentTab
     */
    public void setCurrentTab(int index) {
        if (index < 0 || index >= getTabCount()) {
            return;
        }
        getChildTabViewAt(mSelectedTab).setSelected(false);
        mSelectedTab = index;
        getChildTabViewAt(mSelectedTab).setSelected(true);
    }

    /**
     * Sets the current tab and focuses the UI on it. This method makes sure that the focused tab matches the selected
     * tab, normally at {@link #setCurrentTab}. Normally this would not be an issue if we go through the UI, since the
     * UI is responsible for calling TabWidget.onFocusChanged(), but in the case where we are selecting the tab
     * programmatically, we'll need to make sure focus keeps up.
     *
     * @param index The tab that you want focused (highlighted in orange) and selected (tab brought to the front of the
     *              widget)
     * @see #setCurrentTab
     */
    public void focusCurrentTab(int index) {
        final int oldTab = mSelectedTab;
        // set the tab
        setCurrentTab(index);
        // change the focus if applicable.
        if (oldTab != index) {
            getChildTabViewAt(index).requestFocus();
        }
    }

    /**
     * Notified that the user clicked on a tab indicator.
     */
    public void setTabSelectionListener(OnTabSelectionChanged listener) {
        mSelectionChangedListener = listener;
    }

    /**
     * 处理点击tab
     *
     * @param tabIndex
     */
    protected void clickTab(int tabIndex) {
        callbackTabChanged(tabIndex, true);
        focusCurrentTab(tabIndex);
    }

    /**
     * 回调Tab changed
     *
     * @param tabIndex
     * @param clicked
     */
    protected void callbackTabChanged(int tabIndex, boolean clicked) {
        if (mSelectionChangedListener != null) {
            mSelectionChangedListener.onTabSelectionChanged(tabIndex, clicked);
        }
    }

    private void initTabWidget() {
        setChildrenDrawingOrderEnabled(true);
        // Deal with focus, as we don't want the focus to go by default
        // to a tab other than the current tab
        setFocusable(true);
        setOnFocusChangeListener(this);
    }

    private LayoutParams generateTabLayoutParams() {
        if (getOrientation() == HORIZONTAL) {
            final LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            lp.setMargins(0, 0, 0, 0);
            return lp;
        } else if (getOrientation() == VERTICAL) {
            final LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
            lp.setMargins(0, 0, 0, 0);
            return lp;
        }
        return null;
    }

    private LayoutParams generateDividerLayoutParams() {
        if (getOrientation() == HORIZONTAL) {
            final LayoutParams lp =
                    new LayoutParams(mDividerDrawable.getIntrinsicWidth(), LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, 0);
            return lp;
        } else if (getOrientation() == VERTICAL) {
            final LayoutParams lp =
                    new LayoutParams(LayoutParams.MATCH_PARENT, mDividerDrawable.getIntrinsicHeight());
            lp.setMargins(0, 0, 0, 0);
            return lp;
        }
        return null;
    }

    private void configureStripBounds() {
        if (mStripDrawable != null) {
            mStripDrawable.setBounds(0, 0, mStripDrawable.getIntrinsicWidth(), mStripDrawable.getIntrinsicHeight());
        }
    }

    /**
     * The user clicked on a tab indicator.
     */
    public static interface OnTabSelectionChanged {
        /**
         * Informs the TabHost which tab was selected. It also indicates if the tab was clicked/pressed or just focused
         * into.
         *
         * @param tabIndex index of the tab that was selected
         * @param clicked  whether the selection changed due to a touch/click or due to focus entering the tab through
         *                 navigation. Pass true if it was due to a press/click and false otherwise.
         */
        void onTabSelectionChanged(int tabIndex, boolean clicked);
    }

    // registered with each tab indicator so we can notify tab host
    private class TabClickListener implements OnClickListener {

        private final int mTabIndex;

        private TabClickListener(int tabIndex) {
            mTabIndex = tabIndex;
        }

        @Override
        public void onClick(View v) {
            clickTab(mTabIndex);
        }
    }

}
