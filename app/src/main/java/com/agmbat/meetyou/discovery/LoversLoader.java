package com.agmbat.meetyou.discovery;


import com.agmbat.android.AppResources;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;

public class LoversLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.lover);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        return DiscoveryManager.requestLover(UserManager.getInstance().getLoginUser(), page);
    }
}
