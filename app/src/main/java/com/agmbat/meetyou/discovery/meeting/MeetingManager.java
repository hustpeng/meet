package com.agmbat.meetyou.discovery.meeting;

import com.agmbat.imsdk.asmack.XMPPManager;

public class MeetingManager {

    /**
     * 请求数据
     *
     * @param pageIndex
     * @return
     */
    public static MeetingApiResult request(int pageIndex) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String city = "";
        return MeetingApi.getMeetingList(phone, token, city, pageIndex);
    }

}
