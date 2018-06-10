package com.agmbat.meetyou.discovery.search;

import android.view.View;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.meetyou.R;
import com.agmbat.text.TagText;

import java.util.List;

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
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        return requestHobby(user, page);
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
        String hobbyParam = "";
        List<String> list = TagText.parseTagList(hobby);
        if (list.size() > 0) {
            hobbyParam = list.get(0);
        }
        String center = "30.5,111.2";
        return DiscoveryApi.getHobby(phone, token, center, hobbyParam, pageIndex);
    }

}
