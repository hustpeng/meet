package com.agmbat.imsdk.chat.body;

import java.io.File;


/**
 * Url body
 */
public class FileBody extends Body {

    private String mUrl;
    private String mFileName;
    private File mFile;

    public FileBody(String url, String name) {
        mUrl = url;
        mFileName = name;
    }

    public FileBody(String url, String name, File file) {
        mUrl = url;
        mFileName = name;
        mFile = file;
    }

    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<wrap>");
        builder.append("<type>").append(getBodyType()).append("</type>");
        builder.append("<url>").append(mUrl).append("</url>");
        builder.append("<fileName>").append(mUrl).append("</fileName>");
        builder.append("</wrap>");
        return builder.toString();
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.FILE;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getUrl() {
        return mUrl;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }
}
