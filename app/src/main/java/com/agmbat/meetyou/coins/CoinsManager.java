package com.agmbat.meetyou.coins;

import com.agmbat.imsdk.asmack.XMPPManager;

public class CoinsManager {

    /**
     * 请求数据
     *
     * @param pageIndex
     * @return
     */
    public static CoinsApiResult request(int pageIndex) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        return CoinsApi.getCoins(phone, token, pageIndex);
    }


}
