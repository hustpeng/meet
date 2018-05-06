package com.agmbat.meetyou.discovery;

import android.view.View;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
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
        return requestHobby(UserManager.getInstance().getLoginUser(), page);
    }

    @Override
    public void setupViews(View view) {

    }

    /**
     * 找玩伴
     *
     * @param current   当前用户信息
     * @param pageIndex
     * @return
     */
    private static DiscoveryApiResult requestHobby(LoginUser current, int pageIndex) {
        if (current == null) {
            return null;
        }
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String hobby = current.getHobby();
        String center = "30.5,111.2";
        return DiscoveryApi.getHobby(phone, token, center, hobby, pageIndex);
    }

}
