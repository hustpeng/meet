package com.agmbat.meetyou.discovery.search;

import android.view.View;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;

/**
 * 找老乡
 */
public class BirthplaceLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_birthplace);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        return requestBirthplace(user, page);
    }

    @Override
    public void setupViews(View view) {
    }

    /**
     * 找老乡
     *
     * @param current   当前用户信息
     * @param pageIndex
     * @return
     */
    private static DiscoveryApiResult requestBirthplace(LoginUser current, int pageIndex) {
        if (current == null) {
            return null;
        }
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String birthplace = current.getBirthplace();
        String center = "30.5,111.2";
        return DiscoveryApi.getBirthplace(phone, token, center, birthplace, pageIndex);
    }
}
