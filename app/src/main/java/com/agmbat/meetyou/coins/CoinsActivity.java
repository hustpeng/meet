package com.agmbat.meetyou.coins;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.widget.StarsView;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的缘币
 */
public class CoinsActivity extends Activity {

    @BindView(R.id.my_coins)
    TextView mCoinsView;

    @BindView(R.id.grade_view)
    StarsView mStarsView;

    private CoinsLoader mPageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_coins);
        ButterKnife.bind(this);
        mPageLoader = new CoinsLoader(this);
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

    private class CoinsLoader extends PageDataLoader<CoinsRecords> {

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
            mStarsView.setStarsCount(apiResult.mGrade);
            return new CoinsListAdapter(context, data.getDataList());
        }

        @Override
        protected void onLoadingError(PageData data) {
        }

    }
}
