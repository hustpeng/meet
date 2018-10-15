package com.agmbat.meetyou.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.agmbat.meetyou.R;
import com.agmbat.menu.MenuInfo;

import java.util.ArrayList;
import java.util.List;

public class SimpleAppsGridView extends RelativeLayout {

    private GridView mGridView;

    public SimpleAppsGridView(Context context) {
        this(context, null);
    }

    public SimpleAppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(getContext(), R.layout.view_apps, this);
        mGridView = (GridView) findViewById(R.id.gv_apps);
        List<MenuInfo> beans = new ArrayList<>();
        AppsAdapter adapter = new AppsAdapter(getContext(), beans);
        mGridView.setAdapter(adapter);
    }

    public void addMenuList(List<MenuInfo> list) {
        AppsAdapter adapter = (AppsAdapter) mGridView.getAdapter();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
    }
}
