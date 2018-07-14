package com.nostra13.universalimageloader.core.download;

public class HttpsDownloader extends HttpDownloader {

    @Override
    public String getScheme() {
        return "https";
    }

}
