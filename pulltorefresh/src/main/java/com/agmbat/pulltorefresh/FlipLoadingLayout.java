package com.agmbat.pulltorefresh;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

/**
 * 翻转loading控件
 */
public class FlipLoadingLayout extends LoadingLayout {

    private static final int FLIP_ANIMATION_DURATION = 150;

    private final Animation mRotateAnimation;
    private final Animation mResetRotateAnimation;

    public FlipLoadingLayout(Context context, final PullToRefreshBase.Mode mode,
                             final PullToRefreshBase.Orientation scrollDirection) {
        super(context, mode, scrollDirection);
        final int rotateAngle = mode == PullToRefreshBase.Mode.PULL_FROM_START ? -180 : 180;
        mRotateAnimation =
                new RotateAnimation(0, rotateAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);
        mResetRotateAnimation =
                new RotateAnimation(rotateAngle, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResetRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mResetRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        mResetRotateAnimation.setFillAfter(true);
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            final int dHeight = imageDrawable.getIntrinsicHeight();
            final int dWidth = imageDrawable.getIntrinsicWidth();
            /**
             * We need to set the width/height of the ImageView so that it is square with each side the size of the
             * largest drawable dimension. This is so that it doesn't clip when rotated.
             */
            ViewGroup.LayoutParams lp = mHeaderImage.getLayoutParams();
            lp.width = lp.height = Math.max(dHeight, dWidth);
            mHeaderImage.requestLayout();
            /**
             * We now rotate the Drawable so that is at the correct rotation, and is centered.
             */
            mHeaderImage.setScaleType(ScaleType.MATRIX);
            Matrix matrix = new Matrix();
            matrix.postTranslate((lp.width - dWidth) / 2f, (lp.height - dHeight) / 2f);
            matrix.postRotate(getDrawableRotationAngle(), lp.width / 2f, lp.height / 2f);
            mHeaderImage.setImageMatrix(matrix);
        }
    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
    }

    @Override
    protected void pullToRefreshImpl() {
        // Only start reset Animation, we've previously show the rotate anim
        if (mRotateAnimation == mHeaderImage.getAnimation()) {
            mHeaderImage.startAnimation(mResetRotateAnimation);
        }
    }

    @Override
    protected void refreshingImpl() {
        mHeaderImage.clearAnimation();
        mHeaderImage.setVisibility(View.INVISIBLE);
        mHeaderProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void releaseToRefreshImpl() {
        mHeaderImage.startAnimation(mRotateAnimation);
    }

    @Override
    protected void resetImpl() {
        mHeaderImage.clearAnimation();
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void resetRefreshCompleteImpl() {
        resetImpl();
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.ptr_default_flip;
    }

    private float getDrawableRotationAngle() {
        float angle = 0f;
        if (mMode == PullToRefreshBase.Mode.PULL_FROM_END) {
            if (mScrollDirection == PullToRefreshBase.Orientation.HORIZONTAL) {
                angle = 90f;
            } else {
                angle = 180f;
            }
        } else if (mMode == PullToRefreshBase.Mode.PULL_FROM_START) {
            if (mScrollDirection == PullToRefreshBase.Orientation.HORIZONTAL) {
                angle = 270f;
            }
        }
        return angle;
    }

}