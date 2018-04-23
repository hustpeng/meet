package com.agmbat.meetyou.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.meetyou.R;

/**
 * 搜索用户界面
 */
public class SearchUserActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
    }
}
