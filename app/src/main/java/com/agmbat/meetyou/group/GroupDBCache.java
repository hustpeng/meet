package com.agmbat.meetyou.group;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.log.Debug;

import java.util.ArrayList;
import java.util.List;

public class GroupDBCache {

    /**
     * 先清空原数据, 再保存新的列表
     *
     * @param groupTags
     */
    public static void saveGroupCategories(List<GroupCategory> groupTags) {
        DbManager db = MeetDatabase.getInstance().getDatabase();
        if(null == db){
            return;
        }
        try {
            db.delete(GroupCategory.class);
            for (GroupCategory groupTag : groupTags) {
                db.save(groupTag);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询数据库的list
     *
     * @return
     */
    public static List<GroupCategory> getGroupCategories() {
        DbManager db = MeetDatabase.getInstance().getDatabase();
        if (db == null) {
            Debug.printStackTrace();
            return new ArrayList<>();
        }
        try {
            List<GroupCategory> groupTags = db.selector(GroupCategory.class).findAll();
            return groupTags;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
