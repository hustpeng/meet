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

    private TagSelectedView mTagSelectedView;

    @Override
    public String getName() {
        return AppResources.getString(R.string.discovery_hobby);
    }

    @Override
    public DiscoveryApiResult load(int page) {
        return requestHobby(page);
    }

    @Override
    public void setupViews(View view) {
        mTagSelectedView = (TagSelectedView) view.findViewById(R.id.tag_selected_view);
        mTagSelectedView.setVisibility(View.VISIBLE);
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        String hobby = user.getHobby();
        List<String> list = TagText.parseTagList(hobby);
        mTagSelectedView.setTagList(list);
        mTagSelectedView.setSelectedTag(list.get(0));
    }

    /**
     * 找玩伴
     *
     * @param pageIndex
     * @return
     */
    private DiscoveryApiResult requestHobby(int pageIndex) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String hobby = mTagSelectedView.getSelectedTag();
        String center = "30.5,111.2";
        return DiscoveryApi.getHobby(phone, token, center, hobby, pageIndex);
    }

}
