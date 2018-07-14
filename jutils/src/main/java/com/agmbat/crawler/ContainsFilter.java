package com.agmbat.crawler;

public class ContainsFilter implements Filter {

    private String mText;

    public ContainsFilter(String text) {
        mText = text;
    }

    @Override
    public boolean accept(String url) {
        return url.contains(mText);
    }
}

