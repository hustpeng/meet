package com.agmbat.meetyou.discovery.search;

import android.view.View;

public interface DiscoveryLoader {

    /**
     * 获取标题
     *
     * @return
     */
    public String getName();

    /**
     * 加载会员列表
     *
     * @param page
     * @return
     */
    public DiscoveryApiResult load(int page);

    /**
     * 配置控件
     *
     * @param view
     */
    void setupViews(View view);
}
