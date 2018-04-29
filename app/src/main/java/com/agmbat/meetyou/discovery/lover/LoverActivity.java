package com.agmbat.meetyou.discovery.lover;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.nearbyuser.ContactInfoAdapter;
import com.agmbat.meetyou.discovery.nearbyuser.NearbyUsersApiResult;
import com.agmbat.meetyou.discovery.nearbyuser.NearbyUsersManager;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 附近的人,联系人列表
 */
public class LoverActivity extends Activity {

    @BindView(R.id.title)
    TextView mTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);
        mTitleView.setText(R.string.lover);
        NearbyUsersLoader mPageLoader = new NearbyUsersLoader(this);
        mPageLoader.setupViews(findViewById(android.R.id.content));
        mPageLoader.loadData();
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

    public class NearbyUsersLoader extends PageDataLoader<ContactInfo> {

        public NearbyUsersLoader(Context context) {
            super(context);
        }

        @Override
        public NearbyUsersApiResult onLoadData(int page) {
            return NearbyUsersManager.requestLover(UserManager.getInstance().getLoginUser(), page);
        }

        @Override
        protected ArrayAdapter<ContactInfo> createListAdapter(Context context, PageData<ContactInfo> data) {
            return new ContactInfoAdapter(context, data.getDataList());
        }

        @Override
        protected void onLoadingError(PageData data) {
        }

    }
}
