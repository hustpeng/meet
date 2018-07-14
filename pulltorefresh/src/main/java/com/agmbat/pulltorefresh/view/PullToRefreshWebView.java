package com.agmbat.pulltorefresh.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.agmbat.pulltorefresh.OverScrollHelper;
import com.agmbat.pulltorefresh.PullToRefreshBase;

/**
 * 支持下拉刷新的WebView
 */
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {

    private final OnRefreshListener<WebView> defaultOnRefreshListener = new OnRefreshListener<WebView>() {

        @Override
        public void onPullStartToRefresh(PullToRefreshBase<WebView> refreshView) {
            refreshView.getRefreshableView().reload();
        }

        @Override
        public void onPullEndToRefresh(PullToRefreshBase<WebView> refreshView) {
        }

    };

    private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                onRefreshComplete();
            }
        }

    };

    public PullToRefreshWebView(Context context) {
        super(context);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(defaultOnRefreshListener);
        mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(defaultOnRefreshListener);
        mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    public PullToRefreshWebView(Context context, Mode mode) {
        super(context, mode);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(defaultOnRefreshListener);
        mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    public PullToRefreshWebView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(defaultOnRefreshListener);
        mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected WebView createRefreshableView(Context context, AttributeSet attrs) {
        WebView webView;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            webView = new InternalWebViewSDK9(context, attrs);
        } else {
            webView = new WebView(context, attrs);
        }
        return webView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        float height = mRefreshableView.getContentHeight() * mRefreshableView.getScale();
        float exactContentHeight = (float) Math.floor(height);
        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
    }

    @Override
    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
        super.onPtrRestoreInstanceState(savedInstanceState);
        mRefreshableView.restoreState(savedInstanceState);
    }

    @Override
    protected void onPtrSaveInstanceState(Bundle saveState) {
        super.onPtrSaveInstanceState(saveState);
        mRefreshableView.saveState(saveState);
    }

    @TargetApi(9)
    final class InternalWebViewSDK9 extends WebView {

        // WebView doesn't always scroll back to it's edge so we add some
        // fuzziness
        static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

        // WebView seems quite reluctant to overscroll so we use the scale
        // factor to scale it's value
        static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

        public InternalWebViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
            // Does all of the hard work...
            OverScrollHelper.overScrollBy(PullToRefreshWebView.this, deltaX, scrollX, deltaY, scrollY,
                    getScrollRange(), OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);
            return returnValue;
        }

        private int getScrollRange() {
            return (int) Math.max(0, Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale())
                    - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
    }
}
