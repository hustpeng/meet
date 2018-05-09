package com.agmbat.meetyou.discovery.search;

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
import com.agmbat.meetyou.discovery.filter.FilterView;
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

    /**
     * 过虑器view, 只用于综合搜索会员
     */
    @BindView(R.id.filter_view)
    FilterView mFilterView;

    /**
     * 对应发现页各功能搜索
     */
    private DiscoveryLoader mLoader;

    /**
     * 分页数据加载器
     */
    private PageDataLoader mPageDataLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_discovery_users);
        ButterKnife.bind(this);
        mLoader = DiscoveryHelper.getLoader(getIntent());
        mTitleView.setText(mLoader.getName());
        mPageDataLoader = new DiscoveryPageLoader(this);
        mPageDataLoader.setupViews(findViewById(android.R.id.content));
        mPageDataLoader.loadData();
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

    /**
     * 点击过虑器中搜索
     */
    @OnClick(R.id.btn_confirm)
    void onClickConfirm() {
        mFilterView.setVisibility(View.INVISIBLE);
        mPageDataLoader.loadData();
    }

    /**
     * 点击标题栏中过虑器
     */
    @OnClick(R.id.btn_filter)
    void onClickFilter() {
        if (mFilterView.getVisibility() == View.VISIBLE) {
            mFilterView.setVisibility(View.INVISIBLE);
        } else {
            mFilterView.setVisibility(View.VISIBLE);
        }
    }

    public class DiscoveryPageLoader extends PageDataLoader<ContactInfo> {

        public DiscoveryPageLoader(Context context) {
            super(context);
        }

        @Override
        public void setupViews(View view) {
            super.setupViews(view);
            mLoader.setupViews(view);
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
