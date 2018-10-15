package com.agmbat.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.agmbat.android.AppResources;

/**
 * 支持AdapterView的下拉和上拉的基类
 */
public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T> implements
        OnScrollListener {

    private boolean mLastItemVisible;
    private OnScrollListener mOnScrollListener;
    private OnLastItemVisibleListener mOnLastItemVisibleListener;
    private IndicatorLayout mIndicatorIvTop;
    private IndicatorLayout mIndicatorIvBottom;
    private boolean mShowIndicator;
    private boolean mScrollEmptyView = true;
    public PullToRefreshAdapterViewBase(Context context) {
        super(context);
        mRefreshableView.setOnScrollListener(this);
    }

    public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRefreshableView.setOnScrollListener(this);
    }

    public PullToRefreshAdapterViewBase(Context context, Mode mode) {
        super(context, mode);
        mRefreshableView.setOnScrollListener(this);
    }

    public PullToRefreshAdapterViewBase(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        mRefreshableView.setOnScrollListener(this);
    }

    private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
        FrameLayout.LayoutParams newLp = null;
        if (null != lp) {
            newLp = new FrameLayout.LayoutParams(lp);
            if (lp instanceof LayoutParams) {
                newLp.gravity = ((LayoutParams) lp).gravity;
            } else {
                newLp.gravity = Gravity.CENTER;
            }
        }
        return newLp;
    }

    /**
     * Gets whether an indicator graphic should be displayed when the View is in
     * a state where a Pull-to-Refresh can happen. An example of this state is
     * when the Adapter View is scrolled to the top and the mode is set to
     * {@link Mode#PULL_FROM_START}. The default value is <var>true</var> if
     * {@link PullToRefreshBase#isPullToRefreshOverScrollEnabled()
     * isPullToRefreshOverScrollEnabled()} returns false.
     *
     * @return true if the indicators will be shown
     */
    public boolean getShowIndicator() {
        return mShowIndicator;
    }

    /**
     * Sets whether an indicator graphic should be displayed when the View is in
     * a state where a Pull-to-Refresh can happen. An example of this state is
     * when the Adapter View is scrolled to the top and the mode is set to
     * {@link Mode#PULL_FROM_START}
     *
     * @param showIndicator - true if the indicators should be shown.
     */
    public void setShowIndicator(boolean showIndicator) {
        mShowIndicator = showIndicator;
        if (getShowIndicatorInternal()) {
            // If we're set to Show Indicator, add/update them
            addIndicatorViews();
        } else {
            // If not, then remove then
            removeIndicatorViews();
        }
    }

    @Override
    public final void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (DEBUG) {
            Log.d(LOG_TAG, "First Visible: " + firstVisibleItem + ". Visible Count: " + visibleItemCount
                    + ". Total Items:" + totalItemCount);
        }

        /**
         * Set whether the Last Item is Visible. lastVisibleItemIndex is a
         * zero-based index, so we minus one totalItemCount to check
         */
        if (null != mOnLastItemVisibleListener) {
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        }

        // If we're showing the indicator, check positions...
        if (getShowIndicatorInternal()) {
            updateIndicatorViewsVisibility();
        }

        // Finally call OnScrollListener if we have one
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public final void onScrollStateChanged(AbsListView view, int state) {
        /**
         * Check that the scrolling has stopped, and that the last item is
         * visible.
         */
        if (state == OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
            mOnLastItemVisibleListener.onLastItemVisible();
        }

        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, state);
        }
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
     * getRefreshableView()}.
     * {@link AdapterView#setAdapter(Adapter)}
     * setAdapter(adapter)}. This is just for convenience!
     *
     * @param adapter - Adapter to set
     */
    public void setAdapter(ListAdapter adapter) {
        ((AdapterView<ListAdapter>) mRefreshableView).setAdapter(adapter);
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
     * getRefreshableView()}.
     * {@link AdapterView#setOnItemClickListener(OnItemClickListener)
     * setOnItemClickListener(listener)}. This is just for convenience!
     *
     * @param listener - OnItemClickListener to use
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mRefreshableView.setOnItemClickListener(listener);
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public final void setScrollEmptyView(boolean doScroll) {
        mScrollEmptyView = doScroll;
    }

    @Override
    protected void onPullToRefresh() {
        super.onPullToRefresh();
        if (getShowIndicatorInternal()) {
            switch (getCurrentMode()) {
                case PULL_FROM_END:
                    mIndicatorIvBottom.pullToRefresh();
                    break;
                case PULL_FROM_START:
                    mIndicatorIvTop.pullToRefresh();
                    break;
                default:
                    // NO-OP
                    break;
            }
        }
    }

    protected void onRefreshing(boolean doScroll) {
        super.onRefreshing(doScroll);
        if (getShowIndicatorInternal()) {
            updateIndicatorViewsVisibility();
        }
    }

    @Override
    protected void onReleaseToRefresh() {
        super.onReleaseToRefresh();
        if (getShowIndicatorInternal()) {
            switch (getCurrentMode()) {
                case PULL_FROM_END:
                    mIndicatorIvBottom.releaseToRefresh();
                    break;
                case PULL_FROM_START:
                    mIndicatorIvTop.releaseToRefresh();
                    break;
                default:
                    // NO-OP
                    break;
            }
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (getShowIndicatorInternal()) {
            updateIndicatorViewsVisibility();
        }
    }

    @Override
    protected void handleStyledAttributes() {
        // Set Show Indicator to the default value
        mShowIndicator = !isPullToRefreshOverScrollEnabled();
    }

    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void updateUIForMode() {
        super.updateUIForMode();
        // Check Indicator Views consistent with new Mode
        if (getShowIndicatorInternal()) {
            addIndicatorViews();
        } else {
            removeIndicatorViews();
        }
    }

    private void addIndicatorViews() {
        Mode mode = getMode();
        FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();
        if (mode.showHeaderLoadingLayout() && null == mIndicatorIvTop) {
            // If the mode can pull down, and we don't have one set already
            mIndicatorIvTop = new IndicatorLayout(getContext(), Mode.PULL_FROM_START);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = AppResources.getDimensionPixelSize("indicator_right_padding");
            params.gravity = Gravity.TOP | Gravity.RIGHT;
            refreshableViewWrapper.addView(mIndicatorIvTop, params);
        } else if (!mode.showHeaderLoadingLayout() && null != mIndicatorIvTop) {
            // If we can't pull down, but have a View then remove it
            refreshableViewWrapper.removeView(mIndicatorIvTop);
            mIndicatorIvTop = null;
        }

        if (mode.showFooterLoadingLayout() && null == mIndicatorIvBottom) {
            // If the mode can pull down, and we don't have one set already
            mIndicatorIvBottom = new IndicatorLayout(getContext(), Mode.PULL_FROM_END);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = AppResources.getDimensionPixelSize("indicator_right_padding");
            params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            refreshableViewWrapper.addView(mIndicatorIvBottom, params);
        } else if (!mode.showFooterLoadingLayout() && null != mIndicatorIvBottom) {
            // If we can't pull down, but have a View then remove it
            refreshableViewWrapper.removeView(mIndicatorIvBottom);
            mIndicatorIvBottom = null;
        }
    }

    private boolean getShowIndicatorInternal() {
        return mShowIndicator && isPullToRefreshEnabled();
    }

    private boolean isFirstItemVisible() {
        final Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
            }
            return true;

        } else {

            /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRefreshableView.getFirstVisiblePosition() <= 1) {
                final View firstVisibleChild = mRefreshableView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {
        final Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return true;
        } else {
            final int lastItemPosition = mRefreshableView.getCount() - 1;
            final int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();

            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
                        + lastVisiblePosition);
            }

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - mRefreshableView.getFirstVisiblePosition();
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }

        return false;
    }

    private void removeIndicatorViews() {
        if (null != mIndicatorIvTop) {
            getRefreshableViewWrapper().removeView(mIndicatorIvTop);
            mIndicatorIvTop = null;
        }

        if (null != mIndicatorIvBottom) {
            getRefreshableViewWrapper().removeView(mIndicatorIvBottom);
            mIndicatorIvBottom = null;
        }
    }

    private void updateIndicatorViewsVisibility() {
        if (null != mIndicatorIvTop) {
            if (!isRefreshing() && isReadyForPullStart()) {
                if (!mIndicatorIvTop.isVisible()) {
                    mIndicatorIvTop.show();
                }
            } else {
                if (mIndicatorIvTop.isVisible()) {
                    mIndicatorIvTop.hide();
                }
            }
        }

        if (null != mIndicatorIvBottom) {
            if (!isRefreshing() && isReadyForPullEnd()) {
                if (!mIndicatorIvBottom.isVisible()) {
                    mIndicatorIvBottom.show();
                }
            } else {
                if (mIndicatorIvBottom.isVisible()) {
                    mIndicatorIvBottom.hide();
                }
            }
        }
    }
}
