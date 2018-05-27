package com.agmbat.meetyou.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.browser.WebBrowser;
import com.agmbat.meetyou.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 内置浏览器
 */
public class WebViewActivity extends Activity {


    @BindView(R.id.title)
    TextView mTitleView;

    @BindView(R.id.title_layout)
    View mTitleLayout;

    private WebBrowser mBrowser;

    /**
     * 通过浏览器打开url
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        openBrowser(context, url, null);
    }

    /**
     * 通过浏览器打开url
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url, String title) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.browser_activity_webview);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            mTitleLayout.setVisibility(View.GONE);
        } else {
            mTitleView.setText(title);
        }
        Uri uri = intent.getData();
        String url = uri.toString(); // "http://www.baidu.com"
        mBrowser = new WebBrowser(this, findViewById(android.R.id.content));
        mBrowser.hideInputLayout();
        mBrowser.setHideGoButton(true);
        mBrowser.loadUrl(url);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }
}

