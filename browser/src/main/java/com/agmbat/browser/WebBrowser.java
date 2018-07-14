package com.agmbat.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.KeyboardUtils;

/**
 * 处理WebView的默认配置
 */
public class WebBrowser {

    private static final String TAG = WebBrowser.class.getSimpleName();

    private final Context mContext;
    private final View mContent;

    /**
     * 顶部输入控件
     */
    private View mInputLayout;

    /**
     * WebView
     */
    private WebView mWebView;

    /**
     * url输入框
     */
    private EditText mUrlEditText;

    /**
     * 返回Button
     */
    private View mGoBackButton;

    /**
     * 前进键
     */
    private View mGoForwardButton;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    private BrowserClient mBrowserClient;

    /**
     * 是否隐藏默认Button
     */
    private boolean mHideGoButton;

    private OnKeyListener mOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP && (KeyEvent.KEYCODE_ENTER == keyCode)) {
                loadUrl();
                KeyboardUtils.hideInputMethod(mUrlEditText);
                return true;
            }
            return false;
        }
    };

    public WebBrowser(Context context, View content) {
        mContext = context;
        mContent = content;
        setupViews();
    }

    public void setBrowserClient(BrowserClient client) {
        mBrowserClient = client;
    }

    public View getInputLayout() {
        return mInputLayout;
    }

    /**
     * 隐藏输入框
     */
    public void hideInputLayout() {
        mInputLayout.setVisibility(View.GONE);
    }

    public void setHideGoButton(boolean hideGoButton) {
        mHideGoButton = hideGoButton;
        if (mHideGoButton) {
            mGoBackButton.setVisibility(View.GONE);
            mGoForwardButton.setVisibility(View.GONE);
        } else {
            mGoBackButton.setVisibility(View.VISIBLE);
            mGoForwardButton.setVisibility(View.VISIBLE);
        }
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void setDownloadListener(DownloadListener listener) {
        mWebView.setDownloadListener(listener);
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String name) {
        mWebView.addJavascriptInterface(object, name);
    }

    /**
     * 加载编辑框中的url
     */
    public void loadUrl() {
        String url = mUrlEditText.getText().toString().trim();
        loadUrl(url);
    }

    /**
     * 加载url
     *
     * @param urlIn
     */
    public void loadUrl(String urlIn) {
        String url = UrlUtils.fixWebUrl(urlIn);
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("market://")) {
                boolean opened = AppUtils.openBrowser(mContext, url);
                if (!opened) {
                    mWebView.loadUrl(url);
                }
            } else {
                mWebView.loadUrl(url);
            }
        }
    }

    /**
     * Loads the given data into this WebView using a 'data' scheme URL.
     *
     * @param data
     * @param mimeType
     * @param encoding
     */
    public void loadData(String data, String mimeType, String encoding) {
        mWebView.loadData(data, mimeType, encoding);
    }

    /**
     * Loads the given data into this WebView, using baseUrl as the base URL for the content. The base URL is used both
     * to resolve relative URLs and when applying JavaScript's same origin policy. The historyUrl is used for the
     * history entry. *
     *
     * @param baseUrl
     * @param data
     * @param mimeType
     * @param encoding
     * @param historyUrl
     */
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    private void setupViews() {
        mInputLayout = AppResources.findViewByIdName(mContent, "input_layout");

        mUrlEditText = (EditText) AppResources.findViewByIdName(mContent, "url_edit");
        mUrlEditText.setOnKeyListener(mOnKeyListener);
        mGoBackButton = AppResources.findViewByIdName(mContent, "browser_go_back");
        mGoBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mGoBackButton.setVisibility(View.GONE);
        mGoForwardButton = AppResources.findViewByIdName(mContent, "browser_go_forward");
        mGoForwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWebView.goForward();
            }
        });
        mGoForwardButton.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) AppResources.findViewByIdName(mContent, "browser_progress");
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        setupWebView();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        mWebView = (WebView) AppResources.findViewByIdName(mContent, "webview");
        WebSettings settings = mWebView.getSettings();
        // settings.setUserAgentString(USER_AGENT);

        // 设置支持缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        // 不显示缩放button
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        // 设置WebView属性，能够执行Javascript脚本
        settings.setJavaScriptEnabled(true);
        // settings.setPluginsEnabled(true);

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLightTouchEnabled(true);

        settings.setSaveFormData(true);
        settings.setSavePassword(true);

        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(mContext.getCacheDir().getAbsolutePath());
        settings.setDefaultTextEncodingName("utf-8");

        // 5.0 及以上显示图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 设置Web视图
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                return false;
            }
        });
    }

    /**
     * 返回上一级页面
     *
     * @return
     */
    public boolean goBack() {
        mWebView.requestFocus();
        if (mWebView.canGoBack()) {
            // 返回WebView的上一页面
            mWebView.goBack();
            return true;
        }
        return false;
    }

    private class MyWebViewClient extends WebViewClient {

        /**
         * 为WebView自定义错误显示界面:
         */
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading url= " + url);
            if (mBrowserClient != null) {
                boolean handled = mBrowserClient.shouldOverrideUrlLoading(view, url);
                if (handled) {
                    return true;
                }
            }
            loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted url= " + url);
            if (mBrowserClient != null) {
                mBrowserClient.onPageStarted(view, url);
            }
            mUrlEditText.setText(url);
            mProgressBar.setProgress(0);
            mWebView.requestFocus();
            // mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished url= " + url);
            // mProgressBar.setVisibility(View.INVISIBLE);
            if (mBrowserClient != null) {
                mBrowserClient.onPageFinished(view, url);
            }
            if (!mHideGoButton) {
                updateBackForwardButtonVisibility(view);
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.d(TAG, "onLoadResource url= " + url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.d(TAG, "shouldInterceptRequest url= " + url);
            if (mBrowserClient != null) {
                WebResourceResponse response = mBrowserClient.shouldInterceptRequest(view, url);
                if (response != null) {
                    return response;
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.d(TAG, "shouldInterceptRequest request");
            if (mBrowserClient != null) {
                WebResourceResponse response = mBrowserClient.shouldInterceptRequest(view, request);
                if (response != null) {
                    return response;
                }
            }
            return super.shouldInterceptRequest(view, request);
        }

        /**
         * 更新是否显示goBack button 和 goForward button
         *
         * @param view
         */
        private void updateBackForwardButtonVisibility(WebView view) {
            if (view.canGoBack()) {
                mGoBackButton.setVisibility(View.VISIBLE);
            } else {
                mGoBackButton.setVisibility(View.GONE);
            }
            if (view.canGoForward()) {
                mGoForwardButton.setVisibility(View.VISIBLE);
            } else {
                mGoForwardButton.setVisibility(View.GONE);
            }
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean onJsTimeout() {
            Log.d(TAG, "onJsTimeout");
            return super.onJsTimeout();
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  JsPromptResult result) {
            Log.d(TAG, "onJsPrompt");
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, "onJsBeforeUnload");
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, "onJsAlert");
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, "onJsConfirm");
            return super.onJsConfirm(view, url, message, result);
        }

    }

}
