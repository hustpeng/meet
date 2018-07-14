package com.agmbat.pulltorefresh;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Loading基类
 */
public abstract class LoadingLayout extends FrameLayout {

    static final String LOG_TAG = "PullToRefresh-LoadingLayout";

    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

    private FrameLayout mInnerLayout;

    protected final ImageView mHeaderImage;
    protected final ProgressBar mHeaderProgress;

    private boolean mUseIntrinsicAnimation;

    private final TextView mHeaderText;
    private final TextView mSubHeaderText;

    protected final PullToRefreshBase.Mode mMode;
    protected final PullToRefreshBase.Orientation mScrollDirection;

    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;
    private CharSequence mReleaseCompleteLabel;

    public LoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection) {
        super(context);
        mMode = mode;
        mScrollDirection = scrollDirection;
        switch (scrollDirection) {
            case HORIZONTAL:
                View.inflate(context, R.layout.ptr_header_horizontal, this);
                break;
            case VERTICAL:
            default:
                View.inflate(context, R.layout.ptr_header_vertical, this);
                break;
        }
        mInnerLayout = (FrameLayout) findViewById(R.id.ptr_fl_inner);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.ptr_text);
        mHeaderProgress = (ProgressBar) mInnerLayout.findViewById(R.id.ptr_progress);
        mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.ptr_sub_text);
        mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.ptr_image);

        LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();

        switch (mode) {
            case PULL_FROM_END:
                lp.gravity = scrollDirection == PullToRefreshBase.Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;
                // Load in labels
                mPullLabel = context.getString(R.string.ptr_from_bottom_pull_label);
                mRefreshingLabel = context.getString(R.string.ptr_from_bottom_refreshing_label);
                mReleaseLabel = context.getString(R.string.ptr_from_bottom_release_label);
                mReleaseCompleteLabel = context.getString(R.string.ptr_from_bottom_refresh_complete_label);
                break;
            case PULL_FROM_START:
            default:
                lp.gravity = scrollDirection == PullToRefreshBase.Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;
                // Load in labels
                mPullLabel = context.getString(R.string.ptr_pull_label);
                mRefreshingLabel = context.getString(R.string.ptr_refreshing_label);
                mReleaseLabel = context.getString(R.string.ptr_release_label);
                mReleaseCompleteLabel = context.getString(R.string.ptr_refresh_complete_label);
                break;
        }
        Drawable imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());
        // Set Drawable, and save width/height
        setLoadingDrawable(imageDrawable);
        reset();
    }

    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    public final int getContentSize() {
        switch (mScrollDirection) {
            case HORIZONTAL:
                return mInnerLayout.getWidth();
            case VERTICAL:
            default:
                return mInnerLayout.getHeight();
        }
    }

    public final void hideAllViews() {
        if (View.VISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderProgress.getVisibility()) {
            mHeaderProgress.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mSubHeaderText.getVisibility()) {
            mSubHeaderText.setVisibility(View.INVISIBLE);
        }
    }

    public final void onPull(float scaleOfLayout) {
        if (!mUseIntrinsicAnimation) {
            onPullImpl(scaleOfLayout);
        }
    }

    public final void pullToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
        // Now call the callback
        pullToRefreshImpl();
    }

    /**
     * 正在刷新中
     */
    public final void refreshing() {
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingLabel);
        }
        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).start();
        } else {
            // Now call the callback
            refreshingImpl();
        }
        if (null != mSubHeaderText) {
            mSubHeaderText.setVisibility(View.GONE);
        }
    }

    public final void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }
        // Now call the callback
        releaseToRefreshImpl();
    }

    /**
     * PullToRefreshBase在Reset状态时调用
     */
    public final void reset() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
        mHeaderImage.setVisibility(View.VISIBLE);
        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).stop();
        } else {
            // Now call the callback
            resetImpl();
        }
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(mSubHeaderText.getText())) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * PullToRefreshBase在ResetRefreshComplete状态时调用
     */
    public final void resetRefreshComplete() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseCompleteLabel);
        }
        mHeaderImage.setVisibility(View.VISIBLE);
        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).stop();
        } else {
            // Now call the callback
            resetRefreshCompleteImpl();
        }
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(mSubHeaderText.getText())) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Set the Last Updated Text. This displayed under the main label when
     * Pulling
     *
     * @param label - Label to set
     */
    public void setLastUpdatedLabel(CharSequence label) {
        setSubHeaderText(label);
    }

    public final void setLoadingDrawable(Drawable imageDrawable) {
        // Set Drawable
        mHeaderImage.setImageDrawable(imageDrawable);
        mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);
        // Now call the callback
        onLoadingDrawableSet(imageDrawable);
    }

    public void setPullLabel(CharSequence pullLabel) {
        mPullLabel = pullLabel;
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mRefreshingLabel = refreshingLabel;
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
        mReleaseLabel = releaseLabel;
    }

    /**
     * Set's the Sets the typeface and style in which the text should be
     * displayed. Please see
     * {@link TextView#setTypeface(Typeface)
     * TextView#setTypeface(Typeface)}.
     */
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }

    public final void showInvisibleViews() {
        if (View.INVISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
            mHeaderProgress.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
            mSubHeaderText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Callbacks for derivative Layouts
     */
    protected abstract int getDefaultDrawableResId();

    protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

    protected abstract void onPullImpl(float scaleOfLayout);

    protected abstract void pullToRefreshImpl();

    /**
     * 处理释放后刷新
     */
    protected abstract void refreshingImpl();

    protected abstract void releaseToRefreshImpl();

    protected abstract void resetImpl();

    protected abstract void resetRefreshCompleteImpl();

    private void setSubHeaderText(CharSequence label) {
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(label)) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setText(label);
                // Only set it to Visible if we're GONE, otherwise VISIBLE will
                // be set soon
                if (View.GONE == mSubHeaderText.getVisibility()) {
                    mSubHeaderText.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
