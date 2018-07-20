package com.agmbat.meetyou.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;


/**
 * 圆形的Drawable
 * Created by feiyang on 16/2/18.
 */
public class CircleDrawable extends Drawable {

    private Paint mPaint;
    private Bitmap mBitmap;
    private int mRadius;

    public CircleDrawable(Bitmap bitmap) {
        initWithBitmap(bitmap);
    }


    public CircleDrawable(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        initWithBitmap(bitmap);
    }

    private void initWithBitmap(Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if(bitmapWidth != bitmapHeight){
            //创建一个正方形bitmap，把原来的bitmap图像绘制到正中间
            Paint paint = new Paint();
            int size = Math.max(bitmapWidth, bitmapHeight); //按最长的边来确定正方形边长（或者按最小的边来确定也可以）
            mBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);

            int left = (size - bitmapWidth) / 2;
            int top = (size - bitmapHeight) / 2;
            canvas.drawBitmap(bitmap, left, top, paint);
            bitmap.recycle();
            mRadius = size / 2;
        }else{
            mBitmap = bitmap;
            mRadius = bitmapWidth / 2;
        }
        BitmapShader bitmapShader = new BitmapShader(mBitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

