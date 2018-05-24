package com.agmbat.imsdk.asmack.roster;

import com.agmbat.imsdk.asmack.api.FetchContactInfoRunnable;
import com.agmbat.imsdk.asmack.api.OnFetchContactListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人工具类
 */
public class RosterHelper {

    /**
     * 将联系人列表转为联系人组
     *
     * @param contactInfoList
     * @return
     */
    public static List<ContactGroup> toGroupList(List<ContactInfo> contactInfoList) {
        List<ContactGroup> list = new ArrayList<>();
        if (contactInfoList != null && contactInfoList.size() > 0) {
            Map<String, ContactGroup> groupMap = new HashMap<>();
            for (ContactInfo contactInfo : contactInfoList) {
                String groupName = contactInfo.getGroupName();
                ContactGroup group = groupMap.get(groupName);
                if (group == null) {
                    group = new ContactGroup();
                    group.setGroupName(groupName);
                    groupMap.put(groupName, group);
                }
                group.addContact(contactInfo);
            }
            list.addAll(groupMap.values());
        }
        return list;
    }

    /**
     * 将联系人组转为联系人列表
     *
     * @param groupList
     * @return
     */
    public static List<ContactInfo> toContactList(List<ContactGroup> groupList) {
        List<ContactInfo> list = new ArrayList<>();
        for (ContactGroup group : groupList) {
            list.addAll(group.getContactList());
        }
        return list;
    }


    /**
     * 由于好友列表信息中获取的信息不全, 需要重新从服务器下拉
     * 此方法不要在读写包线程运行
     *
     * @param jid
     * @return
     */
    public static ContactInfo loadContactInfo(String jid) {
        // 使用集合来包装结果
        final List<ContactInfo> result = new ArrayList<>();
        OnFetchContactListener listener = new OnFetchContactListener() {
            @Override
            public void onFetchContactInfo(ContactInfo contactInfo) {
                result.add(contactInfo);
            }
        };
        Runnable runnable = new FetchContactInfoRunnable(jid, listener);
        runnable.run();
        return result.size() > 0 ? result.get(0) : null;
    }


    /**
     * 查找group
     *
     * @param name
     * @param list
     * @return
     */
    public static ContactGroup findContactGroup(String name, List<ContactGroup> list) {
        for (ContactGroup group : list) {
            if (group.getGroupName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    /**
     * 查找联系人
     *
     * @param jid
     * @param list
     * @return
     */
    public static ContactInfo findContactInfo(String jid, List<ContactInfo> list) {
        for (ContactInfo contactInfo : list) {
            if (contactInfo.getBareJid().equals(jid)) {
                return contactInfo;
            }
        }
        return null;
    }


}
