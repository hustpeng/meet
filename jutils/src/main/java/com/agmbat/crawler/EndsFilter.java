package com.agmbat.crawler;

public class EndsFilter implements Filter {

    private String mText;

    public EndsFilter(String text) {
        mText = text;
    }

    @Override
    public boolean accept(String url) {
        return url.endsWith(mText);
    }
}

