package com.agmbat.imsdk.group;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {

    private static GroupManager sInstance = new GroupManager();

    public static GroupManager getInstance() {
        return sInstance;
    }

    private List<CircleInfo> mGroupList = new ArrayList<>();

    public CircleInfo getMemCacheGroup(String jid) {
        for (int i = 0; i < mGroupList.size(); i++) {
            CircleInfo groupBean = mGroupList.get(i);
            if (groupBean.getGroupJid().equals(jid)) {
                return groupBean;
            }
        }
        return null;
    }

    public void setMemCacheGroups(List<CircleInfo> groupList) {
        mGroupList = groupList;
    }

}
