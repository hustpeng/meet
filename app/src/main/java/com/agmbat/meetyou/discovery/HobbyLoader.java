package com.agmbat.meetyou.discovery;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;

/**
 * 找玩伴
 */
public class HobbyLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_hobby);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        return DiscoveryManager.requestHobby(UserManager.getInstance().getLoginUser(), page);
    }
}
