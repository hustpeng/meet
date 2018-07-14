package com.agmbat.browser;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public class BrowserClient {

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    public void onPageStarted(WebView view, String url) {
    }

    public void onPageFinished(WebView view, String url) {
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return null;
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return null;
    }
}
