package com.agmbat.imsdk.db;

import com.agmbat.db.DbException;

/**
 * 用户和分组的关联表
 */
public class ContactGroupBelongTableManager {

    /**
     * 保存分组信息
     *
     * @param item
     */
    public static void save(ContactGroupBelong item) {
        try {
            MeetDatabase.getInstance().getDatabase().save(item);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
