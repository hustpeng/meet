package com.agmbat.imsdk.asmack.roster;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.imsdk.asmack.roster.ContactGroup;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.log.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 联系人表管理
 */
public class ContactDBCache {

    /**
     * 先清空原数据, 再保存新的列表
     *
     * @param list
     */
    public static void saveAndClearOldList(List<ContactInfo> list) {
        DbManager db = MeetDatabase.getInstance().getDatabase();
        try {
            db.delete(ContactInfo.class);
            for (ContactInfo contactInfo : list) {
                db.save(contactInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

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
        DbManager db = MeetDatabase.getInstance().getDatabase();
        if (db == null) {
            Debug.printStackTrace();
            return new ArrayList<>();
        }
        try {
            List<ContactInfo> contactInfoList = db.selector(ContactInfo.class).findAll();
            return RosterHelper.toGroupList(contactInfoList);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


}
