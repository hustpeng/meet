package com.agmbat.pulltorefresh.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.agmbat.pulltorefresh.LoadingLayout;
import com.agmbat.pulltorefresh.OverScrollHelper;
import com.agmbat.pulltorefresh.PullToRefreshAdapterViewBase;

/**
 * 支持下拉刷新的ListView
 */
public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {

    protected FrameLayout mLvFooterLoadingFrame;
    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = createListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    @Override
    protected void handleStyledAttributes() {
        super.handleStyledAttributes();
        final FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        // Create Loading Views ready for use later
        FrameLayout frame = new FrameLayout(getContext());
        mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START);
        mHeaderLoadingView.setVisibility(View.GONE);
        frame.addView(mHeaderLoadingView, lp);
        mRefreshableView.addHeaderView(frame, null, false);
        mLvFooterLoadingFrame = new FrameLayout(getContext());
        mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END);
        mFooterLoadingView.setVisibility(View.GONE);
        mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);
        /**
         * If the value for Scrolling While Refreshing hasn't been explicitly set via XML, enable Scrolling While
         * Refreshing.
         */
        setScrollingWhileRefreshingEnabled(true);
    }

    @Override
    protected void onRefreshing(final boolean doScroll) {
        /**
         * If we're not showing the Refreshing view, or the list is empty, the the header/footer views won't show so we
         * use the normal method.
         */
        ListAdapter adapter = mRefreshableView.getAdapter();
        if (!true || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }

        super.onRefreshing(false);

        final LoadingLayout origLoadingView;
        final LoadingLayout listViewLoadingView;
        final LoadingLayout oppositeListViewLoadingView;
        final int selection;
        final int scrollToY;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                origLoadingView = getFooterLayout();
                listViewLoadingView = mFooterLoadingView;
                oppositeListViewLoadingView = mHeaderLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();
                break;
            case PULL_FROM_START:
            default:
                origLoadingView = getHeaderLayout();
                listViewLoadingView = mHeaderLoadingView;
                oppositeListViewLoadingView = mFooterLoadingView;
                selection = 0;
                scrollToY = getScrollY() + getHeaderSize();
                break;
        }

        // Hide our original Loading View
        origLoadingView.reset();
        origLoadingView.hideAllViews();

        // Make sure the opposite end is hidden too
        oppositeListViewLoadingView.setVisibility(View.GONE);

        // Show the ListView Loading View and set it to refresh.
        listViewLoadingView.setVisibility(View.VISIBLE);
        listViewLoadingView.refreshing();

        if (doScroll) {
            // We need to disable the automatic visibility changes for now
            disableLoadingLayoutVisibilityChanges();

            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);

            // Make sure the ListView is scrolled to show the loading
            // header/footer
            mRefreshableView.setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }
    }

    @Override
    protected void onReset() {
        resetListView();
        // Finally, call up to super
        super.onReset();
    }

    @Override
    protected void onResetRefreshComplete() {
        resetListView();
        super.onResetRefreshComplete();
    }

    private void resetListView() {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        final LoadingLayout originalLoadingLayout;
        final LoadingLayout listViewLoadingLayout;
        final int scrollToHeight;
        final int selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToHeight = getFooterSize();
                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
                break;
            case PULL_FROM_START:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                scrollToHeight = -getHeaderSize();
                selection = 0;
                scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
                break;
        }

        // If the ListView header loading layout is showing, then we need to
        // flip so that the original one is showing instead
        if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {
            // Set our Original View to Visible
            originalLoadingLayout.showInvisibleViews();
            // Hide the ListView Header/Footer
            listViewLoadingLayout.setVisibility(View.GONE);
            /**
             * Scroll so the View is at the same Y as the ListView header/footer, but only scroll if: we've pulled to
             * refresh, it's positioned correctly
             */
            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                mRefreshableView.setSelection(selection);
                setHeaderScroll(scrollToHeight);
            }
        }
    }

    protected ListView createListView(Context context, AttributeSet attrs) {
        final ListView lv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            lv = new InternalListViewSDK9(context, attrs);
        } else {
            lv = new InternalListView(context, attrs);
        }
        return lv;
    }


    @TargetApi(9)
    final class InternalListViewSDK9 extends InternalListView {

        public InternalListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            final boolean returnValue =
                    super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
                            maxOverScrollY, isTouchEvent);
            // Does all of the hard work...
            OverScrollHelper.overScrollBy(PullToRefreshListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);
            return returnValue;
        }
    }

    protected class InternalListView extends ListView {

        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it when using Header/Footer Views and the
             * list is empty. This masks the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it when using Header/Footer Views and the
             * list is empty. This masks the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }
            super.setAdapter(adapter);
        }

    }

}
