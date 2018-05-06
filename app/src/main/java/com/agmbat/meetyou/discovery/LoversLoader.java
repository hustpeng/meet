package com.agmbat.meetyou.discovery;


import android.view.View;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.GenderHelper;

public class LoversLoader implements DiscoveryLoader {

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_lover);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        return requestLover(UserManager.getInstance().getLoginUser(), page);
    }

    @Override
    public void setupViews(View view) {

    }

    /**
     * 找恋人
     *
     * @param current   当前用户信息
     * @param pageIndex
     * @return
     */
    private static DiscoveryApiResult requestLover(LoginUser current, int pageIndex) {
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
        String center = "30.5,111.2";
        return DiscoveryApi.getLover(phone, token, center, searchGender, searchAge, pageIndex);
    }
}
