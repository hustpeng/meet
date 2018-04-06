package com.agmbat.meetyou.credits;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;
import com.agmbat.pulltorefresh.PullToRefreshBase;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;

import java.util.ArrayList;

/**
 * 我的缘币
 */
public class CoinsActivity extends Activity implements PullToRefreshBase.OnRefreshListener<ListView> {

    private CoinsManager mCoinsManager;
    private PullToRefreshListView mListView;
    private CoinsListAdapter mCoinsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_coins);
        mCoinsManager = new CoinsManager();
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView = (PullToRefreshListView) findViewById(R.id.list);
        mCoinsListAdapter = new CoinsListAdapter(this, new ArrayList<CoinsRecords>());
        mListView.setAdapter(mCoinsListAdapter);
        mListView.setOnRefreshListener(this);
        loadFirst();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onPullStartToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadFirst();
    }

    @Override
    public void onPullEndToRefresh(PullToRefreshBase<ListView> refreshView) {
        mCoinsManager.loadMore(1, new CoinsManager.OnLoadRecordsListener() {
            @Override
            public void onLoadRecords(CoinsApiResult result) {
                mCoinsListAdapter.addAll(result.mData);
                mListView.onRefreshComplete();
            }
        });
    }

    private void loadFirst() {
        mCoinsManager.loadFirst(new CoinsManager.OnLoadRecordsListener() {
            @Override
            public void onLoadRecords(CoinsApiResult result) {
                mCoinsListAdapter.clear();
                mCoinsListAdapter.addAll(result.mData);
                mListView.onRefreshComplete();
            }

        });
    }
}
