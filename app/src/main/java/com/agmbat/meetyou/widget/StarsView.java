package com.agmbat.meetyou.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agmbat.android.SysResources;
import com.agmbat.meetyou.R;

public class StarsView extends LinearLayout {

    private int mSpacing;
    private Drawable mStarsIcon;
    private int mCount;
    private int mStarsSize;

    public StarsView(Context context) {
        this(context, null);
    }

    public StarsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StarsView);
        mSpacing = typedArray.getDimensionPixelSize(R.styleable.StarsView_spacing, (int) SysResources.dipToPixel(5));
        mStarsIcon = typedArray.getDrawable(R.styleable.StarsView_star);
        mCount = typedArray.getInt(R.styleable.StarsView_count, 0);
        mStarsSize = typedArray.getDimensionPixelSize(R.styleable.StarsView_size, (int) SysResources.dipToPixel(15));
        typedArray.recycle();
        initView();
    }


    private void initView() {
        setOrientation(HORIZONTAL);
        initStarsLayout();
    }

    private void initStarsLayout() {
        removeAllViews();
        if (null == mStarsIcon || mCount == 0) {
            return;
        }
        for (int i = 0; i < mCount; i++) {
            ImageView imageView = crateStartImage();
            MarginLayoutParams params = (MarginLayoutParams) imageView.getLayoutParams();
            if (i != mCount - 1) {
                params.rightMargin = mSpacing;
                imageView.setLayoutParams(params);
            }
            addView(imageView);
        }
    }

    public void setStarsCount(int count) {
        mCount = count;
        initStarsLayout();
    }

    public void setStarsIcon(int iconRes) {
        mStarsIcon = getResources().getDrawable(iconRes);
        initStarsLayout();
    }

    public void setStarsIcon(Drawable starsIcon) {
        mStarsIcon = starsIcon;
        initStarsLayout();
    }


    private ImageView crateStartImage() {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(mStarsIcon);
        if (mStarsSize > 0) {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(mStarsSize, mStarsSize));
        } else {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        return imageView;
    }

}
