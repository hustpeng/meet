package com.agmbat.pulltorefresh.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.GridView;

import com.agmbat.pulltorefresh.OverScrollHelper;
import com.agmbat.pulltorefresh.PullToRefreshAdapterViewBase;

/**
 * 支持下拉刷新的PullToRefreshGridView
 */
public class PullToRefreshGridView extends PullToRefreshAdapterViewBase<GridView> {

    public PullToRefreshGridView(Context context) {
        super(context);
    }

    public PullToRefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshGridView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshGridView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected final GridView createRefreshableView(Context context, AttributeSet attrs) {
        final GridView gv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            gv = new InternalGridViewSDK9(context, attrs);
        } else {
            gv = new GridView(context, attrs);
        }
        return gv;
    }

    @TargetApi(9)
    final class InternalGridViewSDK9 extends GridView {

        public InternalGridViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            final boolean returnValue =
                    super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
                            maxOverScrollY, isTouchEvent);
            // Does all of the hard work...
            OverScrollHelper.overScrollBy(PullToRefreshGridView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);
            return returnValue;
        }
    }
}
