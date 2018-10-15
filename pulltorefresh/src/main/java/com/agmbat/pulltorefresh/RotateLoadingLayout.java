package com.agmbat.pulltorefresh;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

/**
 * 旋转loading控件
 */
public class RotateLoadingLayout extends LoadingLayout {

    static final int ROTATION_ANIMATION_DURATION = 1200;

    private final Animation mRotateAnimation;
    private final Matrix mHeaderImageMatrix;
    private final boolean mRotateDrawableWhilePulling;
    private float mRotationPivotX;
    private float mRotationPivotY;

    public RotateLoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection) {
        super(context, mode, scrollDirection);
        mRotateDrawableWhilePulling = true;
        mHeaderImage.setScaleType(ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        mRotateAnimation =
                new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
            mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
        }
    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
        float angle;
        if (mRotateDrawableWhilePulling) {
            angle = scaleOfLayout * 90f;
        } else {
            angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
        }
        mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
        mHeaderImage.setImageMatrix(mHeaderImageMatrix);
    }

    @Override
    protected void refreshingImpl() {
        mHeaderImage.setImageResource(R.drawable.ptr_refreshing);
        mHeaderImage.startAnimation(mRotateAnimation);
    }

    @Override
    protected void resetImpl() {
        mHeaderImage.clearAnimation();
        resetImageRotation();
    }

    @Override
    protected void resetRefreshCompleteImpl() {
        mHeaderImage.setImageResource(R.drawable.ptr_refresh_complete);
        mHeaderImage.clearAnimation();
        resetImageRotation();
    }

    @Override
    protected void pullToRefreshImpl() {
        mHeaderImage.setImageResource(getDefaultDrawableResId());
    }

    @Override
    protected void releaseToRefreshImpl() {
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.ptr_default_rotate;
    }

    private void resetImageRotation() {
        if (null != mHeaderImageMatrix) {
            mHeaderImageMatrix.reset();
            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        }
    }
}
