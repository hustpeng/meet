package com.agmbat.meetyou.discovery.meeting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.browser.WebViewActivity;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 聚会活动
 */
public class MeetingActivity extends Activity {

    private MeetingLoader mPageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_discovery_meeting);
        ButterKnife.bind(this);
        mPageLoader = new MeetingLoader(this);
        mPageLoader.setupViews(findViewById(android.R.id.content));
        mPageLoader.loadData();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    private class MeetingLoader extends PageDataLoader<MeetingItem> {

        public MeetingLoader(Context context) {
            super(context);
        }

        @Override
        public MeetingApiResult onLoadData(int page) {
            return MeetingManager.request(page);
        }

        @Override
        protected ArrayAdapter<MeetingItem> createListAdapter(Context context, PageData<MeetingItem> data) {
            return new MeetingListAdapter(context, data.getDataList());
        }

        @Override
        protected void onLoadingError(PageData data) {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            super.onItemClick(parent, view, position, id);
            MeetingItem item = (MeetingItem) parent.getItemAtPosition(position);
            String phone = XMPPManager.getInstance().getConnectionUserName();
            String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
            String url = item.url + "&uid=" + phone + "&ticket=" + token;

            String title = getString(R.string.discovery_meeting);
            WebViewActivity.openBrowser(MeetingActivity.this, url, title);
        }
    }


}
