package com.agmbat.imsdk.db;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.imsdk.data.ContactInfo;


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

}
