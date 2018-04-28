package com.agmbat.imsdk.db;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;

/**
 * 好友分组表管理
 */
public class ContactGroupTableManager {

    /**
     * 保存分组信息
     *
     * @param item
     */
    public static void save(ContactGroup item) {
        try {
            DbManager db = MeetDatabase.getInstance().getDatabase();
            db.save(item);
            ContactGroup exist = db.selector(ContactGroup.class)
                    .where("name", "in", new String[]{item.getGroupName()})
                    .findFirst();
            item.setGroupId(exist.getGroupId());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
