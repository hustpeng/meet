package com.agmbat.meetyou.discovery;

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

}
