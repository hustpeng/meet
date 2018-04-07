package com.agmbat.meetyou.coins;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;

/**
 * 我的缘币
 */
public class CoinsActivity extends Activity {

    private TextView mCoinsView;
    private CoinsLoader mPageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_coins);
        mCoinsView = (TextView) findViewById(R.id.my_coins);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPageLoader = new CoinsLoader(this);
        mPageLoader.setupViews(findViewById(android.R.id.content));
        mPageLoader.loadData();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public class CoinsLoader extends PageDataLoader<CoinsRecords> {

        public CoinsLoader(Context context) {
            super(context);
        }

        @Override
        public CoinsApiResult onLoadData(int page) {
            return CoinsManager.request(page);
        }

        @Override
        protected ArrayAdapter<CoinsRecords> createListAdapter(Context context, PageData<CoinsRecords> data) {
            CoinsApiResult apiResult = (CoinsApiResult) data;
            mCoinsView.setVisibility(View.VISIBLE);
            mCoinsView.setText(String.valueOf(apiResult.mBalance));
            return new CoinsListAdapter(context, data.getDataList());
        }

        @Override
        protected void onLoadingError(PageData data) {
        }

    }
}
