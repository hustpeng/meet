package com.agmbat.meetyou.group;

import com.agmbat.imsdk.asmack.XMPPManager;

import java.util.List;

public class GroupManager {

    public static List<GroupTag> requestAllGroupTags(){
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        return GroupApi.getAllGroupTags(phone, token);
    }
}
