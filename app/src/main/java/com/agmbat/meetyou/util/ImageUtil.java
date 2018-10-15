package com.agmbat.meetyou.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;


/**
 * Created by MPC on 2017/10/10.
 */

public class ImageUtil {

    public static void loadImage(Context context, ImageView imageView, String url, int defaultIcon) {
        loadImage(context, imageView, url, defaultIcon, null);
    }

    public static void loadImage(Context context, ImageView imageView, String url, Drawable defaultIcon) {
        loadImage(context, imageView, url, defaultIcon, null);
    }

    public static void loadImage(Context context, ImageView imageView, String url, int defaultIcon, final ImageListener listener) {
        loadImage(context, imageView, url, ResourceUtil.getDrawable(defaultIcon), listener);
    }

    public static void loadImage(Context context, ImageView imageView, String url, Drawable defaultIcon, final ImageListener listener) {
        try {
            Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onLoadFailed();
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onDrawableLoaded(resource);
                    }
                    return false;
                }
            }).error(defaultIcon).placeholder(defaultIcon).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCircleImage(Context context, ImageView imageView, String url, int defaultIcon) {
        loadCircleImage(context, imageView, url, defaultIcon, null);
    }

    public static void loadCircleImage(Context context, ImageView imageView, String url, Drawable defaultIcon) {
        loadCircleImage(context, imageView, url, defaultIcon, null);
    }

    public static void loadCircleImage(Context context, ImageView imageView, String url, int defaultIcon, final ImageListener listener) {
        loadCircleImage(context, imageView, url, ResourceUtil.getDrawable(defaultIcon), listener);
    }

    public static void loadCircleImage(Context context, ImageView imageView, String url, Drawable defaultIcon, final ImageListener listener) {
        try {
            //把默认图也裁剪成圆形
            Drawable defaultCircleIcon = null;
            if (null != defaultIcon) {
                defaultCircleIcon = new CircleDrawable(defaultIcon);
            }
            Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onLoadFailed();
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onDrawableLoaded(resource);
                    }
                    return false;
                }
            }).transform(new GlideCircleTransform(context)).error(defaultCircleIcon).placeholder(defaultCircleIcon).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadRoundCornerImage(Context context, ImageView imageView, String url, int defaultIcon, int radius) {
        loadRoundCornerImage(context, imageView, url, defaultIcon, radius, null);
    }

    public static void loadRoundCornerImage(Context context, ImageView imageView, String url, Drawable defaultIcon, int radius) {
        loadRoundCornerImage(context, imageView, url, defaultIcon, radius, null);
    }

    public static void loadRoundCornerImage(Context context, ImageView imageView, String url, int defaultIcon, int radius, ImageListener listener) {
        loadRoundCornerImage(context, imageView, url, ResourceUtil.getDrawable(defaultIcon), radius, listener);
    }

    public static void loadRoundCornerImage(Context context, ImageView imageView, String url, Drawable defaultIcon, int radius, final ImageListener listener) {
        try {
            Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onLoadFailed();
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onDrawableLoaded(resource);
                    }
                    return false;
                }
            }).transform(new GlideRoundTransform(context, radius)).error(defaultIcon).placeholder(defaultIcon).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap createCircleBitmap(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, x, y, paint);
        return target;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
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
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
        return drawable;
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb(比如: 32kb=32*1024)
     *
     * @param bitmap
     * @param
     * @return
     */
    public static byte[] compressBitmap(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        byte[] bitmapBytes = output.toByteArray();
        IoUtils.close(output);
        return bitmapBytes;
    }

    public static Bitmap createHorizontalRepeater(int width, Bitmap src) {
        int count = (width + src.getWidth() - 1) / src.getWidth(); //计算出平铺填满所给width（宽度）最少需要的重复次数
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth() * count, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    public static Bitmap createVerticalRepeater(int height, Bitmap src) {
        int count = (height + src.getHeight() - 1) / src.getHeight(); //计算出平铺填满所给height（高度）最少需要的重复次数
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight() * count, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, 0, idx * src.getHeight(), null);
        }
        return bitmap;
    }

    public interface ImageListener {
        void onDrawableLoaded(Drawable drawable);

        void onLoadFailed();
    }

    /**
     * 图片加圆角转化器
     */
    public static class GlideRoundTransform extends BitmapTransformation {

        private float radius = 0f;

        public GlideRoundTransform(Context context, int radiuspx) {
            super(context);
            this.radius = radiuspx;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }

    /**
     * 图片裁剪为圆形转换器
     */
    public static class GlideCircleTransform extends BitmapTransformation {

        public GlideCircleTransform(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

}
