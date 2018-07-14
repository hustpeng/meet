package com.nostra13.universalimageloader.core.download;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Package Loader
 */
public class PackageIconLoader implements SchemeImageDownloader {

    private static final String PACKAGE_URI_PREFIX = "package";

    private Context mContext;

    public PackageIconLoader(Context context) {
        mContext = context;
    }

    @Override
    public String getScheme() {
        return PACKAGE_URI_PREFIX;
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in package icon of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions
     *                 .Builder#extraForDownloader
     *                 (Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     */
    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        String packageName = Scheme.crop(imageUri, getScheme());
        PackageManager pm = mContext.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable appIcon = applicationInfo.loadIcon(pm);
            if (appIcon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) appIcon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] array = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(array);
                return bais;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}