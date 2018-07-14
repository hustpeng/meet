package com.agmbat.baidumap.position;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agmbat.baidumap.R;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;

/**
 * 位置搜索页面
 */
public class SearchPositionActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String KEY_POSITION = "position";

    /**
     * 附近地点列表
     */
    private ListView mListView;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    /**
     * 输入框
     */
    private EditText mEditText;

    /**
     * 列表适配器
     */
    private SearchPositionAdapter mAdapter;

    /**
     * 建议查询
     */
    private SuggestionSearch mSuggestionSearch;

    /**
     * 获取搜索的内容
     */
    private OnGetSuggestionResultListener mSuggestionResultListener = new OnGetSuggestionResultListener() {

        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            mProgressBar.setVisibility(View.GONE);
            if (res == null || res.getAllSuggestions() == null) {
                Toast.makeText(SearchPositionActivity.this, "没找到结果", Toast.LENGTH_LONG).show();
                return;
            }
            //获取在线建议检索结果
            mAdapter.clear();
            for (SuggestionResult.SuggestionInfo suggestionInfos : res.getAllSuggestions()) {
                mAdapter.add(suggestionInfos);
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_search_position);
        setupViews();
    }

    /**
     * 初始化Ui
     */
    private void setupViews() {
        mListView = (ListView) findViewById(R.id.lv_locator_search_position);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.title_btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEditText.getText().toString())) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    // 根据输入框的内容，进行搜索
                    String text = mEditText.getText().toString();
                    String city = "北京";
                    // 北京, city 必须指定
                    SuggestionSearchOption option = new SuggestionSearchOption().keyword(text).city(city);
                    mSuggestionSearch.requestSuggestion(option);
                } else {
                    Toast.makeText(SearchPositionActivity.this, "请输入地点", Toast.LENGTH_LONG).show();
                }
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.pb_location_search_load_bar);
        mEditText = (EditText) findViewById(R.id.et_search);
        // 建议查询
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(mSuggestionResultListener);

        // 列表初始化
        mAdapter = new SearchPositionAdapter(this, new ArrayList<SuggestionResult.SuggestionInfo>());
        mListView.setAdapter(mAdapter);

        // 注册监听
        mListView.setOnItemClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestionSearch.destroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.setSelectSearchItemIndex(position);
        mAdapter.notifyDataSetChanged();
        Intent intent = new Intent();
        // 设置坐标
        intent.putExtra(KEY_POSITION, mAdapter.getItem(position).pt);
        setResult(RESULT_OK, intent);
        finish();
    }

}