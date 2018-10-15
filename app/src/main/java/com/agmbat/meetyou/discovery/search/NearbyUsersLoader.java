package com.agmbat.meetyou.discovery.search;

import android.view.View;
import android.widget.ImageView;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;

/**
 * 附近的人,联系人列表
 */
public class NearbyUsersLoader implements DiscoveryLoader {

    private int mGender = -1;

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_nearby_users);
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    @Override
    public DiscoveryApiResult load(int page) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String center = "30.5,111.2";
        return DiscoveryApi.getNearbyUsers(phone, token, center, mGender, page);
    }

    @Override
    public void setupViews(final View view) {
        final ImageView filterView = (ImageView) view.findViewById(R.id.btn_filter);
        filterView.setVisibility(View.VISIBLE);
    }
}
