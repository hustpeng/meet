package com.agmbat.meetyou.discovery;

import com.agmbat.android.AppResources;
import com.agmbat.meetyou.R;

/**
 * 附近的人,联系人列表
 */
public class NearbyUsersLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.nearby_users);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        return DiscoveryManager.request(page);
    }
}
