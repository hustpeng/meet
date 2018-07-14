package com.nostra13.universalimageloader.core.download;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.IOException;
import java.io.InputStream;


public class DrawableLoader implements SchemeImageDownloader {

    private static final String SCHEME = "drawable";

    private Context mContext;

    public DrawableLoader(Context context) {
        mContext = context;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in drawable resources of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     */
    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        String drawableIdString = Scheme.crop(imageUri, SCHEME);
        int drawableId = Integer.parseInt(drawableIdString);
        return mContext.getResources().openRawResource(drawableId);
    }

}
