package com.agmbat.android.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;

import com.agmbat.io.IoUtils;
import com.agmbat.log.Log;
import com.agmbat.utils.Asserts;
import com.agmbat.utils.MathUtils;
import com.agmbat.utils.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 图片处理工具类
 */
public class BitmapUtils {

    public static final int UNCONSTRAINED = -1;
    private static final String TAG = "BitmapUtils";
    private static final int DEFAULT_JPEG_QUALITY = 90;


    private BitmapUtils() {
    }

    /*
     * Compute the sample size as a function of minSideLength and maxNumOfPixels. minSideLength is used to specify that
     * minimal width or height of a bitmap. maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints. Both size and minSideLength can be passed in as
     * UNCONSTRAINED, which indicates no care of the corresponding constraint. The functions prefers returning a sample
     * size that generates a smaller bitmap, unless minSideLength = UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple of 8 because BitmapFactory only honors
     * sample size this way. For example, BitmapFactory downsamples an image by 2 even though the request is 3. So we
     * round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(int width, int height, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(width, height, minSideLength, maxNumOfPixels);
        return initialSize <= 8 ? MathUtils.nextPowerOf2(initialSize) : (initialSize + 7) / 8 * 8;
    }

    /**
     * inSampleSize的默认值和最小值为1（当小于1时，解码器将该值当做1来处理），
     * 且在大于1时，该值只能为2的幂（当不为2的幂时，解码器会取与该值最接近的2的幂）。
     * 例如，当inSampleSize为2时，一个20001000的图片，将被缩小为1000500，相应地，它的像素数和内存占用都被缩小为了原来的1/4：
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    /**
     * Find the max x that 1 / x <= scale.
     * <p>
     * inSampleSize的默认值和最小值为1（当小于1时，解码器将该值当做1来处理），
     * 且在大于1时，该值只能为2的幂（当不为2的幂时，解码器会取与该值最接近的2的幂）。
     * 例如，当inSampleSize为2时，一个20001000的图片，将被缩小为1000500，相应地，它的像素数和内存占用都被缩小为了原来的1/4：
     */
    public static int computeSampleSize(float scale) {
        Asserts.assertTrue(scale > 0);
        int initialSize = Math.max(1, (int) Math.ceil(1 / scale));
        return initialSize <= 8 ? MathUtils.nextPowerOf2(initialSize) : (initialSize + 7) / 8 * 8;
    }

    /**
     * Find the min x that 1 / x >= scale
     * 得到较小的SampleSize, 缩放值较小, 输出图片较大
     */
    public static int computeSampleSizeLarger(float scale) {
        int initialSize = (int) Math.floor(1f / scale);
        if (initialSize <= 1) {
            return 1;
        }
        return initialSize <= 8 ? MathUtils.prevPowerOf2(initialSize) : initialSize / 8 * 8;
    }


    private static int computeInitialSampleSize(int w, int h, int minSideLength, int maxNumOfPixels) {
        if (maxNumOfPixels == UNCONSTRAINED && minSideLength == UNCONSTRAINED) {
            return 1;
        }
        int lowerBound =
                (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt((float) (w * h)
                        / maxNumOfPixels));

        if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            int sampleSize = Math.min(w / minSideLength, h / minSideLength);
            return Math.max(sampleSize, lowerBound);
        }
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound =
                (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * This computes a sample size which makes the longer side at least
     * minSideLength long. If that's not possible, return 1.
     */
    public static int computeSampleSizeLarger(int w, int h, int minSideLength) {
        int initialSize = Math.max(w / minSideLength, h / minSideLength);
        if (initialSize <= 1) {
            return 1;
        }
        return initialSize <= 8 ? MathUtils.prevPowerOf2(initialSize) : initialSize / 8 * 8;
    }


    public static Bitmap resizeDownBySideLength(Bitmap bitmap, int maxLength, boolean recycle) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.min((float) maxLength / srcWidth, (float) maxLength / srcHeight);
        if (scale >= 1.0f) {
            return bitmap;
        }
        return resizeBitmapByScale(bitmap, scale, recycle);
    }

