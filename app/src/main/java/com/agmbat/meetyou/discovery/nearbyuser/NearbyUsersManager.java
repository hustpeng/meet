package com.agmbat.meetyou.discovery.nearbyuser;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.data.GenderHelper;

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

    /**
     * 请求数据
     *
     * @param pageIndex
     * @return
     */

    /**
     * 找恋人
     *
     * @param current   当前用户信息
     * @param pageIndex
     * @return
     */
    public static NearbyUsersApiResult requestLover(LoginUser current, int pageIndex) {
        if (current == null) {
            return null;
        }
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        int userGender = current.getGender();
        int searchGender;
        String searchAge = null;
        int userAge = current.getAge();
        // 按需求文档，若用户本身为男性，则要搜索女性，即传gender=0，反之则传gender=1
        // 按需求文档要求，若用户本身为男，年龄25岁，则搜索比他小的女性，即传age=18,25;
        // 反之若用户本身为女，年龄25岁，则搜索比她大的男性，即传age=25,80
        if (userGender == GenderHelper.GENDER_MALE) {
            searchAge = "18," + userAge;
            searchGender = GenderHelper.GENDER_FEMALE;
        } else {
            searchAge = userAge + ",80";
            searchGender = GenderHelper.GENDER_MALE;
        }
        return NearbyUserApi.getLover(phone, token, searchGender, searchAge, pageIndex);
    }
}
