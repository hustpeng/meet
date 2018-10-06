package com.agmbat.meetyou.tab.msg;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecycleView的分割线实现类
 */

public class DividerDecoration extends RecyclerView.ItemDecoration{

    private int mOrientation;
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Paint mLinePaint; //画笔
    private int mDividerHeight;//分割线高度
    private int mPadding;//分割线距离两边的距离

    public DividerDecoration(int color, int dividerHeight, int orientation) {
        this(color, dividerHeight, orientation, 0);
    }

    public DividerDecoration(int color, int dividerHeight, int orientation, int padding) {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(color);
        mDividerHeight = dividerHeight;
        mPadding = padding;
        setOrientation(orientation);
    }


    //设置屏幕的方向
    public void setOrientation(int orientation){
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST){
            drawVerticalLine(canvas, parent, state);
        }else {
            drawHorizontalLine(canvas, parent, state);
        }
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    public void drawHorizontalLine(Canvas canvas, RecyclerView parent, RecyclerView.State state){
        int left = parent.getPaddingLeft() + mPadding;
        int right = parent.getWidth() - parent.getPaddingRight() - mPadding;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);

            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;

            canvas.save();
            //如果有padding，那么分割线不会撑满一行，左右两边padding的部分会是透明的，可以看到RecyclerView背景色
            if(mPadding > 0) {
                Drawable background = child.getBackground();
                if (null != background) {
                    //使用内容的背景图，保证分割线的背景跟上面内容的背景一致
                    Rect dividerRect = new Rect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
                    Drawable lineBg = background.getConstantState().newDrawable();//注意：不能使用原来的背景图，需要复制一个新的drawable
                    lineBg.setBounds(dividerRect);
                    lineBg.draw(canvas);
                }
            }
            //开始画分割线
            canvas.drawRect(left, top, right, bottom, mLinePaint);
            canvas.restore();
        }
    }

    //画竖线
    public void drawVerticalLine(Canvas canvas, RecyclerView parent, RecyclerView.State state){
        int top = parent.getPaddingTop() + mPadding;
        int bottom = parent.getHeight() - parent.getPaddingBottom() - mPadding;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);

            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerHeight;

            canvas.save();
            //如果有padding，那么分割线不会撑满一列，上下两边padding的部分会是透明的，可以看到RecyclerView背景色
            if(mPadding > 0) {
                Drawable background = child.getBackground();
                if (null != background) {
                    //使用内容的背景图，保证分割线的背景跟左边内容的背景一致
                    Rect dividerRect = new Rect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
                    Drawable lineBg = background.getConstantState().newDrawable();//注意：不能使用原来的背景图，需要复制一个新的drawable
                    lineBg.setBounds(dividerRect);
                    lineBg.draw(canvas);
                }
            }
            canvas.drawRect(left, top, right, bottom, mLinePaint);
            canvas.restore();
        }
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL_LIST){
            //画竖线，就是往右偏移一个分割线的宽度
            outRect.set(0, 0, mDividerHeight, 0);
        }else {
            //画横线，就是往下偏移一个分割线的高度
            outRect.set(0, 0, 0, mDividerHeight);
        }
    }
}