    /**
     * 重新设置大小, 并且裁剪居中
     * <p>
     * scale the image so that the shorter side equals to the target;
     * the longer side will be center-cropped.
     *
     * @param bitmap
     * @param size
     * @param recycle
     * @return
     */
    public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (w == size && h == size) {
            return bitmap;
        }
        float scale = (float) size / Math.min(w, h);
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) {
            recycleSilently(bitmap);
        }
        return target;
    }

    /**
     * 对图片进行缩放和裁剪成指定大小的图片,等比缩放
     *
     * @param bitmap
     * @param outW
     * @param outH
     * @return
     */
    public static Bitmap resizeBitmapScale(Bitmap bitmap, int outW, int outH) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        float scaleWidth = ((float) outW / bw);
        float scaleHeight = ((float) outH / bh);
        float scale = Math.max(scaleWidth, scaleHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 等比放大/缩小
        return Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix, true);
    }


    /**
     * 将图片进行缩放
     *
     * @param bitmap
     * @param scale
     * @param recycle
     * @return
     */
    public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle) {
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
            return bitmap;
        }
        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) {
            recycleSilently(bitmap);
        }
        return target;
    }

    /**
     * 对图片进行缩放和裁剪成指定大小的图片,输出图片可能不是等比,所以会导致图片压缩
     *
     * @param bitmap
     * @param outW
     * @param outH
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int outW, int outH) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = (float) outW / bw;
        float scaleHeight = (float) outH / bh;
        matrix.postScale(scaleWidht, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix, true);
    }

    /**
     * 旋转图片
     *
     * @param source   原图
     * @param rotation 需要旋转的角度
     * @param recycle  是否回收
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
        if (rotation == 0) {
            return source;
        }
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
        if (recycle) {
            recycleSilently(source);
        }
        return bitmap;
    }

    public static byte[] compressToBytes(Bitmap bitmap) {
        return compressToBytes(bitmap, DEFAULT_JPEG_QUALITY);
    }

    public static byte[] compressToBytes(Bitmap bitmap, int quality) {
        return compressToBytes(bitmap, CompressFormat.JPEG, quality);
    }

    public static byte[] compressToBytes(Bitmap bitmap, CompressFormat format, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // size 65536
        bitmap.compress(format, quality, baos);
        return baos.toByteArray();
    }

    public static void compressToFile(Bitmap bitmap, String path) {
        compressToFile(bitmap, DEFAULT_JPEG_QUALITY, path);
    }

    public static void compressToFile(Bitmap bitmap, int quality, String path) {
        compressToFile(bitmap, Bitmap.CompressFormat.JPEG, quality, path);
    }

    public static void compressToFile(Bitmap bitmap, CompressFormat format, int quality, String path) {
        File f = new File(path);
        if (f.exists()) {
            return;
        }
        FileOutputStream fos = null;
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            bitmap.compress(format, quality, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fos);
        }
    }

    public static boolean isSupportedByRegionDecoder(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        mimeType = mimeType.toLowerCase();
        return mimeType.startsWith("image/") && (!mimeType.equals("image/gif") && !mimeType.endsWith("bmp"));
    }


    /**
     * 是否支持旋转
     *
     * @param mimeType
     * @return
     */
    public static boolean isRotationSupported(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        mimeType = mimeType.toLowerCase();
        return mimeType.equals("image/jpeg");
    }


    /**
     * 回收图片资源
     *
     * @param bitmap
     */
    public static void recycleSilently(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                bitmap.recycle();
            } catch (Throwable t) {
                Log.w(TAG, "unable recycle bitmap", t);
            }
        }
    }


    /**
     * 将drawable 转换为 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        Bitmap bitmap =
                Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Rect oldBounds = (Rect) ReflectionUtils.getFieldValue(drawable, "mBounds");
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        if (oldBounds != null) {
            drawable.setBounds(oldBounds);
        }
        return bitmap;
    }

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得带倒影的图片方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap makeReflectionBitmap(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader =
                new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap,
                        0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * get reflection bitmap of the original bitmap.
     *
     * @param srcBitmap
     * @return
     */
    public static Bitmap makeReflectionBitmap2(Bitmap srcBitmap) {
        int bmpWidth = srcBitmap.getWidth();
        int bmpHeight = srcBitmap.getHeight();
        int[] pixels = new int[bmpWidth * bmpHeight * 4];
        srcBitmap.getPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);

        // get reversed bitmap
        Bitmap reverseBitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < bmpHeight; y++) {
            reverseBitmap.setPixels(pixels, y * bmpWidth, bmpWidth, 0, bmpHeight - y - 1, bmpWidth, 1);
        }

        // get reflection bitmap based on the reversed one
        reverseBitmap.getPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);
        Bitmap reflectionBitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        // int alpha = 0xff000000;
        // int d = 0xff / bmpHeight;
        Log.d(TAG, "bmpHeight =" + bmpHeight);
        float alpha = 255.0f;
        float d = alpha / bmpHeight;
        Log.d(TAG, "d =" + d);
        int rAlpha = 0xff000000;
        for (int y = 0; y < bmpHeight; y++) {
            for (int x = 0; x < bmpWidth; x++) {
                int index = y * bmpWidth + x;
                int r = (pixels[index] >> 16) & 0xff;
                int g = (pixels[index] >> 8) & 0xff;
                int b = pixels[index] & 0xff;
                Log.d(TAG, "rAlpha =" + (rAlpha >>> 24));
                pixels[index] = rAlpha | (r << 16) | (g << 8) | b;
                reflectionBitmap.setPixel(x, y, pixels[index]);
            }
            int a = (int) (alpha - y * d - 3);
            if (a < 0) {
                a = 0;
            }
            rAlpha = (a << 24);
        }
        return reflectionBitmap;
    }

    public static byte[] drawableToBytes(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        if (bitmap != null) {
            return compressToBytes(bitmap);
        }
        return null;
    }

    /**
     * 复制一个drawable
     *
     * @param drawable
     * @return
     */
    public static Drawable cloneDrawable(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    /**
     * Returns a bitmap showing a screenshot of the view passed in.
     *
     * @param v
     * @return
     */
    public static Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * 从view 中获取 bitmap
     *
     * @param v
     * @return
     */
    public static Bitmap fromView(View v) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        return bitmap;
    }

    /**
     * 把二进制数组转成Bitmap对象
     *
     * @param imageBytes
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] imageBytes) {
        Bitmap bitmap = null;
        if (null != imageBytes) {
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return bitmap;
    }

    /**
     * 创建视频缩略图
     *
     * @param filePath
     * @return
     */
    public static Bitmap createVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();
            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);
            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) {
                        return bitmap;
                    }
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (Exception e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return null;
    }

    /**
     * 创建缩略图
     *
     * @param filePath
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(filePath)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, maxWidth * maxHeight);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }
        return bitmap;
    }

    /**
     * 创建缩略图
     *
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap createImageThumbnail(byte[] byteArray, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        if (null != byteArray) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, maxWidth * maxHeight);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
        }
        return bitmap;
    }


    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 获取我们需要的整理过旋转角度的Uri
     *
     * @param activity 上下文环境
     * @param path     路径
     * @return 正常的Uri
     */
    public static Uri getRotatedUri(Activity activity, String path) {
        int degree = getBitmapDegree(path);
        if (degree != 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap newBitmap = rotateBitmapByDegree(bitmap, degree);
            return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), newBitmap, null, null));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param path   需要旋转的图片的路径
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(String path, int degree) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return rotateBitmapByDegree(bitmap, degree);
    }

    /**
     * 获取bitmap 中config 信息
     *
     * @param bitmap
     * @return
     */
    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    /**
     * 解码文件, 传入指定的宽高, 但输出的宽高不一致
     * <p>
     * 原图 3024x4032
     * 指定 1080x1920
     * 输出 1512x2016
     *
     * @param path
     * @param width
     * @param height
     * @param config
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String path, int width, int height, Bitmap.Config config) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int sourceWidth = opts.outWidth;
            int sourceHeight = opts.outHeight;
            int sampleSizeX = (int) (sourceWidth / (float) width);
            int sampleSizeY = (int) (sourceHeight / (float) height);
            int sampleSize = Math.max(sampleSizeX, sampleSizeY);

            opts.inSampleSize = sampleSize;
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inPreferredConfig = config;

            return BitmapFactory.decodeFile(path, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码图片, 当原图与指定的宽高比例不一致时, 通过down参数来调节输出的大小
     *
     * @param path
     * @param width  指定输出的bitmap宽
     * @param height 指定输出的bitmap高
     * @param config
     * @param down   true 表示实际输出的w*h<=指定的w*h, false 输出的w*h >= 指定的w*h
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String path, int width, int height, Bitmap.Config config, boolean down) {
        Bitmap bitmap = decodeBitmap(path, width, height, config);
        // 此时解码的图片与需要输出的图片大小是不一致的
        // 再次调整图片大小
        float scale = calcScale(bitmap.getWidth(), bitmap.getHeight(), width, height, down);
        return resizeBitmapByScale(bitmap, scale, true);
    }

    /**
     * 解码图片, 以较大输出
     *
     * @param path
     * @param width
     * @param height
     * @param config
     * @return
     */
    private static Bitmap decodeBitmap(String path, int width, int height, Bitmap.Config config) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 先取缩放到较大的值, 保证第一次decode图片较大, 再进行缩小, 保证图片质量
        float scale = calcScale(opts.outWidth, opts.outHeight, width, height, false);
        opts.inSampleSize = computeSampleSizeLarger(scale);
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        opts.inPreferredConfig = config;
        return BitmapFactory.decodeFile(path, opts);
    }

    /**
     * 计算scale值
     *
     * @param sourceWidth
     * @param sourceHeight
     * @param width
     * @param height
     * @param down         true 返回的值小, false返回的值大
     * @return
     */
    private static float calcScale(int sourceWidth, int sourceHeight, int width, int height, boolean down) {
        float scaleX = ((float) width / sourceWidth);
        float scaleY = ((float) height / sourceHeight);
        float scale;
        if (down) {
            scale = Math.min(scaleX, scaleY);
        } else {
            scale = Math.max(scaleX, scaleY);
        }
        return scale;
    }
}
