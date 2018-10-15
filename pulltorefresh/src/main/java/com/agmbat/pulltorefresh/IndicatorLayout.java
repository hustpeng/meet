package com.agmbat.pulltorefresh;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.agmbat.android.AppResources;

public class IndicatorLayout extends FrameLayout implements AnimationListener {

    static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;
    private final Animation mRotateAnimation;
    private final Animation mResetRotateAnimation;
    private Animation mInAnim;
    private Animation mOutAnim;
    private ImageView mArrowImageView;

    public IndicatorLayout(Context context, PullToRefreshBase.Mode mode) {
        super(context);
        mArrowImageView = new ImageView(context);

        Drawable arrowD = AppResources.getDrawable("ptr_indicator_arrow");
        mArrowImageView.setImageDrawable(arrowD);

        final int padding = AppResources.getDimensionPixelSize("indicator_internal_padding");
        mArrowImageView.setPadding(padding, padding, padding, padding);
        addView(mArrowImageView);

        int inAnimResId;
        int outAnimResId;
        switch (mode) {
            case PULL_FROM_END:
                inAnimResId = AppResources.getAnimId("ptr_slide_in_from_bottom");
                outAnimResId = AppResources.getAnimId("ptr_slide_out_to_bottom");
                setBackgroundResource(AppResources.getDrawableId("ptr_indicator_bg_bottom"));

                // Rotate Arrow so it's pointing the correct way
                mArrowImageView.setScaleType(ScaleType.MATRIX);
                Matrix matrix = new Matrix();
                matrix.setRotate(180f, arrowD.getIntrinsicWidth() / 2f, arrowD.getIntrinsicHeight() / 2f);
                mArrowImageView.setImageMatrix(matrix);
                break;
            default:
            case PULL_FROM_START:
                inAnimResId = AppResources.getAnimId("ptr_slide_in_from_top");
                outAnimResId = AppResources.getAnimId("ptr_slide_out_to_top");
                setBackgroundResource(AppResources.getDrawableId("ptr_indicator_bg_top"));
                break;
        }

        mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
        mInAnim.setAnimationListener(this);

        mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
        mOutAnim.setAnimationListener(this);

        final Interpolator interpolator = new LinearInterpolator();
        mRotateAnimation =
                new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(interpolator);
        mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);

        mResetRotateAnimation =
                new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResetRotateAnimation.setInterpolator(interpolator);
        mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        mResetRotateAnimation.setFillAfter(true);

    }

    public final boolean isVisible() {
        Animation currentAnim = getAnimation();
        if (null != currentAnim) {
            return mInAnim == currentAnim;
        }

        return getVisibility() == View.VISIBLE;
    }

    public void hide() {
        startAnimation(mOutAnim);
    }

    public void show() {
        mArrowImageView.clearAnimation();
        startAnimation(mInAnim);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == mOutAnim) {
            mArrowImageView.clearAnimation();
            setVisibility(View.GONE);
        } else if (animation == mInAnim) {
            setVisibility(View.VISIBLE);
        }

        clearAnimation();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(View.VISIBLE);
    }

    public void releaseToRefresh() {
        mArrowImageView.startAnimation(mRotateAnimation);
    }

    public void pullToRefresh() {
        mArrowImageView.startAnimation(mResetRotateAnimation);
    }

}
