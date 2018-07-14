package com.nostra13.universalimageloader.core.download;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.IOException;
import java.io.InputStream;


public class AssetsLoader implements SchemeImageDownloader {

    private static final String SCHEME = "assets";

    private Context mContext;

    public AssetsLoader(Context context) {
        mContext = context;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in assets of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs file reading
     */
    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.crop(imageUri, SCHEME);
        return mContext.getAssets().open(filePath);
    }


}
