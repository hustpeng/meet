package com.agmbat.meetyou.nearbyuser;

import com.agmbat.imsdk.asmack.XMPPManager;

public class NearbyUsersManager {

    /**
     * 请求数据
     *
     * @param pageIndex
     * @return
     */
    public static NearbyUsersApiResult request(int pageIndex) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        return NearbyUserApi.getNearbyUsers(phone, token, pageIndex);
    }

}
