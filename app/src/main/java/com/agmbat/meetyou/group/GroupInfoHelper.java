package com.agmbat.meetyou.group;

import android.content.Context;
import android.content.Intent;

import com.agmbat.imsdk.search.group.GroupInfo;

import java.util.HashMap;
import java.util.Map;

public class GroupInfoHelper {

    private static final Map<String, GroupInfo> GROUP_INFO_MAP = new HashMap<>();

    private static final String KEY_GROUP = "group";

    public static void openGroupDetail(Context context, GroupInfo groupInfo) {
        GROUP_INFO_MAP.put(groupInfo.jid, groupInfo);
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(KEY_GROUP, groupInfo.jid);
        context.startActivity(intent);
    }

    public static GroupInfo getGroupInfo(Intent intent) {
        String jid = intent.getStringExtra(KEY_GROUP);
        return GROUP_INFO_MAP.get(jid);
    }
}
