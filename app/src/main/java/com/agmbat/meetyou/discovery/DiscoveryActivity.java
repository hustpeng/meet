package com.agmbat.meetyou.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.search.UserInfoActivity;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 附近的人,联系人列表
 */
public class DiscoveryActivity extends Activity {

    @BindView(R.id.title)
    TextView mTitleView;

    private DiscoveryLoader mLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);
        mLoader = DiscoveryHelper.getLoader(getIntent());
        mTitleView.setText(mLoader.getName());
        DiscoveryPageLoader pageLoader = new DiscoveryPageLoader(this);
        pageLoader.setupViews(findViewById(android.R.id.content));
        pageLoader.loadData();
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

    public class DiscoveryPageLoader extends PageDataLoader<ContactInfo> {

        public DiscoveryPageLoader(Context context) {
            super(context);
        }

        @Override
        public DiscoveryApiResult onLoadData(int page) {
            return mLoader.load(page);
        }

        @Override
        protected ArrayAdapter<ContactInfo> createListAdapter(Context context, PageData<ContactInfo> data) {
            return new ContactInfoAdapter(context, data.getDataList());
        }

        @Override
        protected void onLoadingError(PageData data) {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            super.onItemClick(parent, view, position, id);
            ContactInfo contactInfo = (ContactInfo) parent.getItemAtPosition(position);
            UserInfoActivity.viewUserInfo(DiscoveryActivity.this, contactInfo);
        }
    }
}
