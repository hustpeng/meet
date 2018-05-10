package com.agmbat.imsdk.db;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 联系人表管理
 */
public class ContactTableManager {

    /**
     * 保存或更新联系人信息
     *
     * @param contactInfo
     */
    public static void saveOrUpdateContact(ContactInfo contactInfo) {
        DbManager db = MeetDatabase.getInstance().getDatabase();
        try {
            ContactInfo exit = db.selector(ContactInfo.class)
                    .where("jid", "in", new String[]{contactInfo.getBareJid()})
                    .findFirst();
            if (exit != null) {
                contactInfo.setId(exit.getId());
            }
            db.saveOrUpdate(contactInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询数据库的list
     *
     * @return
     */
    public static List<ContactGroup> getGroupList() {
        List<ContactGroup> list = new ArrayList<>();
        DbManager db = MeetDatabase.getInstance().getDatabase();
        try {
            List<ContactInfo> contactInfoList = db.selector(ContactInfo.class).findAll();
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
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }
}
