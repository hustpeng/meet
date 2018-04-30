package com.agmbat.meetyou.discovery;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;

/**
 * 附近的人,联系人列表
 */
public class NearbyUsersLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_nearby_users);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String center = "30.5,111.2";
        int gender = -1;
        return DiscoveryApi.getNearbyUsers(phone, token, center, gender, page);
    }
}
