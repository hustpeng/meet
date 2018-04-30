package com.agmbat.meetyou.discovery;

public interface DiscoveryLoader {
    public String getName();

    public DiscoveryApiResult load(int page);
}
