package com.agmbat.meetyou.discovery;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;

/**
 * 找玩伴
 */
public class BirthplaceLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_birthplace);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        return requestBirthplace(UserManager.getInstance().getLoginUser(), page);
    }


    /**
     * 找老乡
     *
     * @param current   当前用户信息
     * @param pageIndex
     * @return
     */
    public static DiscoveryApiResult requestBirthplace(LoginUser current, int pageIndex) {
        if (current == null) {
            return null;
        }
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String birthplace = current.getBirthplace();
        return DiscoveryApi.getBirthplace(phone, token, birthplace, pageIndex);
    }
}
